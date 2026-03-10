-- Add like_count and dislike_count columns to contents table
ALTER TABLE contents ADD COLUMN like_count INTEGER DEFAULT 0 NOT NULL;
ALTER TABLE contents ADD COLUMN dislike_count INTEGER DEFAULT 0 NOT NULL;