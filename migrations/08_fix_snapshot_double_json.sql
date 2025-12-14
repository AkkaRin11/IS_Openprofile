UPDATE publications
SET snapshot = (snapshot #>> '{}')::jsonb
WHERE jsonb_typeof(snapshot) = 'string';
