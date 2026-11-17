package cz.cvut.fel.sin.library.service;

import cz.cvut.fel.sin.library.dto.AuthorDto;
import cz.cvut.fel.sin.library.model.Author;

/**
 * Business operations of an {@link Author}.
 * <p>
 * Implemented by {@link AuthorServiceImp}. The controller depends on this interface,
 * not on the implementation (easy to swap / mock in tests).
 */
public interface AuthorService {

    /**
     * Signs a contract between an author and a publisher (adds a row to the {@code contract} join table).
     * <p>
     * Preconditions: the author exists, the publisher exists, they do not have a contract yet.<br>
     * Postcondition: a row (author_id, publisher_id) exists in the {@code contract} table.
     * <p>
     * Throws NotFoundException (404) if the author or the publisher does not exist,
     * ConflictException (409) if the contract already exists.
     *
     * @param authorId    id of the author (from the URL)
     * @param publisherId id of the publisher (from {@code ContractCreateDto})
     * @return the updated author as DTO (mapped inside the transaction, lazy relations are still loadable)
     */
    public AuthorDto signContract(Long authorId, Long publisherId);


}
