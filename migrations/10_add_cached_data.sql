-- Add cached_data column to profile_widgets for storing synced widget data
ALTER TABLE profile_widgets ADD COLUMN cached_data JSONB;

COMMENT ON COLUMN profile_widgets.cached_data IS 'Кэшированные данные из внешнего API (WakaTime, GitHub и т.д.)';
