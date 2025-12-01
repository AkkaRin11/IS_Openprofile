ALTER TABLE users ADD COLUMN IF NOT EXISTS email_verified boolean NOT NULL DEFAULT false;

CREATE INDEX IF NOT EXISTS idx_users_email_verified ON users(email_verified);