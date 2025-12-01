-- Create/update a connection by service code
CREATE OR REPLACE FUNCTION connect_service(
    p_user_id uuid,
    p_service_code text,
    p_external_user_id text,
    p_access_token text DEFAULT NULL,
    p_refresh_token text DEFAULT NULL,
    p_token_expires_at timestamptz DEFAULT NULL
) RETURNS uuid LANGUAGE plpgsql AS $$
DECLARE
    v_service_id uuid;
    v_conn_id uuid;
BEGIN
    SELECT id INTO v_service_id FROM external_services WHERE code = p_service_code;
    IF v_service_id IS NULL THEN
        RAISE EXCEPTION 'Unknown service code: %', p_service_code;
    END IF;

    INSERT INTO service_connections(user_id, service_id, external_user_id, access_token, refresh_token, token_expires_at)
    VALUES (p_user_id, v_service_id, p_external_user_id, p_access_token, p_refresh_token, p_token_expires_at)
    ON CONFLICT (user_id, service_id, external_user_id)
        DO UPDATE SET access_token = EXCLUDED.access_token,
                      refresh_token = EXCLUDED.refresh_token,
                      token_expires_at = EXCLUDED.token_expires_at,
                      updated_at = now()
    RETURNING id INTO v_conn_id;

    RETURN v_conn_id;
END $$;

-- Add a widget instance to a profile by widget code
CREATE OR REPLACE FUNCTION add_widget(
    p_profile_id uuid,
    p_widget_code text,
    p_title text DEFAULT NULL,
    p_settings jsonb DEFAULT '{}'::jsonb,
    p_layout jsonb DEFAULT '{}'::jsonb
) RETURNS uuid LANGUAGE plpgsql AS $$
DECLARE
    v_widget_type_id uuid;
    v_next_pos int;
    v_id uuid;
BEGIN
    SELECT id INTO v_widget_type_id FROM widget_types WHERE code = p_widget_code;
    IF v_widget_type_id IS NULL THEN
        RAISE EXCEPTION 'Unknown widget type: %', p_widget_code;
    END IF;

    SELECT coalesce(max(position), -1) + 1 INTO v_next_pos
    FROM profile_widgets WHERE profile_id = p_profile_id;

    INSERT INTO profile_widgets(profile_id, widget_type_id, title, settings, layout, position)
    VALUES (p_profile_id, v_widget_type_id, p_title, p_settings, p_layout, v_next_pos)
    RETURNING id INTO v_id;

    RETURN v_id;
END $$;

-- Bind a widget instance to a service connection
CREATE OR REPLACE FUNCTION bind_widget_to_connection(
    p_widget_id uuid,
    p_connection_id uuid
) RETURNS void LANGUAGE plpgsql AS $$
DECLARE
    v_supports_binding boolean;
    v_widget_user_id uuid;
    v_conn_user_id uuid;
BEGIN
    SELECT wt.supports_binding, p.user_id
    INTO v_supports_binding, v_widget_user_id
    FROM profile_widgets pw
             JOIN profiles p ON p.id = pw.profile_id
             JOIN widget_types wt ON wt.id = pw.widget_type_id
    WHERE pw.id = p_widget_id;

    IF NOT v_supports_binding THEN
        RAISE EXCEPTION 'Widget type does not support binding';
    END IF;

    SELECT user_id INTO v_conn_user_id
    FROM service_connections
    WHERE id = p_connection_id;

    IF v_widget_user_id != v_conn_user_id THEN
        RAISE EXCEPTION 'Connection does not belong to profile owner';
    END IF;

    INSERT INTO widget_bindings(profile_widget_id, connection_id)
    VALUES (p_widget_id, p_connection_id)
    ON CONFLICT DO NOTHING;
END $$;

-- Build snapshot and publish profile (returns slug)
CREATE OR REPLACE FUNCTION publish_profile(p_profile_id uuid)
    RETURNS text LANGUAGE plpgsql AS $$
DECLARE
    v_version int;
    v_slug text;
    v_pub_id uuid;
    v_snapshot jsonb;
    v_privacy text;
BEGIN
    SELECT slug, privacy INTO v_slug, v_privacy
    FROM profiles
    WHERE id = p_profile_id;

    IF v_slug IS NULL THEN
        RAISE EXCEPTION 'Profile not found: %', p_profile_id;
    END IF;

    IF v_privacy = 'private' THEN
        RAISE EXCEPTION 'Cannot publish private profile';
    END IF;

    SELECT jsonb_build_object(
                   'profile', jsonb_build_object(
                    'name', p.name,
                    'slug', p.slug,
                    'privacy', p.privacy,
                    'theme', COALESCE(
                            (SELECT jsonb_build_object(
                                            'name', t.name,
                                            'palette', t.palette,
                                            'typography', t.typography
                                    )
                             FROM themes t WHERE t.id = p.theme_id),
                            '{}'::jsonb
                             )
                              ),
                   'widgets', (
                       SELECT coalesce(jsonb_agg(
                                               jsonb_build_object(
                                                       'id', w.id,
                                                       'type', (SELECT code FROM widget_types wt WHERE wt.id = w.widget_type_id),
                                                       'title', w.title,
                                                       'settings', w.settings,
                                                       'layout', w.layout
                                               ) ORDER BY w.position
                                       ), '[]'::jsonb)
                       FROM profile_widgets w WHERE w.profile_id = p.id
                   ),
                   'media', (
                       SELECT coalesce(jsonb_agg(
                                               jsonb_build_object(
                                                       'media_id', m.id,
                                                       'role', pm.role,
                                                       'is_primary', pm.is_primary,
                                                       'path', m.storage_key,
                                                       'content_type', m.content_type,
                                                       'alt', m.alt_text
                                               )
                                       ), '[]'::jsonb)
                       FROM profile_media pm
                                JOIN media_assets m ON m.id = pm.media_id
                       WHERE pm.profile_id = p.id
                   )
           ) INTO v_snapshot
    FROM profiles p WHERE p.id = p_profile_id;

    SELECT coalesce(max(version),0)+1 INTO v_version
    FROM publications
    WHERE profile_id = p_profile_id;

    INSERT INTO publications(profile_id, status, is_active, version, snapshot, published_at)
    VALUES (p_profile_id, 'published', true, v_version, v_snapshot, now())
    RETURNING id INTO v_pub_id;

    RETURN v_slug;
END $$;

CREATE OR REPLACE FUNCTION validate_publication()
    RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
    IF NEW.status = 'published' AND NEW.published_at IS NULL THEN
        NEW.published_at := now();
    END IF;

    IF NEW.status != 'published' AND NEW.is_active THEN
        RAISE EXCEPTION 'Only published profiles can be active';
    END IF;

    RETURN NEW;
END $$;

CREATE TRIGGER trg_publications_validate
    BEFORE INSERT OR UPDATE ON publications
    FOR EACH ROW EXECUTE FUNCTION validate_publication();

-- Resolve public profile by slug
CREATE OR REPLACE FUNCTION get_public_profile(p_slug text)
    RETURNS jsonb LANGUAGE sql AS $$
SELECT vp.snapshot
FROM v_public_profiles vp
WHERE vp.slug = slugify(p_slug)
$$;