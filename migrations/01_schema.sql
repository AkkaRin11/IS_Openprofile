CREATE EXTENSION IF NOT EXISTS pgcrypto;   -- gen_random_uuid()
CREATE EXTENSION IF NOT EXISTS unaccent;   -- slug normalization

-- domain constraints
DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'privacy_level') THEN
        CREATE TYPE privacy_level AS ENUM ('public','unlisted','private');
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'publication_status') THEN
        CREATE TYPE publication_status AS ENUM ('draft','published','archived');
    END IF;
END $$;

-- Helper triggers
CREATE OR REPLACE FUNCTION set_updated_at()
    RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN NEW.updated_at := now(); RETURN NEW; END $$;

CREATE OR REPLACE FUNCTION normalize_email()
    RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN IF NEW.email IS NOT NULL THEN NEW.email := lower(trim(NEW.email)); END IF; RETURN NEW; END $$;

CREATE OR REPLACE FUNCTION slugify(src text)
    RETURNS text LANGUAGE plpgsql AS $$
DECLARE s text;
BEGIN
    s := lower(unaccent(coalesce(src,'')));
    s := regexp_replace(s, '[^a-z0-9]+', '-', 'g');
    s := regexp_replace(s, '(^-+|-+$)', '', 'g');
    IF s = '' THEN s := substr(encode(gen_random_bytes(6),'hex'),1,8); END IF;
    RETURN s;
END $$;

CREATE TABLE IF NOT EXISTS users (
                                     id                uuid PRIMARY KEY DEFAULT gen_random_uuid(),
                                     email             text NOT NULL UNIQUE,
                                     password_hash     text NOT NULL,
                                     two_factor_enabled boolean NOT NULL DEFAULT false,
                                     created_at        timestamptz NOT NULL DEFAULT now(),
                                     updated_at        timestamptz NOT NULL DEFAULT now(),
                                     CONSTRAINT chk_pw_len CHECK (length(password_hash) >= 60)
);
CREATE TRIGGER trg_users_updated BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();
CREATE TRIGGER trg_users_email_norm BEFORE INSERT OR UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION normalize_email();

