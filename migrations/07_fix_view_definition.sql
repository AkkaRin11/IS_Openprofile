CREATE OR REPLACE VIEW v_public_profiles AS
SELECT pr.slug, pub.profile_id, pub.id AS publication_id, pub.published_at, pub.snapshot
FROM publications pub
         JOIN profiles pr ON pr.id = pub.profile_id
WHERE pub.is_active AND pub.status='published';
