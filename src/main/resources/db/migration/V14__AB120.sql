ALTER TABLE IF EXISTS public.subsidy_measure
    ADD COLUMN IF NOT EXISTS purpose VARCHAR(5000);