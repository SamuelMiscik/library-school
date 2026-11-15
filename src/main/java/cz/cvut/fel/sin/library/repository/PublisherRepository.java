package cz.cvut.fel.sin.library.repository;

import cz.cvut.fel.sin.library.model.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository (DAO layer) for {@link Publisher}.
 * <p>
 * {@code JpaRepository<Publisher, Long>}: entity type + type of its {@code @Id}.
 * <p>
 * Careful with delete: a publisher that still has books or contracts cannot be deleted
 * (FK error) - books must not lose their publisher.
 */
@Repository // Spring bean of the DAO layer
public interface PublisherRepository extends JpaRepository<Publisher, Long> {

    /**
     * Finds a publisher by its exact name.
     *
     * @param name the name
     * @return the publisher, or empty Optional if not found
     */
    Optional<Publisher> findByName(String name);
}
