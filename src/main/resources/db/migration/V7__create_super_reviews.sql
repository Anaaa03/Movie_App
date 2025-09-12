CREATE TABLE super_reviews
(
    id               UUID PRIMARY KEY,
    movie_id         UUID                     NOT NULL,
    user_id          UUID                     NOT NULL,
    overall_rating   INTEGER                  NOT NULL CHECK (overall_rating >= 1 AND overall_rating <= 5),
    script_rating    INTEGER CHECK (script_rating >= 1 AND script_rating <= 5),
    acting_rating    INTEGER CHECK (acting_rating >= 1 AND acting_rating <= 5),
    effects_rating   INTEGER CHECK (effects_rating >= 1 AND effects_rating <= 5),
    music_rating     INTEGER CHECK (music_rating >= 1 AND music_rating <= 5),
    title            TEXT,
    detailed_comment TEXT,
    pros             TEXT,
    cons             TEXT,
    recommendation   BOOLEAN,
    created_at       TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at       TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_super_review_movie
        FOREIGN KEY (movie_id)
            REFERENCES movies (id)
            ON DELETE CASCADE,
    CONSTRAINT fk_super_review_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE,
    CONSTRAINT unique_super_review UNIQUE (movie_id, user_id)
);