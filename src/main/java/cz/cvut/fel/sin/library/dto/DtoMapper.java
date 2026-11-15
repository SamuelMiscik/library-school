package cz.cvut.fel.sin.library.dto;

import cz.cvut.fel.sin.library.dto.request.AuthorCreateDto;
import cz.cvut.fel.sin.library.dto.request.BookCreateDto;
import cz.cvut.fel.sin.library.dto.request.LibraryCreateDto;
import cz.cvut.fel.sin.library.dto.request.PublisherCreateDto;
import cz.cvut.fel.sin.library.model.*;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Converts entities to DTOs (way OUT) and request DTOs to entities (way IN).
 * <p>
 * Hand-written version of what MapStruct would generate. It only copies plain data:
 * it never calls a repository, so relations (publisher, authors, library, book) are
 * NOT set by {@code toEntity} methods; the service loads them from the DB and sets them.
 * <p>
 * Response methods read lazy relations (e.g. {@code book.getAuthors()}), so call them
 * while the Hibernate session is still open (inside the service transaction, or in the
 * controller with Spring's default open-in-view).
 */
@Component // Spring creates ONE instance and injects it where needed (like a service)
public class DtoMapper {

    // ---------------- entity -> DTO (response) ----------------

    /**
     * Converts an author to its DTO.
     *
     * @param author the entity
     * @return the DTO, relations as IDs
     */
    public AuthorDto toDto(Author author) {
        return new AuthorDto(
                author.getId(),
                author.getFirstName(),
                author.getLastName(),
                author.getEmail(),
                author.getPublishers().stream().map(Publisher::getId).collect(Collectors.toSet()),
                author.getBooks().stream().map(Book::getIsbn).collect(Collectors.toSet())
        );
    }

    /**
     * Converts a book to its DTO.
     *
     * @param book the entity
     * @return the DTO, publisher and authors as IDs
     */
    public BookDto toDto(Book book) {
        return new BookDto(
                book.getIsbn(),
                book.getTitle(),
                book.getPublicationDate(),
                book.getGenre(),
                book.getPublisher() == null ? null : book.getPublisher().getId(),
                book.getAuthors().stream().map(Author::getId).collect(Collectors.toSet())
        );
    }

    /**
     * Converts a publisher to its DTO.
     *
     * @param publisher the entity
     * @return the DTO, authors and books as IDs
     */
    public PublisherDto toDto(Publisher publisher) {
        return new PublisherDto(
                publisher.getId(),
                publisher.getName(),
                toDto(publisher.getAddress()),
                publisher.getAuthors().stream().map(Author::getId).collect(Collectors.toSet()),
                publisher.getBooks().stream().map(Book::getIsbn).collect(Collectors.toSet())
        );
    }

    /**
     * Converts a library to its DTO, including its ownerships.
     *
     * @param library the entity
     * @return the DTO
     */
    public LibraryDto toDto(Library library) {
        return new LibraryDto(
                library.getId(),
                library.getName(),
                toDto(library.getAddress()),
                library.getOwnerships().stream().map(this::toDto).toList()
        );
    }

    /**
     * Converts an ownership to its DTO.
     *
     * @param ownership the entity
     * @return the DTO, library and book as IDs
     */
    public OwnershipDto toDto(Ownership ownership) {
        return new OwnershipDto(
                ownership.getId(),
                ownership.getLibrary().getId(),
                ownership.getBook().getIsbn(),
                ownership.getCopies()
        );
    }

    /**
     * Converts an embedded address to its DTO.
     *
     * @param address the address, can be null
     * @return the DTO, or null if there is no address
     */
    public AddressDto toDto(Address address) {
        if (address == null) return null;
        return new AddressDto(address.street(), address.city(), address.zip(), address.country());
    }

    // ---------------- request DTO -> entity (way IN) ----------------

    /**
     * Creates a new (unsaved) author from the request.
     *
     * @param dto the request body
     * @return new entity without id (the DB generates it on save)
     */
    public Author toEntity(AuthorCreateDto dto) {
        Author author = new Author();
        author.setFirstName(dto.firstName());
        author.setLastName(dto.lastName());
        author.setEmail(dto.email());
        return author;
    }

    /**
     * Creates a new (unsaved) book from the request, only the plain fields.
     * The service must still call {@code setPublisher(...)} and {@code addAuthor(...)}
     * with entities loaded from the DB ({@code dto.authorIds()} are only numbers).
     *
     * @param dto the request body
     * @return new entity without publisher and authors
     */
    public Book toEntity(BookCreateDto dto) {
        Book book = new Book();
        book.setIsbn(dto.isbn());
        book.setTitle(dto.title());
        book.setPublicationDate(dto.publicationDate());
        book.setGenre(dto.genre());
        return book;
    }

    /**
     * Creates a new (unsaved) publisher from the request.
     *
     * @param dto the request body
     * @return new entity without id
     */
    public Publisher toEntity(PublisherCreateDto dto) {
        Publisher publisher = new Publisher();
        publisher.setName(dto.name());
        publisher.setAddress(toEntity(dto.address()));
        return publisher;
    }

    /**
     * Creates a new (unsaved) library from the request.
     *
     * @param dto the request body
     * @return new entity without id
     */
    public Library toEntity(LibraryCreateDto dto) {
        Library library = new Library();
        library.setName(dto.name());
        library.setAddress(toEntity(dto.address()));
        return library;
    }

    /**
     * Converts an address DTO to the embedded address.
     *
     * @param dto the address DTO, can be null
     * @return the address, or null if none was sent
     */
    public Address toEntity(AddressDto dto) {
        if (dto == null) return null;
        return new Address(dto.street(), dto.city(), dto.zip(), dto.country());
    }
}
