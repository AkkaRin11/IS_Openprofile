CREATE TABLE IF NOT EXISTS verification_tokens (
    id                uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    token             text NOT NULL UNIQUE,
    user_id           uuid NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    expiry_date       timestamptz NOT NULL,
    type              text NOT NULL,
    created_at        timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT fk_token_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_verification_tokens_token ON verification_tokens(token);
CREATE INDEX IF NOT EXISTS idx_verification_tokens_user ON verification_tokens(user_id);
CREATE INDEX IF NOT EXISTS idx_verification_tokens_expiry ON verification_tokens(expiry_date);