package cz.cvut.fel.sin.library.dto.request;

import cz.cvut.fel.sin.library.model.Genre;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Set;

/**
 * Request body for publishing a new book ({@code POST /publishers/{publisherId}/books}).
 * The publisher is in the URL, authors are sent only as IDs (the service loads them from the DB).
 *
 * @param isbn            ISBN, required, max 17 chars (same as the DB column)
 * @param title           title, required
 * @param publicationDate date of publication, optional (JSON: "2024-05-31")
 * @param genre           genre, optional (JSON: "NOVEL")
 * @param authorIds       ids of the authors, at least one
 */
public record BookCreateDto(
        @NotBlank @Size(max = 17) String isbn, // 400 if missing or longer than the DB column
        @NotBlank String title,                // 400 if missing / empty
        LocalDate publicationDate,
        Genre genre,
        @NotEmpty Set<Long> authorIds          // 400 if no author (UML "1..*")
) {}
