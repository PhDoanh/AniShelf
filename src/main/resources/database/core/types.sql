-- This file contains the SQL commands to create custom ENUM types for the database.

-- Create custom ENUM types first
CREATE TYPE book_status AS ENUM ('AVAILABLE', 'UNAVAILABLE');
CREATE TYPE book_item_status AS ENUM ('AVAILABLE', 'RESERVED', 'LOANED', 'LOST');
CREATE TYPE gender_type AS ENUM ('MALE', 'FEMALE', 'OTHER');
CREATE TYPE account_status AS ENUM ('ACTIVE', 'BLACKLISTED', 'CLOSED', 'NONE');
CREATE TYPE reservation_status AS ENUM ('WAITING', 'COMPLETED', 'CANCELED');
CREATE TYPE book_issue_status AS ENUM ('BORROWED', 'RETURNED', 'LOST');
CREATE TYPE report_status AS ENUM ('PENDING', 'RESOLVED');