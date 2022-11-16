ALTER TABLE IF EXISTS public.award
    ALTER COLUMN sc_number DROP NOT NULL;

ALTER TABLE IF EXISTS public.award
    ADD COLUMN standalone_award character varying(3) DEFAULT 'No';

ALTER TABLE IF EXISTS public.award
    ADD COLUMN subsidy_award_description VARCHAR(2000);
