-- Create a role type enum for better data integrity
CREATE TYPE agent_role AS enum (
    'ADMIN',
    'AGENT'
);

-- Add role column to AGENT table with proper enum default
ALTER TABLE AGENT ADD COLUMN role agent_role NOT NULL DEFAULT 'AGENT'::agent_role;