package cz.cvut.fel.sin.library.service;

import cz.cvut.fel.sin.library.dto.AuthorDto;
import cz.cvut.fel.sin.library.dto.DtoMapper;
import cz.cvut.fel.sin.library.exception.ConflictException;
import cz.cvut.fel.sin.library.exception.NotFoundException;
import cz.cvut.fel.sin.library.model.Author;
import cz.cvut.fel.sin.library.model.Publisher;
import cz.cvut.fel.sin.library.repository.AuthorRepository;
import cz.cvut.fel.sin.library.repository.PublisherRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of {@link AuthorService}.
 */
@Slf4j // Lombok: generates "private static final Logger log" for this class
@Service // Spring component: created once, injected wherever AuthorService is needed
public class AuthorServiceImp implements AuthorService {

    /** DAO for author rows (injected by Spring). */
    private final AuthorRepository authorRepository;

    /** DAO for publisher rows (injected by Spring). */
    private final PublisherRepository publisherRepository;

    /** Entity -> DTO (way out). */
    private final DtoMapper dtoMapper;

    /**
     * Constructor injection: Spring creates the repositories and passes them in
     * (one constructor -> no {@code @Autowired} needed).
     *
     * @param authorRepository author DAO
     * @param publisherRepository publisher DAO
     * @param dtoMapper entity -> DTO mapper
     */
    public AuthorServiceImp(AuthorRepository authorRepository,
                            PublisherRepository publisherRepository,
                            DtoMapper dtoMapper) {
        this.authorRepository = authorRepository;
        this.publisherRepository = publisherRepository;
        this.dtoMapper = dtoMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional // all or nothing: any RuntimeException -> rollback, nothing is written to contract
    public AuthorDto signContract(Long authorId, Long publisherId) {
        log.trace("signContract(authorId={}, publisherId={})", authorId, publisherId);

        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> {
                    log.warn("Author {} not found", authorId);
                    return new NotFoundException("Author " + authorId + " not found");
                });
        log.debug("Loaded author {}", authorId);

        Publisher publisher = publisherRepository.findById(publisherId)
                .orElseThrow(() -> {
                    log.warn("Publisher {} not found", publisherId);
                    return new NotFoundException("Publisher " + publisherId + " not found");
                });
        log.debug("Loaded publisher {}", publisherId);

        // check on the owning side (Author.publishers = the contract table)
        if (author.getPublishers().contains(publisher)) {
            log.warn("Author {} already has a contract with publisher {}", authorId, publisherId);
            throw new ConflictException("Author " + authorId + " already has a contract with publisher " + publisherId);
        }

        author.addPublisher(publisher);          // both sides in Java; owning side -> INSERT INTO contract
        Author saved = authorRepository.save(author); // returns the managed (saved) entity; INSERT runs at flush/commit
        log.info("Contract signed: author {} <-> publisher {}", authorId, publisherId);
        return dtoMapper.toDto(saved); // map INSIDE the transaction (open-in-view is off)
    }
}
