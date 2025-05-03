/*
 * Database Triggers
 * This file contains all trigger definitions for maintaining data consistency
 * and automating business rules in the library management system.
 */

-- Books Status Management Triggers
CREATE OR REPLACE FUNCTION update_books_stats()
RETURNS TRIGGER AS $$
DECLARE
    current_isbn BIGINT;
    total_quantity INTEGER;
    total_lost INTEGER;
    total_loaned INTEGER;
    total_reserved INTEGER;
BEGIN
    IF TG_OP = 'DELETE' THEN
        current_isbn := OLD."ISBN";
    ELSE
        current_isbn := NEW."ISBN";
    END IF;

    -- Calculate totals
    SELECT COUNT(*) INTO total_quantity FROM "BookItem" WHERE "ISBN" = current_isbn;
    SELECT COUNT(*) INTO total_lost FROM "BookItem" WHERE "ISBN" = current_isbn AND "BookItemStatus" = 'LOST';
    SELECT COUNT(*) INTO total_loaned FROM "BookItem" WHERE "ISBN" = current_isbn AND "BookItemStatus" = 'LOANED';
    SELECT COUNT(*) INTO total_reserved FROM "BookItem" WHERE "ISBN" = current_isbn AND "BookItemStatus" = 'RESERVED';

    -- Update Books table with explicit enum casts
    UPDATE "Books"
    SET "quantity" = total_quantity,
        "number_lost_book" = total_lost,
        "number_loaned_book" = total_loaned,
        "number_reserved_book" = total_reserved,
        "BookStatus" = CASE
            WHEN total_quantity <= (total_lost + total_loaned + total_reserved) THEN 'UNAVAILABLE'::book_status
            ELSE 'AVAILABLE'::book_status
        END
    WHERE "ISBN" = current_isbn;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;


DROP TRIGGER IF EXISTS update_books_on_bookitem_change ON "BookItem";
CREATE TRIGGER update_books_on_bookitem_change
    AFTER INSERT OR UPDATE OR DELETE ON "BookItem"
    FOR EACH ROW
    EXECUTE FUNCTION update_books_stats();

-- Book Rating Management Triggers
CREATE OR REPLACE FUNCTION update_books_rate()
RETURNS TRIGGER AS $$
DECLARE
    current_isbn BIGINT;
    average_rate FLOAT;
BEGIN
    IF TG_OP = 'DELETE' THEN
        current_isbn := OLD."ISBN";
    ELSE
        current_isbn := NEW."ISBN";
    END IF;

    -- Calculate average rate for the provided ISBN
    SELECT AVG(CAST("rate" AS INTEGER))
    INTO average_rate
    FROM "Comments"
    WHERE "ISBN" = current_isbn;

    -- Update Books table (verify if subtracting 1 is required by business rules)
    UPDATE "Books"
    SET "rate" = FLOOR(average_rate)::INTEGER - 1
    WHERE "ISBN" = current_isbn;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS update_books_rate_on_comment_change ON "Comments";
CREATE TRIGGER update_books_rate_on_comment_change
    AFTER INSERT OR UPDATE OR DELETE ON "Comments"
    FOR EACH ROW
    EXECUTE FUNCTION update_books_rate();

-- Reservation Management Triggers
CREATE OR REPLACE FUNCTION update_reservation_status()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW."BookReservationStatus" = 'WAITING' AND CURRENT_DATE > NEW."due_date" THEN
        -- Update BookReservation status
        UPDATE "BookReservation"
        SET "BookReservationStatus" = 'CANCELED'
        WHERE "barcode" = NEW."barcode"
        AND "BookReservationStatus" = 'WAITING';

        -- Update BookItem status
        UPDATE "BookItem"
        SET "BookItemStatus" = 'AVAILABLE'
        WHERE "barcode" = NEW."barcode";
    END IF;

    IF NEW."BookReservationStatus" = 'CANCELED' THEN
        UPDATE "BookItem"
        SET "BookItemStatus" = 'AVAILABLE'
        WHERE "barcode" = NEW."barcode";
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS update_reservation_on_status_change ON "BookReservation";
CREATE TRIGGER update_reservation_on_status_change
    AFTER UPDATE ON "BookReservation"
    FOR EACH ROW
    EXECUTE FUNCTION update_reservation_status();

-- Book Issue Management Triggers
CREATE OR REPLACE FUNCTION update_book_issue_status()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW."BookIssueStatus" = 'BORROWED' THEN
        -- Update BookItem status
        UPDATE "BookItem"
        SET "BookItemStatus" = 'LOANED'
        WHERE "barcode" = NEW."barcode"
        AND ("BookItemStatus" = 'AVAILABLE' OR "BookItemStatus" = 'RESERVED');

        -- Update BookReservation status
        UPDATE "BookReservation"
        SET "BookReservationStatus" = 'COMPLETED'
        WHERE "barcode" = NEW."barcode"
        AND "BookReservationStatus" = 'WAITING';
    END IF;

    IF NEW."BookIssueStatus" = 'BORROWED' AND CURRENT_DATE > NEW."due_date" + INTERVAL '1 month' THEN
        -- Update BookIssue status
        UPDATE "BookIssue"
        SET "BookIssueStatus" = 'LOST'
        WHERE "barcode" = NEW."barcode";

        -- Update BookItem status
        UPDATE "BookItem"
        SET "BookItemStatus" = 'LOST'
        WHERE "barcode" = NEW."barcode";
    END IF;

    IF NEW."BookIssueStatus" = 'RETURNED' THEN
        UPDATE "BookItem"
        SET "BookItemStatus" = 'AVAILABLE'
        WHERE "barcode" = NEW."barcode";
    END IF;

    IF NEW."BookIssueStatus" = 'LOST' THEN
        UPDATE "BookItem"
        SET "BookItemStatus" = 'LOST'
        WHERE "barcode" = NEW."barcode";
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS update_book_issue_on_status_change ON "BookIssue";
CREATE TRIGGER update_book_issue_on_status_change
    AFTER UPDATE ON "BookIssue"
    FOR EACH ROW
    EXECUTE FUNCTION update_book_issue_status();