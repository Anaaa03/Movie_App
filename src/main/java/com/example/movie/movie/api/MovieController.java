package com.example.movie.movie.api;

import com.example.movie.movie.api.model.AddMovieRequest;
import com.example.movie.movie.api.model.MovieResponse;
import com.example.movie.movie.domain.AddMovieUseCase;
import com.example.movie.movie.domain.model.Movie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static com.example.movie.movie.api.MovieResponseMapper.mapToMovieResponse;

@RestController
@RequestMapping("/api/movies")
public class MovieController {
    private final AddMovieUseCase addMovieUseCase;

    public MovieController(AddMovieUseCase addMovieUseCase) {
        this.addMovieUseCase = addMovieUseCase;
    }

    @PostMapping
    public ResponseEntity<MovieResponse> addMovie(@RequestBody AddMovieRequest request) {
        try {
            Movie savedMovie = addMovieUseCase.addMovie(request);
            MovieResponse response = mapToMovieResponse(savedMovie);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @PostMapping("/{movieId}/poster")
    public ResponseEntity<MovieResponse> uploadPoster(
            @PathVariable UUID movieId,
            @RequestParam("posterImage") MultipartFile posterImage) {
        addMovieUseCase.uploadPoster(movieId, posterImage);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{movieId}")
    public ResponseEntity<MovieResponse> getMovieById(@PathVariable UUID movieId) {
        return addMovieUseCase.findById(movieId)
                .map(movie -> ResponseEntity.ok(mapToMovieResponse(movie)))
                .orElse(ResponseEntity.notFound().build());
    }
}
