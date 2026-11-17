package cz.cvut.fel.sin.library.dto;

import cz.cvut.fel.sin.library.dto.request.AuthorCreateDto;
import cz.cvut.fel.sin.library.dto.request.BookCreateDto;
import cz.cvut.fel.sin.library.dto.request.LibraryCreateDto;
import cz.cvut.fel.sin.library.dto.request.PublisherCreateDto;
import cz.cvut.fel.sin.library.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * Converts entities (BO) to DTOs (way OUT) and request DTOs to entities (way IN).
 * <p>
 * MapStruct generates the implementation ({@code DtoMapperImpl}) at COMPILE time
 * (see {@code target/generated-sources/annotations}). At runtime it is an ordinary class
 * with getters / constructors, no reflection.
 * <ul>
 *     !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 *   <li>fields with the same name are copied automatically</li>
 *   <li>{@code @Mapping} only where names differ or a relation becomes an ID</li>
 *   <li>{@code ignore = true}: the service fills it in (needs the DB), or the DB generates it</li>
 *   !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * </ul>
 * It never calls a repository: relations in {@code toEntity} are set by the service.
 * <p>
 * Response methods read lazy relations (e.g. {@code book.getAuthors()}), so call them
 * while the Hibernate session is open (inside the service, or in the controller with open-in-view).
 */
@Mapper(
        componentModel = "spring",                    // generated class is a Spring bean -> can be injected
        unmappedTargetPolicy = ReportingPolicy.ERROR  // a target field nobody fills -> COMPILE error (safety)
)
public interface DtoMapper {

    // ---------------- entity -> DTO (response) ----------------

    /**
     * Converts an author to its DTO.
     *
     * @param author the entity
     * @return the DTO, relations as IDs
     */
    @Mapping(target = "publisherIds", source = "publishers") // Set<Publisher> -> Set<Long> via publisherId()
    @Mapping(target = "bookIsbns", source = "books")         // Set<Book> -> Set<String> via bookIsbn()
    AuthorDto toDto(Author author);

    /**
     * Converts a book to its DTO.
     *
     * @param book the entity
     * @return the DTO, publisher and authors as IDs
     */
    @Mapping(target = "publisherId", source = "publisher.id") // book.getPublisher().getId() (null-safe)
    @Mapping(target = "authorIds", source = "authors")        // Set<Author> -> Set<Long> via authorId()
    BookDto toDto(Book book);

    /**
     * Converts a publisher to its DTO.
     *
     * @param publisher the entity
     * @return the DTO, authors and books as IDs
     */
    @Mapping(target = "authorIds", source = "authors")
    @Mapping(target = "bookIsbns", source = "books")
    PublisherDto toDto(Publisher publisher); // address -> AddressDto via toDto(Address)

    /**
     * Converts a library to its DTO, including its ownerships.
     *
     * @param library the entity
     * @return the DTO
     */
    LibraryDto toDto(Library library); // ownerships -> List<OwnershipDto> via toDto(Ownership)

    /**
     * Converts an ownership to its DTO.
     *
     * @param ownership the entity
     * @return the DTO, library and book as IDs
     */
    @Mapping(target = "libraryId", source = "library.id")
    @Mapping(target = "bookIsbn", source = "book.isbn")
    OwnershipDto toDto(Ownership ownership);

    /**
     * Converts an embedded address to its DTO (all names match).
     *
     * @param address the address, can be null
     * @return the DTO, or null if there is no address
     */
    AddressDto toDto(Address address);

    // ---------------- request DTO -> entity (way IN) ----------------

    /**
     * Creates a new (unsaved) author from the request.
     *
     * @param dto the request body
     * @return new entity without id and relations
     */
    @Mapping(target = "id", ignore = true)         // DB generates it
    @Mapping(target = "publishers", ignore = true) // contracts are signed later (signContract)
    @Mapping(target = "books", ignore = true)      // books are published later (publishBook)
    Author toEntity(AuthorCreateDto dto);

    /**
     * Creates a new (unsaved) book from the request, only the plain fields.
     * The service must still call {@code setPublisher(...)} and {@code addAuthor(...)}
     * with entities loaded from the DB ({@code dto.authorIds()} are only numbers).
     *
     * @param dto the request body
     * @return new entity without publisher and authors
     */
    @Mapping(target = "publisher", ignore = true)  // service: book.setPublisher(publisher)
    @Mapping(target = "authors", ignore = true)    // service: book.addAuthor(author)
    @Mapping(target = "ownerships", ignore = true) // libraries add it later (addBookToLibrary)
    Book toEntity(BookCreateDto dto);

    /**
     * Creates a new (unsaved) publisher from the request.
     *
     * @param dto the request body
     * @return new entity without id and relations
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "books", ignore = true)
    @Mapping(target = "authors", ignore = true)
    Publisher toEntity(PublisherCreateDto dto);

    /**
     * Creates a new (unsaved) library from the request.
     *
     * @param dto the request body
     * @return new entity without id and ownerships
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ownerships", ignore = true)
    Library toEntity(LibraryCreateDto dto);

    /**
     * Converts an address DTO to the embedded address.
     *
     * @param dto the address DTO, can be null
     * @return the address, or null if none was sent
     */
    Address toEntity(AddressDto dto);

    // ---------------- helpers: entity -> its ID ----------------
    // MapStruct picks them by type wherever it must turn e.g. Publisher -> Long (also inside Sets)

    /**
     * @param publisher the publisher
     * @return its id
     */
    default Long publisherId(Publisher publisher) {
        return publisher.getId();
    }

    /**
     * @param author the author
     * @return its id
     */
    default Long authorId(Author author) {
        return author.getId();
    }

    /**
     * @param book the book
     * @return its ISBN
     */
    default String bookIsbn(Book book) {
        return book.getIsbn();
    }
}
