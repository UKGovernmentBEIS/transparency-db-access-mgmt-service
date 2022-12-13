ALTER TABLE IF EXISTS public.subsidy_measure
    ADD COLUMN has_no_end_date boolean NOT NULL DEFAULT false;

ALTER TABLE IF EXISTS public.subsidy_measure
    ALTER COLUMN end_date DROP NOT NULL;