CREATE TABLE reviews
(
    id         UUID PRIMARY KEY,
    movie_id   UUID                     NOT NULL,
    user_id    UUID                     NOT NULL,
    rating     INTEGER                  NOT NULL CHECK (rating >= 1 AND rating <= 10),
    comment    TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_review_movie
        FOREIGN KEY (movie_id)
            REFERENCES movies (id)
            ON DELETE CASCADE,
    CONSTRAINT fk_review_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE,
    CONSTRAINT unique_review UNIQUE (movie_id, user_id)
); 