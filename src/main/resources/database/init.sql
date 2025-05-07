/*
 * Database Initialization
 * This is the main entry point for setting up the entire database.
 * Files are executed in order to maintain dependencies and data integrity.
 */

-- Create custom types first
\i core/types.sql

-- Create tables and their constraints
\i core/tables.sql

-- Create all necessary indexes
\i indexes/indexes.sql

-- Create triggers and functions
\i triggers/triggers.sql

-- Load sample data (comment out in production)
\i mock_data/sample_data.sql
