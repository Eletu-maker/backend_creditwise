-- Add plan_status column to client_profiles table
ALTER TABLE client_profiles ADD COLUMN plan_status VARCHAR(20) DEFAULT 'PENDING' NOT NULL;