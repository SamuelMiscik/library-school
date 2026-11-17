package cz.cvut.fel.sin.library.service;

import cz.cvut.fel.sin.library.dto.request.BookCreateDto;
import cz.cvut.fel.sin.library.dto.BookDto;

/**
 * Business operations of a {@link cz.cvut.fel.sin.library.model.Publisher}.
 * <p>
 * Implemented by {@link PublisherServiceImp}. The controller depends on this interface,
 * not on the implementation (easy to swap / mock in tests).
 */
public interface PublisherService {

    /**
     * Publishes a new book: creates it, links it to the publisher and its authors and saves it.
     * <p>
     * Preconditions: the publisher exists, the ISBN does not exist yet, every author exists
     * and has a contract with the publisher.<br>
     * Postcondition: a new row in {@code book} (with publisher_id) and one row in {@code writes}
     * per author exist.
     * <p>
     * Throws NotFoundException (404) if the publisher or an author does not exist,
     * ConflictException (409) if the ISBN exists or an author has no contract with the publisher.
     *
     * @param publisherId id of the publisher (from the URL)
     * @param dto         request body with the book data and author ids (already validated by {@code @Valid})
     * @return the saved book as DTO (mapped inside the transaction, lazy relations are still loadable)
     */
    public BookDto publishBook(Long publisherId, BookCreateDto dto);
}
