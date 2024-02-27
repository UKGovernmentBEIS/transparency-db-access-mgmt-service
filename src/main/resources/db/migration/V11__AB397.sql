ALTER TABLE IF EXISTS public.award
    ALTER COLUMN subsidy_award_description TYPE VARCHAR(8000);

ALTER TABLE IF EXISTS public.subsidy_measure
    ALTER COLUMN subsidy_scheme_description TYPE VARCHAR(8000);