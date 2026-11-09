package cz.cvut.fel.sin.library.repository;

import cz.cvut.fel.sin.library.entities.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository (DAO layer) for {@link Author}.
 * <p>
 * {@code JpaRepository<Author, Long>}: entity type + type of its {@code @Id}.
 * <p>
 * Careful with delete: an author still listed in {@code writes} (Book.authors) or
 * {@code contract} cannot be deleted (FK error). First {@code book.removeAuthor(author)}
 * for his books, then delete.
 */
@Repository // Spring bean of the DAO layer
public interface AuthorRepository extends JpaRepository<Author, Long> {

    /**
     * Finds authors by last name (exact match).
     * <p>
     * SQL: {@code ... WHERE last_name = ?}
     *
     * @param lastName the last name
     * @return matching authors
     */
    List<Author> findByLastName(String lastName);

    /**
     * Finds an author by e-mail.
     * <p>
     * {@code Optional} = "maybe there is one, maybe not" (instead of returning null).
     *
     * @param email the e-mail
     * @return the author, or empty Optional if not found
     */
    Optional<Author> findByEmail(String email);
}
