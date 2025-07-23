package com.example.movie.movie.persistence;

import com.example.movie.movie.domain.model.Movie;
import com.example.movie.movie.persistence.model.MovieEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class MovieRepository {
    private final JpaMovieRepository movieRepository;
    private final JpaMovieRepository jpaMovieRepository;

    public MovieRepository(JpaMovieRepository movieRepository, JpaMovieRepository jpaMovieRepository) {
        this.movieRepository = movieRepository;
        this.jpaMovieRepository = jpaMovieRepository;
    }


    public Movie save(Movie movie) {
        MovieEntity saved = movieRepository.save(MovieEntityMapper.toEntity(movie));
        return MovieEntityMapper.toDomain(saved);
    }

    public Optional<Movie> findById(UUID id) {
        return jpaMovieRepository.findById(id)
                .map(MovieEntityMapper::toDomain);
    }

}


