package cz.cvut.fel.sin.library.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * Request body for adding a book to a library ({@code POST /libraries/{libraryId}/books}).
 * Creates an {@link cz.cvut.fel.sin.library.model.Ownership}.
 *
 * @param isbn   ISBN of an existing book, required
 * @param copies number of copies, 1-5 (same rule as Ownership.copies)
 */
public record AddBookDto(
        @NotBlank String isbn,      // 400 if missing / empty
        @Min(1) @Max(5) int copies  // 400 if outside 1-5 (missing in JSON -> 0 -> also 400)
) {}
