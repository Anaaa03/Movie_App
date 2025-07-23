ALTER TABLE movies
DROP COLUMN poster_url,
    ADD COLUMN poster_image BYTEA;