/*
 * Database Initialization
 * This is the main entry point for setting up the entire database.
 * Files are executed in order to maintain dependencies and data integrity.
 */

-- Drop existing tables
\echo Dropping existing tables...
DROP TABLE IF EXISTS "BookReservation" CASCADE;
DROP TABLE IF EXISTS "BookIssue" CASCADE;
DROP TABLE IF EXISTS "BookMark" CASCADE;
DROP TABLE IF EXISTS "Comments" CASCADE;
-- Removed Reports table drop statement
DROP TABLE IF EXISTS "Users" CASCADE;
DROP TABLE IF EXISTS "Admins" CASCADE;
DROP TABLE IF EXISTS "Members" CASCADE;
DROP TABLE IF EXISTS "BookItem" CASCADE;
DROP TABLE IF EXISTS "Books_Authors" CASCADE;
DROP TABLE IF EXISTS "Books_Category" CASCADE;
DROP TABLE IF EXISTS "Books" CASCADE;
DROP TABLE IF EXISTS "Authors" CASCADE;
DROP TABLE IF EXISTS "Category" CASCADE;

-- Drop existing enum types
\echo Dropping existing types...
DROP TYPE IF EXISTS book_status CASCADE;
DROP TYPE IF EXISTS book_item_status CASCADE;
DROP TYPE IF EXISTS gender_type CASCADE;
DROP TYPE IF EXISTS account_status CASCADE;
DROP TYPE IF EXISTS reservation_status CASCADE;
DROP TYPE IF EXISTS book_issue_status CASCADE;
-- Removed report_status enum type drop

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
