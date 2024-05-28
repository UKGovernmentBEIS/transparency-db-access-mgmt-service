ALTER TABLE IF EXISTS public.award
    ADD COLUMN specific_policy_objective VARCHAR(1500);

ALTER TABLE IF EXISTS public.subsidy_measure
    ADD COLUMN specific_policy_objective VARCHAR(1500);