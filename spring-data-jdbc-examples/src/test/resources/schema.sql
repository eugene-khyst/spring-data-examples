CREATE TABLE CATEGORY (
  ID SERIAL PRIMARY KEY,
  NAME VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE AUTHOR (
  ID SERIAL PRIMARY KEY,
  FULL_NAME VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE BOOK (
  ID SERIAL PRIMARY KEY,
  ISBN VARCHAR(14) UNIQUE,
  TITLE VARCHAR(255) NOT NULL,
  PUBLICATION_DATE DATE NOT NULL
);

CREATE TABLE BOOK_CATEGORY (
  BOOK INTEGER REFERENCES BOOK(ID),
  CATEGORY INTEGER REFERENCES CATEGORY(ID),
  PRIMARY KEY(BOOK, CATEGORY)
);

CREATE TABLE BOOK_AUTHOR (
  BOOK INTEGER REFERENCES BOOK(ID),
  BOOK_KEY INTEGER,
  AUTHOR INTEGER REFERENCES AUTHOR(ID),
  PRIMARY KEY(BOOK, AUTHOR)
);

CREATE TABLE BOOK_RATING (
  ID SERIAL PRIMARY KEY,
  VERSION INTEGER NOT NULL,
  BOOK INTEGER REFERENCES BOOK(ID),
  RATING NUMERIC(4, 3) NOT NULL,
  NUMBER_OF_RATINGS INTEGER NOT NULL
);