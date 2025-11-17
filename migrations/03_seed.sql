
-- Users
INSERT INTO users (email, password_hash, two_factor_enabled) VALUES
                                                                 ('artemcrepper@gmail.com', '$argon2id$v=19$m=65536,t=3,p=1$c29tZXNhbHQxMjM0NTY$YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXoxMjM0NTY3ODkwMTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY', false),
                                                                 ('sanspie@akrpov.ru',    '$argon2id$v=19$m=65536,t=3,p=1$YW5vdGhlcnNhbHQxMjM$ZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXoxMjM0NTY3ODkwMTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkw', false)
ON CONFLICT (email) DO NOTHING;

-- Themes
INSERT INTO themes (name, palette, typography) VALUES
                                                   ('Classic', '{"primary":"#1f2937","accent":"#3b82f6","bg":"#ffffff"}', '{"fontFamily":"Inter"}'),
                                                   ('Dark',    '{"primary":"#e5e7eb","accent":"#60a5fa","bg":"#0f172a"}', '{"fontFamily":"Inter"}')
ON CONFLICT (name) DO NOTHING;

-- External services
INSERT INTO external_services (code, name, auth_type) VALUES
                                                          ('github','GitHub','oauth'),
                                                          ('telegram','Telegram','api_key')
ON CONFLICT (code) DO NOTHING;

-- Widget types
INSERT INTO widget_types (code, name, supports_binding, schema_json) VALUES
                                                                         ('about','О себе', false, '{}'::jsonb),
                                                                         ('links','Ссылки', false, '{}'::jsonb),
                                                                         ('image','Изображение', false, '{}'::jsonb),
                                                                         ('markdown','Markdown/HTML', false, '{}'::jsonb),
                                                                         ('github','GitHub', true, '{}'::jsonb),
                                                                         ('projects','Последние проекты', true, '{}'::jsonb),
                                                                         ('skills','Навыки', false, '{}'::jsonb)
ON CONFLICT (code) DO NOTHING;

-- Profiles for each user
WITH u as (SELECT id, email FROM users)
INSERT INTO profiles (user_id, name, slug, privacy, theme_id)
SELECT u.id,
       CASE WHEN u.email='artem.podvorny@example.com' THEN 'Профиль Артём' ELSE 'Профиль Александр' END,
       NULL, 'public',
       (SELECT id FROM themes WHERE name='Classic')
FROM u
ON CONFLICT DO NOTHING;

-- Media
INSERT INTO media_assets (user_id, filename, storage_key, content_type, size_bytes, width, height, alt_text)
SELECT u.id, 'avatar.png', 'users/'||u.id||'/avatar.png', 'image/png', 12345, 512, 512, 'Аватар'
FROM users u
ON CONFLICT DO NOTHING;

-- Link avatars to profiles
INSERT INTO profile_media (profile_id, media_id, role, is_primary)
SELECT p.id, m.id, 'avatar', true
FROM profiles p
         JOIN media_assets m ON m.user_id = p.user_id
ON CONFLICT DO NOTHING;

-- Connections (GitHub for each user)
WITH u AS (SELECT id, email FROM users)
INSERT INTO service_connections(user_id, service_id, external_user_id, access_token)
SELECT u.id, s.id, split_part(u.email,'@',1), 'token-'||left(u.id::text,8)
FROM u JOIN external_services s ON s.code='github'
ON CONFLICT DO NOTHING;

-- Widgets
WITH p as (SELECT id FROM profiles)
SELECT add_widget(p.id, 'about', 'Обо мне', '{"text":"Привет! Это мой открытый профиль."}', '{"x":0,"y":0,"w":4,"h":3}')
FROM p;
WITH p as (SELECT id FROM profiles)
SELECT add_widget(p.id, 'links', 'Ссылки', '{"items":[{"label":"GitHub","url":"https://github.com/user"}]}', '{"x":4,"y":0,"w":4,"h":3}')
FROM p;
WITH p as (SELECT id FROM profiles)
SELECT add_widget(p.id, 'github', 'GitHub', '{"repos":["itmo-2025-demo"]}', '{"x":0,"y":3,"w":8,"h":4}')
FROM p;

-- Bind github widgets
WITH w AS (
    SELECT w.id, w.profile_id
    FROM profile_widgets w
             JOIN widget_types wt ON wt.id = w.widget_type_id AND wt.code='github'
),
     c AS (
         SELECT sc.id, p.id as profile_id
         FROM service_connections sc
                  JOIN profiles p ON p.user_id = sc.user_id
     )
INSERT INTO widget_bindings(profile_widget_id, connection_id)
SELECT w.id, c.id FROM w JOIN c ON c.profile_id = w.profile_id
ON CONFLICT DO NOTHING;

-- Publish all profiles
DO $$ DECLARE r record; BEGIN
    FOR r IN SELECT id FROM profiles LOOP
            PERFORM publish_profile(r.id);
        END LOOP;
END $$;