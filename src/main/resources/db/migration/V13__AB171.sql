ALTER TABLE IF EXISTS public.award
    ADD COLUMN IF NOT EXISTS subsidy_award_interest text;

ALTER TABLE IF EXISTS public.subsidy_measure
    ADD COLUMN IF NOT EXISTS subsidy_scheme_interest text;