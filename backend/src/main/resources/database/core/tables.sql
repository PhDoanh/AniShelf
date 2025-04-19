/*
 * Table Definitions
 * This file contains all table creation SQL statements for the library management system.
 * Tables are created in order to maintain referential integrity.
 */

-- Core tables
CREATE TABLE IF NOT EXISTS "Books" (
    "ISBN" BIGINT NOT NULL,
    "title" VARCHAR(1000) NOT NULL,
    "image_path" VARCHAR(1000) NOT NULL DEFAULT 'bookImage/default.png',
    "description" TEXT,
    "placeAt" VARCHAR(250),
    "preview" VARCHAR(250) DEFAULT NULL,
    "quantity" INTEGER NOT NULL DEFAULT 0,
    "number_lost_book" INTEGER NOT NULL DEFAULT 0,
    "number_loaned_book" INTEGER NOT NULL DEFAULT 0,
    "number_reserved_book" INTEGER NOT NULL DEFAULT 0,
    "rate" INTEGER NOT NULL DEFAULT 5,
    "BookStatus" book_status NOT NULL DEFAULT 'AVAILABLE',
    "added_at_timestamp" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_books PRIMARY KEY ("ISBN")
);

CREATE TABLE IF NOT EXISTS "Authors" (
    "author_ID" SERIAL PRIMARY KEY,
    "author_name" VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS "Category" (
    "category_ID" SERIAL PRIMARY KEY,
    "category_name" VARCHAR(1000) NOT NULL
);

-- Junction tables
CREATE TABLE IF NOT EXISTS "Books_Authors" (
    "ISBN" BIGINT NOT NULL,
    "author_ID" INTEGER NOT NULL,
    CONSTRAINT pk_books_authors PRIMARY KEY ("ISBN", "author_ID"),
    CONSTRAINT fk_books_authors_isbn FOREIGN KEY ("ISBN") REFERENCES "Books" ("ISBN") ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_books_authors_author FOREIGN KEY ("author_ID") REFERENCES "Authors" ("author_ID") ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS "Books_Category" (
    "ISBN" BIGINT NOT NULL,
    "category_ID" INTEGER NOT NULL,
    CONSTRAINT pk_books_category PRIMARY KEY ("ISBN", "category_ID"),
    CONSTRAINT fk_books_category_isbn FOREIGN KEY ("ISBN") REFERENCES "Books" ("ISBN") ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_books_category_category FOREIGN KEY ("category_ID") REFERENCES "Category" ("category_ID") ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS "BookItem" (
    "barcode" SERIAL PRIMARY KEY,
    "ISBN" BIGINT NOT NULL,
    "BookItemStatus" book_item_status NOT NULL DEFAULT 'AVAILABLE',
    "note" VARCHAR(1000),
    CONSTRAINT fk_bookitem_isbn FOREIGN KEY ("ISBN") REFERENCES "Books" ("ISBN") ON UPDATE CASCADE ON DELETE CASCADE
);

-- User management tables
CREATE TABLE IF NOT EXISTS "Members" (
    "member_ID" SERIAL PRIMARY KEY,
    "image_path" VARCHAR(1000) NOT NULL DEFAULT 'avatar/default.png',
    "first_name" VARCHAR(50) NOT NULL,
    "last_name" VARCHAR(50) NOT NULL,
    "birth_date" DATE NOT NULL,
    "gender" gender_type NOT NULL,
    "email" VARCHAR(250) NOT NULL UNIQUE,
    "phone" VARCHAR(10) NOT NULL UNIQUE,
    "role" VARCHAR(10) NOT NULL DEFAULT 'NONE' CHECK ("role" IN ('ADMIN', 'NONE'))
);

CREATE TABLE IF NOT EXISTS "Users" (
    "user_ID" SERIAL PRIMARY KEY,
    "username" VARCHAR(250),
    "password" VARCHAR(250),
    "AccountStatus" account_status NOT NULL,
    "member_ID" INTEGER NOT NULL,
    "otp" VARCHAR(6) DEFAULT NULL,
    "otp_expiry" TIMESTAMP DEFAULT NULL,
    "added_at_timestamp" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_users_member FOREIGN KEY ("member_ID") REFERENCES "Members" ("member_ID") ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS "Admins" (
    "admin_ID" SERIAL PRIMARY KEY,
    "username" VARCHAR(250),
    "password" VARCHAR(250),
    "member_ID" INTEGER NOT NULL,
    "added_at_timestamp" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_admins_member FOREIGN KEY ("member_ID") REFERENCES "Members" ("member_ID") ON UPDATE CASCADE ON DELETE CASCADE
);

-- Transaction tables
CREATE TABLE IF NOT EXISTS "BookReservation" (
    "reservation_ID" SERIAL PRIMARY KEY,
    "member_ID" INTEGER NOT NULL,
    "barcode" INTEGER NOT NULL,
    "creation_date" DATE NOT NULL,
    "due_date" DATE NOT NULL,
    "BookReservationStatus" reservation_status NOT NULL DEFAULT 'WAITING',
    CONSTRAINT fk_bookreservation_barcode FOREIGN KEY ("barcode") REFERENCES "BookItem" ("barcode") ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_bookreservation_member FOREIGN KEY ("member_ID") REFERENCES "Members" ("member_ID") ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS "BookIssue" (
    "issue_ID" SERIAL PRIMARY KEY,
    "member_ID" INTEGER NOT NULL,
    "barcode" INTEGER NOT NULL,
    "creation_date" DATE NOT NULL,
    "due_date" DATE NOT NULL,
    "return_date" DATE,
    "BookIssueStatus" book_issue_status NOT NULL DEFAULT 'BORROWED',
    CONSTRAINT fk_bookissue_barcode FOREIGN KEY ("barcode") REFERENCES "BookItem" ("barcode") ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_bookissue_member FOREIGN KEY ("member_ID") REFERENCES "Members" ("member_ID") ON UPDATE CASCADE ON DELETE CASCADE
);

-- Interaction tables
CREATE TABLE IF NOT EXISTS "BookMark" (
    "member_ID" INTEGER NOT NULL,
    "ISBN" BIGINT NOT NULL,
    CONSTRAINT pk_bookmark PRIMARY KEY ("member_ID", "ISBN"),
    CONSTRAINT fk_bookmark_member FOREIGN KEY ("member_ID") REFERENCES "Members" ("member_ID") ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_bookmark_isbn FOREIGN KEY ("ISBN") REFERENCES "Books" ("ISBN") ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS "Comments" (
    "comment_ID" SERIAL PRIMARY KEY,
    "member_ID" INTEGER NOT NULL,
    "ISBN" BIGINT NOT NULL,
    "title" TEXT,
    "content" TEXT NOT NULL,
    "rate" VARCHAR(1) NOT NULL DEFAULT '5' CHECK ("rate" IN ('0', '1', '2', '3', '4', '5')),
    CONSTRAINT fk_comments_member FOREIGN KEY ("member_ID") REFERENCES "Members" ("member_ID") ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_comments_isbn FOREIGN KEY ("ISBN") REFERENCES "Books" ("ISBN") ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS "Reports" (
    "report_ID" SERIAL PRIMARY KEY,
    "member_ID" INTEGER NOT NULL,
    "title" TEXT,
    "content" TEXT NOT NULL,
    "ReportStatus" report_status NOT NULL DEFAULT 'PENDING',
    CONSTRAINT fk_reports_member FOREIGN KEY ("member_ID") REFERENCES "Members" ("member_ID") ON UPDATE CASCADE ON DELETE CASCADE
);