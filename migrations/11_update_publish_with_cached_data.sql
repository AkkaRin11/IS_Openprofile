-- Update publish_profile function to include cached_data in snapshot
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
                                                       'layout', w.layout,
                                                       'data', w.cached_data
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
