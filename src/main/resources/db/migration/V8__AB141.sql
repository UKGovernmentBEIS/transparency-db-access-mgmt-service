ALTER TABLE IF EXISTS public.subsidy_measure
    ADD COLUMN IF NOT EXISTS maximum_amount_under_scheme VARCHAR(255);