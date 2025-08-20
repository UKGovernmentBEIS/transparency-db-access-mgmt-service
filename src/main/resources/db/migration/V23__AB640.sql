ALTER TABLE IF EXISTS public.award
    ADD COLUMN IF NOT EXISTS standalone_award_title VARCHAR(255);