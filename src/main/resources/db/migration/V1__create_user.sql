CREATE TABLE users
(
    id            UUID PRIMARY KEY,
    username      TEXT  NOT NULL UNIQUE,
    email         TEXT NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    created_at    TIMESTAMP WITH TIME ZONE NOT NULL
);