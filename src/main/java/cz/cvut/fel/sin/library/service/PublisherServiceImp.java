package cz.cvut.fel.sin.library.service;

import cz.cvut.fel.sin.library.dto.DtoMapper;
import cz.cvut.fel.sin.library.dto.request.BookCreateDto;
import cz.cvut.fel.sin.library.exception.ConflictException;
import cz.cvut.fel.sin.library.exception.NotFoundException;
import cz.cvut.fel.sin.library.model.Author;
import cz.cvut.fel.sin.library.model.Book;
import cz.cvut.fel.sin.library.model.Publisher;
import cz.cvut.fel.sin.library.repository.PublisherRepository;
import cz.cvut.fel.sin.library.repository.BookRepository;
import cz.cvut.fel.sin.library.repository.AuthorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of {@link PublisherService}.
 */
@Service // Spring component: created once, injected wherever PublisherService is needed
public class PublisherServiceImp implements PublisherService {

    /** Logger of this class (the class name is printed in every log line). */
    private static final Logger log = LoggerFactory.getLogger(PublisherServiceImp.class);

    /** DAO for publisher rows (injected by Spring). */
    private final PublisherRepository publisherRepository;

    /** DAO for book rows (injected by Spring). */
    private final BookRepository bookRepository;

    /** DAO for author rows (injected by Spring). */
    private final AuthorRepository authorRepository;

    /** Converts the request DTO to a new Book entity (plain fields only). */
    private final DtoMapper dtoMapper;

    /**
     * Constructor injection: Spring creates the repositories and passes them in
     * (one constructor -> no {@code @Autowired} needed).
     *
     * @param publisherRepository publisher DAO
     * @param bookRepository book DAO
     * @param authorRepository author DAO
     * @param dtoMapper DTO <-> entity mapper
     */
    public PublisherServiceImp(PublisherRepository publisherRepository,
                               BookRepository bookRepository,
                               AuthorRepository authorRepository,
                               DtoMapper dtoMapper) {
        this.publisherRepository = publisherRepository;
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.dtoMapper = dtoMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional // all or nothing: any RuntimeException -> rollback, no row in book / writes
    public Book publishBook(Long publisherId, BookCreateDto dto) {
        log.trace("publishBook(publisherId={}, isbn={}, authorIds={})", publisherId, dto.isbn(), dto.authorIds());

        Publisher publisher = publisherRepository.findById(publisherId)
                .orElseThrow(() -> {
                    log.warn("Publisher {} not found", publisherId);
                    return new NotFoundException("Publisher " + publisherId + " not found");
                });
        log.debug("Loaded publisher {}", publisherId);

        // ISBN is the primary key -> must not exist yet (otherwise save() would UPDATE the old book!)
        if (bookRepository.existsById(dto.isbn())) {
            log.warn("Book {} already exists", dto.isbn());
            throw new ConflictException("Book " + dto.isbn() + " already exists");
        }

        // loop 1: every author must exist and have a contract with this publisher
        Set<Author> authors = new HashSet<>();
        for (Long authorId : dto.authorIds()) {
            log.trace("Checking author {}", authorId);
            Author author = authorRepository.findById(authorId)
                    .orElseThrow(() -> {
                        log.warn("Author {} not found", authorId);
                        return new NotFoundException("Author " + authorId + " not found");
                    });
            if (!author.getPublishers().contains(publisher)) { // owning side = contract table
                log.warn("Author {} has no contract with publisher {}", authorId, publisherId);
                throw new ConflictException("Author " + authorId + " has no contract with publisher " + publisherId);
            }
            authors.add(author);
        }
        log.debug("All {} authors checked", authors.size());

        // create the book in memory and connect it
        Book book = dtoMapper.toEntity(dto);     // new Book with isbn, title, publicationDate, genre
        book.setPublisher(publisher);            // helper: book.publisher_id + publisher.books
        for (Author author : authors) {          // loop 2
            book.addAuthor(author);              // both sides; owning side -> INSERT INTO writes
        }

        Book saved = bookRepository.save(book);  // INSERT INTO book + writes at flush/commit
        log.info("Publisher {} published book {} with {} authors", publisherId, dto.isbn(), authors.size());
        return saved;
    }
}
