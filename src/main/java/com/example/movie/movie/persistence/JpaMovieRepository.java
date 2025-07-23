package com.example.movie.movie.persistence;

import com.example.movie.movie.persistence.model.MovieEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface JpaMovieRepository extends JpaRepository<MovieEntity, UUID> {
}
