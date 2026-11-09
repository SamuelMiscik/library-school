package cz.cvut.fel.sin.library.repository;

import cz.cvut.fel.sin.library.entities.Library;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository (DAO layer) for {@link Library}.
 * <p>
 * Only an interface: Spring Data generates the implementation at runtime.
 * {@code JpaRepository<Library, Long>} = entity type + type of its {@code @Id}.
 * <p>
 * CRUD methods we get for free: {@code save(library)} (INSERT/UPDATE), {@code findById(id)},
 * {@code findAll()}, {@code count()}, {@code existsById(id)}, {@code deleteById(id)}, {@code delete(library)}.
 * Deleting a library also deletes its ownerships (cascade on {@code Library.ownerships}).
 */
@Repository // Spring bean of the DAO layer (optional for JpaRepository, but makes it clear)
public interface LibraryRepository extends JpaRepository<Library, Long> {

    /**
     * Finds libraries whose name contains the given text, ignoring upper/lower case.
     * <p>
     * Derived query: Spring builds the SQL from the method name:
     * {@code SELECT * FROM library WHERE UPPER(name) LIKE UPPER('%text%')}
     *
     * @param text part of the name
     * @return matching libraries (empty list if none)
     */
    List<Library> findByNameContainingIgnoreCase(String text);
}
