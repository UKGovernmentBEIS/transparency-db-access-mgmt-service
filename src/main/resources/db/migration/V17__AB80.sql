ALTER TABLE IF EXISTS public.subsidy_measure
    ADD COLUMN IF NOT EXISTS reason VARCHAR(1000);

ALTER TABLE IF EXISTS public.subsidy_measure_version
    ADD COLUMN IF NOT EXISTS reason VARCHAR(1000);