package cz.cvut.fel.sin.library.exception;

/**
 * Thrown when an operation conflicts with the current state of the data (HTTP 409),
 * e.g. a contract or ISBN that already exists.
 * <p>
 * Unchecked (extends {@link RuntimeException}): no {@code throws} needed in method signatures,
 * and Spring rolls back the {@code @Transactional} method when it is thrown.
 */
public class ConflictException extends RuntimeException {

    /**
     * @param message precise description, e.g. "Author 5 already has a contract with publisher 2"
     */
    public ConflictException(String message) {
        super(message);
    }
}
