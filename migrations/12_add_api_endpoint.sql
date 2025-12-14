-- Add api_endpoint to widget_types for external data fetching
ALTER TABLE widget_types ADD COLUMN api_endpoint TEXT;

COMMENT ON COLUMN widget_types.api_endpoint IS 'URL для получения данных из внешнего API (например, https://wakatime.com/api/v1/users/current/stats/last_7_days)';

-- Update existing widget types with their API endpoints
UPDATE widget_types 
SET api_endpoint = 'https://wakatime.com/api/v1/users/current/stats/last_7_days'
WHERE code = 'wakatime_stats';

UPDATE widget_types 
SET api_endpoint = 'https://api.github.com/users/{username}/repos'
WHERE code = 'github';
