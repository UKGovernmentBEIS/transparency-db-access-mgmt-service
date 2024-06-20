
-- Update spending region column to remove character restriction
ALTER TABLE public.award ALTER COLUMN spending_region TYPE text;

-- Update all existing spending regions to convert to JSON list
UPDATE AWARD SET SPENDING_REGION = '["' || SPENDING_REGION || '"]';