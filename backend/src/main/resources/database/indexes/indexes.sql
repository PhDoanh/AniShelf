/*
 * Index Definitions
 * This file contains all index creation statements for optimizing query performance.
 * Indexes are created after tables to ensure they can be properly created.
 */

-- Book-related indexes
CREATE INDEX idx_books_isbn ON "Books" ("ISBN");
CREATE INDEX idx_books_title ON "Books" ("title");
CREATE INDEX idx_books_rate ON "Books" ("rate");

-- Author and category indexes
CREATE INDEX idx_authors_author_name ON "Authors" ("author_name");
CREATE INDEX idx_category_category_name ON "Category" ("category_name");

-- BookItem indexes
CREATE INDEX idx_bookitem_isbn ON "BookItem" ("ISBN");
CREATE INDEX idx_bookitem_status ON "BookItem" ("BookItemStatus");

-- Member-related indexes
CREATE INDEX idx_members_email ON "Members" ("email");
CREATE INDEX idx_members_phone ON "Members" ("phone");
CREATE INDEX idx_members_first_name ON "Members" ("first_name");
CREATE INDEX idx_members_last_name ON "Members" ("last_name");

-- User indexes
CREATE INDEX idx_users_username ON "Users" ("username");
CREATE INDEX idx_users_status ON "Users" ("AccountStatus");

-- Transaction indexes
CREATE INDEX idx_bookreservation_member_id ON "BookReservation" ("member_ID");
CREATE INDEX idx_bookreservation_barcode ON "BookReservation" ("barcode");
CREATE INDEX idx_bookreservation_status ON "BookReservation" ("BookReservationStatus");

CREATE INDEX idx_bookissue_member_id ON "BookIssue" ("member_ID");
CREATE INDEX idx_bookissue_barcode ON "BookIssue" ("barcode");
CREATE INDEX idx_bookissue_status ON "BookIssue" ("BookIssueStatus");

-- Interaction indexes
CREATE INDEX idx_comments_isbn ON "Comments" ("ISBN");
CREATE INDEX idx_comments_member_id ON "Comments" ("member_ID");

CREATE INDEX idx_reports_member_id ON "Reports" ("member_ID");
CREATE INDEX idx_reports_status ON "Reports" ("ReportStatus");