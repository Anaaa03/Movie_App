ALTER TABLE super_reviews DROP CONSTRAINT IF EXISTS super_reviews_overall_rating_check;
ALTER TABLE super_reviews DROP CONSTRAINT IF EXISTS super_reviews_script_rating_check;
ALTER TABLE super_reviews DROP CONSTRAINT IF EXISTS super_reviews_acting_rating_check;
ALTER TABLE super_reviews DROP CONSTRAINT IF EXISTS super_reviews_effects_rating_check;
ALTER TABLE super_reviews DROP CONSTRAINT IF EXISTS super_reviews_music_rating_check;

ALTER TABLE super_reviews
    ADD CONSTRAINT super_reviews_overall_rating_check
        CHECK (overall_rating >= 1 AND overall_rating <= 10);

ALTER TABLE super_reviews
    ADD CONSTRAINT super_reviews_script_rating_check
        CHECK (script_rating >= 1 AND script_rating <= 10);

ALTER TABLE super_reviews
    ADD CONSTRAINT super_reviews_acting_rating_check
        CHECK (acting_rating >= 1 AND acting_rating <= 10);

ALTER TABLE super_reviews
    ADD CONSTRAINT super_reviews_effects_rating_check
        CHECK (effects_rating >= 1 AND effects_rating <= 10);

ALTER TABLE super_reviews
    ADD CONSTRAINT super_reviews_music_rating_check
        CHECK (music_rating >= 1 AND music_rating <= 10);
