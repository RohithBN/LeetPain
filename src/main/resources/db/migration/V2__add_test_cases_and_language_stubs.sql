ALTER TABLE problems
    ADD COLUMN test_cases JSONB NOT NULL DEFAULT '[]'::jsonb;

ALTER TABLE problems
    ADD COLUMN language_stubs JSONB NOT NULL DEFAULT '{}'::jsonb;