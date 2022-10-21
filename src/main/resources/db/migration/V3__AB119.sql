ALTER TABLE IF EXISTS public.subsidy_measure
    ADD COLUMN IF NOT EXISTS spending_sectors json;