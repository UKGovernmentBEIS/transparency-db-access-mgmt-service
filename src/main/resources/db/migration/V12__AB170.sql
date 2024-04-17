ALTER TABLE IF EXISTS public.award
    ADD COLUMN authority_url VARCHAR(500);
ALTER TABLE IF EXISTS public.award
    ADD COLUMN authority_url_description VARCHAR(255);