package com.example.movie.movie.domain;

import com.example.movie.movie.api.model.AddMovieRequest;
import com.example.movie.movie.domain.model.Movie;
import com.example.movie.movie.domain.model.Poster;
import com.example.movie.movie.persistence.MovieRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

@Service
public class AddMovieUseCase {
    private final MovieRepository movieRepository;

    public AddMovieUseCase(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public Movie addMovie(AddMovieRequest addMovieRequest) {
        String title = addMovieRequest.getTitle();
        Integer releaseYear = addMovieRequest.getReleaseYear();
        Integer durationMinutes = addMovieRequest.getDurationMinutes();
        String description = addMovieRequest.getDescription();
        String trailerUrl = addMovieRequest.getTrailerUrl();

        Movie movie = Movie.addNewMovie(title, releaseYear, durationMinutes, description, trailerUrl, null);

        return movieRepository.save(movie);
    }

    public void uploadPoster(UUID movieId, MultipartFile file) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found"));

        try {
            byte[] bytes = file.getBytes();
            Poster poster = new Poster(bytes);
            Movie updatedMovie = movie.addPoster(poster);
            movieRepository.save(updatedMovie);
        } catch (Exception e) {
            throw new RuntimeException("sorry", e);
        }
    }

    public Optional<Movie> findById(UUID id) {
        return movieRepository.findById(id);
    }
}
