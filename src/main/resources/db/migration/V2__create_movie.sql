CREATE TABLE movies
(
    id               UUID PRIMARY KEY,
    title            TEXT NOT NULL,
    release_year     INT,
    duration_minutes INT,
    description      TEXT,
    poster_url       TEXT,
    trailer_url      TEXT
);