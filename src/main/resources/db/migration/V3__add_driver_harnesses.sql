ALTER TABLE problems
    ADD COLUMN driver_harnesses JSONB NOT NULL DEFAULT '{}'::jsonb;
