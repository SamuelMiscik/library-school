package cz.cvut.fel.sin.library.dto;

/**
 * One book owned by one library (response).
 *
 * @param id        ownership id
 * @param libraryId id of the library
 * @param bookIsbn  ISBN of the book
 * @param copies    number of copies (1-5)
 */
public record OwnershipDto(
        Long id,
        Long libraryId,
        String bookIsbn,
        int copies
) {}
