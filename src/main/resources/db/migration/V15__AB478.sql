ALTER TABLE IF EXISTS public.subsidy_measure_version
    ADD COLUMN IF NOT EXISTS subsidy_scheme_interest text;