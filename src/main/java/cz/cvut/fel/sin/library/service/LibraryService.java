package cz.cvut.fel.sin.library.service;

import cz.cvut.fel.sin.library.dto.OwnershipDto;
import cz.cvut.fel.sin.library.dto.request.AddBookDto;
import cz.cvut.fel.sin.library.model.Ownership;

/**
 * Business operations of a {@link cz.cvut.fel.sin.library.model.Library}.
 * <p>
 * Implemented by {@link LibraryServiceImp}. The controller depends on this interface,
 * not on the implementation (easy to swap / mock in tests).
 */
public interface LibraryService {

    /**
     * Adds an existing book to a library, i.e. creates an {@link Ownership} with the given number of copies.
     * <p>
     * Preconditions: the library exists, the book exists, the library does not own the book yet.<br>
     * Postcondition: a new row (library_id, book_id, copies) exists in the {@code ownership} table.
     * <p>
     * Throws NotFoundException (404) if the library or the book does not exist,
     * ConflictException (409) if the library already owns the book.
     *
     * @param libraryId id of the library (from the URL)
     * @param dto       request body: ISBN of the book + number of copies (1-5, already validated by {@code @Valid})
     * @return the saved ownership as DTO (mapped inside the transaction)
     */
    OwnershipDto addBookToLibrary(Long libraryId, AddBookDto dto);
}
