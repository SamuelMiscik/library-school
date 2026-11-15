package cz.cvut.fel.sin.library.dto;

import cz.cvut.fel.sin.library.model.Genre;

import java.time.LocalDate;
import java.util.Set;

/**
 * Book returned to the frontend (response).
 * The enum {@link Genre} is used directly, it is only a value (JSON: "NOVEL").
 *
 * @param isbn            ISBN, the book's id
 * @param title           title
 * @param publicationDate date of publication, can be null
 * @param genre           genre, can be null
 * @param publisherId     id of the publisher
 * @param authorIds       ids of the authors
 */
public record BookDto(
        String isbn,
        String title,
        LocalDate publicationDate,
        Genre genre,
        Long publisherId,
        Set<Long> authorIds
) {}
