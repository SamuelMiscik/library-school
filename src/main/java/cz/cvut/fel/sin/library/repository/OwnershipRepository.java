package cz.cvut.fel.sin.library.repository;

import cz.cvut.fel.sin.library.entities.Book;
import cz.cvut.fel.sin.library.entities.Library;
import cz.cvut.fel.sin.library.entities.Ownership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository (DAO layer) for {@link Ownership}.
 * <p>
 * Mainly for READING (searching ownerships).
 * To create / delete an ownership use the entity itself, NOT {@code save()} / {@code delete()} here:
 * <ul>
 *   <li>create: {@code new Ownership(library, book, copies)} + {@code libraryRepository.save(library)}</li>
 *   <li>delete: {@code ownership.remove()} (orphanRemoval deletes the row)</li>
 * </ul>
 */
@Repository // Spring bean of the DAO layer
public interface OwnershipRepository extends JpaRepository<Ownership, Long> {

    /**
     * All book titles a library owns.
     * <p>
     * SQL: {@code ... WHERE library_id = ?}
     *
     * @param library the library
     * @return its ownerships
     */
    List<Ownership> findByLibrary(Library library);

    /**
     * All libraries that own a book.
     * <p>
     * SQL: {@code ... WHERE book_id = ?}
     *
     * @param book the book
     * @return its ownerships
     */
    List<Ownership> findByBook(Book book);

    /**
     * The ownership of one book in one library (there is at most one, UNIQUE constraint).
     * <p>
     * SQL: {@code ... WHERE library_id = ? AND book_id = ?}
     *
     * @param library the library
     * @param book    the book
     * @return the ownership, or empty Optional if the library doesn't own the book
     */
    Optional<Ownership> findByLibraryAndBook(Library library, Book book);
}
