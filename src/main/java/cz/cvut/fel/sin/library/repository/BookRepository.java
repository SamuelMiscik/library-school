package cz.cvut.fel.sin.library.repository;

import cz.cvut.fel.sin.library.model.Book;
import cz.cvut.fel.sin.library.model.Genre;
import cz.cvut.fel.sin.library.model.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository (DAO layer) for {@link Book}.
 * <p>
 * {@code JpaRepository<Book, String>}: the id is the ISBN (a String), so
 * {@code findById("978-...")} / {@code existsById("978-...")} work with ISBNs.
 * <p>
 * Deleting a book also deletes its ownerships (cascade) and its rows in {@code writes}
 * (Book is the owning side), authors and publisher stay.
 */
@Repository // Spring bean of the DAO layer
public interface BookRepository extends JpaRepository<Book, String> {

    /**
     * Finds books whose title contains the given text, ignoring case.
     * <p>
     * SQL: {@code ... WHERE UPPER(title) LIKE UPPER('%text%')}
     *
     * @param text part of the title
     * @return matching books
     */
    List<Book> findByTitleContainingIgnoreCase(String text);

    /**
     * Finds all books of a genre.
     * <p>
     * SQL: {@code ... WHERE genre = 'NOVEL'}
     *
     * @param genre the genre
     * @return books of that genre
     */
    List<Book> findByGenre(Genre genre);

    /**
     * Finds all books of a publisher.
     * <p>
     * SQL: {@code ... WHERE publisher_id = ?}
     *
     * @param publisher the publisher
     * @return books published by it
     */
    List<Book> findByPublisher(Publisher publisher);
}
