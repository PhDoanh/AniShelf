/*
 * Sample Data
 * This file contains mock data for testing and development purposes.
 * The data includes sample books, authors, categories, members, and their interactions.
 */

-- Clear existing data
TRUNCATE "BookReservation", "BookIssue", "BookMark", "Comments", "Reports",
         "Users", "Admins", "Members",
         "BookItem", "Books_Authors", "Books_Category", "Books",
         "Authors", "Category"
RESTART IDENTITY CASCADE;

-- Insert Categories
INSERT INTO "Category" ("category_name") VALUES
    ('Manga'),
    ('Light Novel'),
    ('Comic'),
    ('Art Book'),
    ('Magazine');

-- Insert Authors
INSERT INTO "Authors" ("author_name") VALUES
    ('Eiichiro Oda'),
    ('Masashi Kishimoto'),
    ('Akira Toriyama'),
    ('Kentaro Miura'),
    ('Naoko Takeuchi');

-- Insert Books
INSERT INTO "Books" ("ISBN", "title", "description", "placeAt") VALUES
    (9784088725093, 'One Piece Volume 1', 'The beginning of the great pirate era', 'Shelf A-1'),
    (9784088725094, 'One Piece Volume 2', 'Luffy meets Zoro', 'Shelf A-1'),
    (9784088591902, 'Naruto Volume 1', 'A young ninja seeks recognition', 'Shelf B-2'),
    (9784088591903, 'Dragon Ball Volume 1', 'The journey of Son Goku begins', 'Shelf C-3'),
    (9784088591904, 'Berserk Volume 1', 'Dark fantasy epic begins', 'Shelf D-4');

-- Link Books and Authors
INSERT INTO "Books_Authors" VALUES
    (9784088725093, 1),
    (9784088725094, 1),
    (9784088591902, 2),
    (9784088591903, 3),
    (9784088591904, 4);

-- Link Books and Categories
INSERT INTO "Books_Category" VALUES
    (9784088725093, 1),
    (9784088725094, 1),
    (9784088591902, 1),
    (9784088591903, 1),
    (9784088591904, 1);

-- Insert Book Items
INSERT INTO "BookItem" ("ISBN", "BookItemStatus") VALUES
    (9784088725093, 'AVAILABLE'::book_item_status),
    (9784088725093, 'AVAILABLE'::book_item_status),
    (9784088725094, 'AVAILABLE'::book_item_status),
    (9784088591902, 'AVAILABLE'::book_item_status),
    (9784088591903, 'AVAILABLE'::book_item_status),
    (9784088591904, 'AVAILABLE'::book_item_status);

-- Insert Members
INSERT INTO "Members" ("first_name", "last_name", "birth_date", "gender", "email", "phone", "role") VALUES
    ('John', 'Doe', '2000-01-01', 'MALE'::gender_type, 'john.doe@email.com', '0123456789', 'ADMIN'),
    ('Jane', 'Smith', '2001-02-02', 'FEMALE'::gender_type, 'jane.smith@email.com', '0123456788', 'NONE'),
    ('Bob', 'Johnson', '1999-03-03', 'MALE'::gender_type, 'bob.johnson@email.com', '0123456787', 'NONE');

-- Insert Users
INSERT INTO "Users" ("username", "password", "AccountStatus", "member_ID") VALUES
    ('johndoe', '$2a$10$abcdefghijklmnopqrstuvwxyz123456', 'ACTIVE'::account_status, 1),
    ('janesmith', '$2a$10$abcdefghijklmnopqrstuvwxyz123456', 'ACTIVE'::account_status, 2),
    ('bobjohnson', '$2a$10$abcdefghijklmnopqrstuvwxyz123456', 'ACTIVE'::account_status, 3);

-- Insert Admin
INSERT INTO "Admins" ("username", "password", "member_ID") VALUES
    ('admin', '$2a$10$abcdefghijklmnopqrstuvwxyz123456', 1);

-- Insert some book reservations
INSERT INTO "BookReservation" ("member_ID", "barcode", "creation_date", "due_date", "BookReservationStatus") VALUES
    (2, 1, CURRENT_DATE, CURRENT_DATE + INTERVAL '7 days', 'WAITING'::reservation_status);

-- Insert some book issues
INSERT INTO "BookIssue" ("member_ID", "barcode", "creation_date", "due_date", "BookIssueStatus") VALUES
    (3, 2, CURRENT_DATE - INTERVAL '7 days', CURRENT_DATE + INTERVAL '7 days', 'BORROWED'::book_issue_status);

-- Insert some bookmarks
INSERT INTO "BookMark" VALUES
    (2, 9784088725093),
    (3, 9784088591902);

-- Insert some comments
INSERT INTO "Comments" ("member_ID", "ISBN", "title", "content", "rate") VALUES
    (2, 9784088725093, 'Great manga!', 'One of the best manga series ever!', '5'),
    (3, 9784088591902, 'Classic', 'A classic ninja story', '4');

-- Insert some reports
INSERT INTO "Reports" ("member_ID", "title", "content", "ReportStatus") VALUES
    (2, 'Damaged Book', 'Found torn pages in One Piece Volume 1', 'PENDING'::report_status);

