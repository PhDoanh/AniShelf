-- INIT ALL OBJECTS RELATIVE TO BOOKS

-- define the custom types
CREATE TYPE book_status AS ENUM ('AVAILABLE', 'UNAVAILABLE');

-- create the table
CREATE TABLE IF NOT EXISTS Books (
    ISBN BIGINT PRIMARY KEY,
    Title varchar(1000) NOT NULL,
    ImagePath varchar(1000) NOT NULL DEFAULT 'defaultBookImg.png',
    Description text,
--     PlaceAt varchar(250),
    Preview varchar(250) DEFAULT NULL,
    Quantity INTEGER NOT NULL DEFAULT 0,
--     NumberLostedBook INTEGER NOT NULL DEFAULT 0,
    NumberLoanedBook INTEGER NOT NULL DEFAULT 0,
    NumberReservedBook INTEGER NOT NULL DEFAULT 0,
    Rate INTEGER NOT NULL DEFAULT 5,
    BookStatus book_status NOT NULL DEFAULT 'AVAILABLE',
    AddedTime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
