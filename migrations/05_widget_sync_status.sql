CREATE TABLE IF NOT EXISTS widget_sync_status (
    id                uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    widget_id         uuid NOT NULL REFERENCES profile_widgets(id) ON DELETE CASCADE,
    last_sync_at      timestamptz,
    sync_status       text NOT NULL,
    error_message     text,
    retry_count       int NOT NULL DEFAULT 0,
    next_sync_at      timestamptz,
    created_at        timestamptz NOT NULL DEFAULT now(),
    updated_at        timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT fk_sync_widget FOREIGN KEY (widget_id) REFERENCES profile_widgets(id) ON DELETE CASCADE,
    UNIQUE (widget_id)
);

CREATE INDEX IF NOT EXISTS idx_widget_sync_widget ON widget_sync_status(widget_id);
CREATE INDEX IF NOT EXISTS idx_widget_sync_next_sync ON widget_sync_status(next_sync_at);
CREATE INDEX IF NOT EXISTS idx_widget_sync_status ON widget_sync_status(sync_status);

CREATE OR REPLACE FUNCTION set_widget_sync_updated_at()
    RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN NEW.updated_at := now(); RETURN NEW; END $$;

CREATE TRIGGER trg_widget_sync_updated
    BEFORE UPDATE ON widget_sync_status
    FOR EACH ROW EXECUTE FUNCTION set_widget_sync_updated_at();