CREATE TABLE IF NOT EXISTS themes (
                                      id          uuid PRIMARY KEY DEFAULT gen_random_uuid(),
                                      name        text NOT NULL UNIQUE,
                                      palette     jsonb NOT NULL DEFAULT '{}'::jsonb,
                                      typography  jsonb NOT NULL DEFAULT '{}'::jsonb,
                                      created_at  timestamptz NOT NULL DEFAULT now(),
                                      updated_at  timestamptz NOT NULL DEFAULT now()
);
CREATE TRIGGER trg_themes_updated BEFORE UPDATE ON themes
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TABLE IF NOT EXISTS profiles (
                                        id          uuid PRIMARY KEY DEFAULT gen_random_uuid(),
                                        user_id     uuid NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                        name        text NOT NULL,
                                        slug        text NOT NULL UNIQUE,
                                        privacy     privacy_level NOT NULL DEFAULT 'public',
                                        theme_id    uuid REFERENCES themes(id),
                                        created_at  timestamptz NOT NULL DEFAULT now(),
                                        updated_at  timestamptz NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_profiles_user ON profiles(user_id);
CREATE TRIGGER trg_profiles_updated BEFORE UPDATE ON profiles
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- Auto-slug
CREATE OR REPLACE FUNCTION profiles_autoslug()
    RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
    IF TG_OP='INSERT' THEN
        IF NEW.slug IS NULL OR NEW.slug='' THEN NEW.slug := slugify(NEW.name);
        ELSE NEW.slug := slugify(NEW.slug); END IF;
    ELSE
        IF NEW.slug IS DISTINCT FROM OLD.slug THEN NEW.slug := slugify(NEW.slug); END IF;
    END IF;
    RETURN NEW;
END $$;
CREATE TRIGGER trg_profiles_slug BEFORE INSERT OR UPDATE ON profiles
    FOR EACH ROW EXECUTE FUNCTION profiles_autoslug();

CREATE TABLE IF NOT EXISTS widget_types (
                                            id          uuid PRIMARY KEY DEFAULT gen_random_uuid(),
                                            code        text NOT NULL UNIQUE,
                                            name        text NOT NULL,
                                            supports_binding boolean NOT NULL DEFAULT false,
                                            schema_json jsonb NOT NULL DEFAULT '{}'::jsonb,
                                            created_at  timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS profile_widgets (
                                               id              uuid PRIMARY KEY DEFAULT gen_random_uuid(),
                                               profile_id      uuid NOT NULL REFERENCES profiles(id) ON DELETE CASCADE,
                                               widget_type_id  uuid NOT NULL REFERENCES widget_types(id),
                                               title           text,
                                               settings        jsonb NOT NULL DEFAULT '{}'::jsonb,
                                               layout          jsonb NOT NULL DEFAULT '{}'::jsonb,
                                               position        integer NOT NULL DEFAULT 0,
                                               created_at      timestamptz NOT NULL DEFAULT now(),
                                               updated_at      timestamptz NOT NULL DEFAULT now(),
                                               UNIQUE (profile_id, position)
);
CREATE INDEX IF NOT EXISTS idx_profile_widgets_profile_pos ON profile_widgets(profile_id, position);
CREATE INDEX IF NOT EXISTS idx_profile_widgets_type ON profile_widgets(widget_type_id);
CREATE TRIGGER trg_profile_widgets_updated BEFORE UPDATE ON profile_widgets
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TABLE IF NOT EXISTS external_services (
                                                 id         uuid PRIMARY KEY DEFAULT gen_random_uuid(),
                                                 code       text NOT NULL UNIQUE, -- 'github','telegram', etc.
                                                 name       text NOT NULL,
                                                 auth_type  text NOT NULL DEFAULT 'oauth', -- or 'api_key'
                                                 created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS service_connections (
                                                   id              uuid PRIMARY KEY DEFAULT gen_random_uuid(),
                                                   user_id         uuid NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                                   service_id      uuid NOT NULL REFERENCES external_services(id) ON DELETE CASCADE,
                                                   external_user_id text NOT NULL,
                                                   access_token     text,
                                                   refresh_token    text,
                                                   token_expires_at timestamptz,
                                                   created_at       timestamptz NOT NULL DEFAULT now(),
                                                   updated_at       timestamptz NOT NULL DEFAULT now(),
                                                   UNIQUE (user_id, service_id, external_user_id)
);
CREATE INDEX IF NOT EXISTS idx_connections_user ON service_connections(user_id);
CREATE INDEX IF NOT EXISTS idx_connections_service ON service_connections(service_id);
CREATE TRIGGER trg_connections_updated BEFORE UPDATE ON service_connections
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- M:N: widget <-> connection
CREATE TABLE IF NOT EXISTS widget_bindings (
                                               profile_widget_id uuid NOT NULL REFERENCES profile_widgets(id) ON DELETE CASCADE,
                                               connection_id     uuid NOT NULL REFERENCES service_connections(id) ON DELETE CASCADE,
                                               PRIMARY KEY (profile_widget_id, connection_id)
);
CREATE INDEX IF NOT EXISTS idx_widget_bindings_conn ON widget_bindings(connection_id);

-- Media
CREATE TABLE IF NOT EXISTS media_assets (
                                            id            uuid PRIMARY KEY DEFAULT gen_random_uuid(),
                                            user_id       uuid NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                            filename      text NOT NULL,
                                            storage_key   text NOT NULL,
                                            content_type  text NOT NULL,
                                            size_bytes    bigint NOT NULL CHECK (size_bytes >= 0),
                                            width         int,
                                            height        int,
                                            alt_text      text,
                                            created_at    timestamptz NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_media_user ON media_assets(user_id);

-- M:N: profile <-> media
CREATE TABLE IF NOT EXISTS profile_media (
                                             profile_id   uuid NOT NULL REFERENCES profiles(id) ON DELETE CASCADE,
                                             media_id     uuid NOT NULL REFERENCES media_assets(id) ON DELETE CASCADE,
                                             role         text NOT NULL DEFAULT 'gallery', -- 'avatar','background','gallery'
                                             is_primary   boolean NOT NULL DEFAULT false,
                                             PRIMARY KEY (profile_id, media_id)
);
CREATE INDEX IF NOT EXISTS idx_profile_media_role ON profile_media(profile_id, role);

-- Publications (versioned snapshots)
CREATE TABLE IF NOT EXISTS publications (
                                            id            uuid PRIMARY KEY DEFAULT gen_random_uuid(),
                                            profile_id    uuid NOT NULL REFERENCES profiles(id) ON DELETE CASCADE,
                                            status        publication_status NOT NULL DEFAULT 'draft',
                                            is_active     boolean NOT NULL DEFAULT false, -- max 1 per profile
                                            version       int NOT NULL DEFAULT 1,
                                            snapshot      jsonb NOT NULL DEFAULT '{}'::jsonb,
                                            published_at  timestamptz,
                                            created_at    timestamptz NOT NULL DEFAULT now(),
                                            UNIQUE (profile_id, version)
);
CREATE INDEX IF NOT EXISTS idx_publications_profile ON publications(profile_id);
CREATE INDEX IF NOT EXISTS idx_publications_active ON publications(profile_id) WHERE is_active;

-- Enforce single active publication per profile
CREATE OR REPLACE FUNCTION activate_publication()
    RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
    IF NEW.is_active AND NEW.status='published' THEN
        UPDATE publications p SET is_active=false
        WHERE p.profile_id = NEW.profile_id AND p.id <> NEW.id AND p.is_active;
    END IF;
    RETURN NEW;
END $$;
CREATE TRIGGER trg_publications_active
    AFTER INSERT OR UPDATE ON publications
    FOR EACH ROW EXECUTE FUNCTION activate_publication();

-- View to quickly fetch public snapshot by slug
CREATE OR REPLACE VIEW v_public_profiles AS
SELECT pr.slug, pub.profile_id, pub.id AS publication_id, pub.published_at, pub.snapshot
FROM publications pub
         JOIN profiles pr ON pr.id = pub.profile_id
WHERE pub.is_active AND pub.status='published';