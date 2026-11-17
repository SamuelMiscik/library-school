package cz.cvut.fel.sin.library.service;

import cz.cvut.fel.sin.library.dto.request.AddBookDto;
import cz.cvut.fel.sin.library.dto.DtoMapper;
import cz.cvut.fel.sin.library.dto.OwnershipDto;
import cz.cvut.fel.sin.library.exception.ConflictException;
import cz.cvut.fel.sin.library.exception.NotFoundException;
import cz.cvut.fel.sin.library.model.Book;
import cz.cvut.fel.sin.library.model.Library;
import cz.cvut.fel.sin.library.model.Ownership;
import cz.cvut.fel.sin.library.repository.LibraryRepository;
import cz.cvut.fel.sin.library.repository.BookRepository;
import cz.cvut.fel.sin.library.repository.OwnershipRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of {@link LibraryService}.
 */
@Service // Spring component: created once, injected wherever LibraryService is needed
public class LibraryServiceImp implements LibraryService {

    /** Logger of this class (the class name is printed in every log line). */
    private static final Logger log = LoggerFactory.getLogger(LibraryServiceImp.class);

    /** DAO for library rows (injected by Spring). */
    private final LibraryRepository libraryRepository;

    /** DAO for book rows (injected by Spring). */
    private final BookRepository bookRepository;

    /** DAO for ownership rows (injected by Spring). */
    private final OwnershipRepository ownershipRepository;

    /** Entity -> DTO (way out). */
    private final DtoMapper dtoMapper;

    /**
     * Constructor injection: Spring creates the repositories and passes them in
     * (one constructor -> no {@code @Autowired} needed).
     *
     * @param libraryRepository library DAO
     * @param bookRepository book DAO
     * @param ownershipRepository ownership DAO
     * @param dtoMapper entity -> DTO mapper
     */
    public LibraryServiceImp(LibraryRepository libraryRepository,
                             BookRepository bookRepository,
                             OwnershipRepository ownershipRepository,
                             DtoMapper dtoMapper) {
        this.libraryRepository = libraryRepository;
        this.bookRepository = bookRepository;
        this.ownershipRepository = ownershipRepository;
        this.dtoMapper = dtoMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional // all or nothing: any RuntimeException -> rollback, no row in ownership
    public OwnershipDto addBookToLibrary(Long libraryId, AddBookDto dto) {
        log.trace("addBookToLibrary(libraryId={}, isbn={}, copies={})", libraryId, dto.isbn(), dto.copies());

        Library library = libraryRepository.findById(libraryId)
                .orElseThrow(() -> {
                    log.warn("Library {} not found", libraryId);
                    return new NotFoundException("Library " + libraryId + " not found");
                });
        log.debug("Loaded library {}", libraryId);

        Book book = bookRepository.findById(dto.isbn())
                .orElseThrow(() -> {
                    log.warn("Book {} not found", dto.isbn());
                    return new NotFoundException("Book " + dto.isbn() + " not found");
                });
        log.debug("Loaded book {}", dto.isbn());

        // DB has UNIQUE(library_id, book_id) too, this check gives a clean 409 instead of a DB error
        if (ownershipRepository.findByLibraryAndBook(library, book).isPresent()) {
            log.warn("Library {} already owns book {}", libraryId, dto.isbn());
            throw new ConflictException("Library " + libraryId + " already owns book " + dto.isbn());
        }

        Ownership ownership = new Ownership(library, book, dto.copies()); // links it into library + book (Java)
        Ownership saved = ownershipRepository.save(ownership);           // INSERT INTO ownership at flush/commit
        log.info("Library {} now owns {} copies of book {}", libraryId, dto.copies(), dto.isbn());
        return dtoMapper.toDto(saved); // map INSIDE the transaction (open-in-view is off)
    }
}
