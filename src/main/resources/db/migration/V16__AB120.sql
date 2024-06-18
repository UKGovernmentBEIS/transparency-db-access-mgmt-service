ALTER TABLE IF EXISTS public.subsidy_measure
    ADD COLUMN IF NOT EXISTS purpose VARCHAR(5000);

ALTER TABLE IF EXISTS public.subsidy_measure_version
    ADD COLUMN IF NOT EXISTS purpose VARCHAR(5000);