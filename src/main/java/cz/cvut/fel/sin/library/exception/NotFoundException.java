package cz.cvut.fel.sin.library.exception;

/**
 * Thrown when a requested entity does not exist in the database (HTTP 404).
 * <p>
 * Unchecked (extends {@link RuntimeException}): no {@code throws} needed in method signatures,
 * and Spring rolls back the {@code @Transactional} method when it is thrown.
 */
public class NotFoundException extends RuntimeException {

    /**
     * @param message precise description, e.g. "Author 5 not found"
     */
    public NotFoundException(String message) {
        super(message);
    }
}
