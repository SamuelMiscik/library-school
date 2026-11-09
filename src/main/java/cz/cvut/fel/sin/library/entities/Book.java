package cz.cvut.fel.sin.library.entities;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A book title (not a physical copy) in the library system.
 * <p>
 * DB table: {@code book}. One book can be owned by many libraries,
 * each through one {@link Ownership} row (which also stores the number of copies).
 * Every book has exactly one {@link Publisher} (N:1) and at least one {@link Author}
 * (N:M, join table {@code writes}).
 */
@Entity // this class is a table
@Table(name = "book") // DB: table name "book"
@Getter @Setter // Lombok: generates getters + setters for all fields
@NoArgsConstructor // Lombok: empty constructor, JPA needs it to create objects from DB rows
public class Book {

    /** ISBN of the book = primary key (natural key), set by us, not generated. */
    @Id // DB: PRIMARY KEY (-> automatically UNIQUE + NOT NULL, so one ISBN only once in the system)
    // no @GeneratedValue: the ISBN already exists in the real world, we must set it before save
    @NotBlank // Java: must not be null / empty / spaces
    @Column(name = "isbn", length = 17) // DB: column "isbn" VARCHAR(17) (e.g. "978-80-7200-123-4")
    private String isbn;

    /** Title of the book, required. */
    @NotBlank // Bean Validation: not null, not empty, not just spaces --> IN JAVA ONLY FOR STRINGS
    @Column(nullable = false) // DB constraint: NOT NULL --> IN DB
    private String title;

    /** Date when the book was published (optional). */
    @Column(name = "publication_date") // DB: column "publication_date" of type DATE (no time), can be NULL
    private LocalDate publicationDate; // LocalDate = only date (year, month, day) -> DB DATE

    /** The genre of the book. */
    @Enumerated(EnumType.STRING) // DB: store as VARCHAR (the enum name, e.g. "NOVEL"), not as a number 0,1,2...
    private Genre genre;

    /** The publisher of the book, required. */
    @NotNull // Java: must be set before save (@NotBlank is only for Strings, for objects use @NotNull)
    // Book "0..*" -- "1" Publisher  (many books -> one publisher)
    // owning side: this table holds the FK publisher_id
    @ManyToOne(fetch = FetchType.LAZY, optional = false) // many books -> one publisher
    // fetch = LAZY: publisher is loaded from DB only when you call getPublisher()
    // optional = false: JPA knows every book always has a publisher (can use INNER JOIN)
    // DB: column "publisher_id" = FOREIGN KEY -> publisher(PK), NOT NULL
    // (NOT unique: many books can have the same publisher)
    @JoinColumn(name = "publisher_id", nullable = false)
    private Publisher publisher;

    /** Authors of this book, at least one. */
    // Book "1..*" -- "0..*" Author  (N:M)
    // OWNING side of "writes": this field defines the join table
    //   (the other side, Author.books, has mappedBy = "authors")
    // fetch: @ManyToMany is LAZY by default
    // NO cascade: deleting a book must not delete its authors (they wrote other books too)
    //   but deleting a book DOES delete its rows in "writes" automatically (this is the owning side)
    @NotEmpty // Java: at least 1 author (UML "1..*")
    @ManyToMany
    // @JoinTable: DB creates table "writes"(isbn, author_id), PK = both columns
    @JoinTable(
            name = "writes",
            joinColumns = @JoinColumn(name = "isbn"),             // FK -> book(isbn)          (this side)
            inverseJoinColumns = @JoinColumn(name = "author_id")  // FK -> author(author_id)   (other side)
    )
    private Set<Author> authors = new HashSet<>();

    /** Libraries that own this book (with number of copies), one {@link Ownership} per library. */
    // Book "1" -- "0..*" Ownership
    // mappedBy = "book": the FK (book_id) lives in Ownership, this list is only a mirror
    //   -> DB: NO column in "book" table for this, it's just read from ownership.book_id
    // cascade = ALL: save/delete of Book is also done on its Ownerships
    // orphanRemoval = true: Ownership removed from this list -> DELETE its row from DB
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ownership> ownerships = new ArrayList<>();

    /**
     * Two books are equal when they have the same ISBN.
     *
     * @param o the other object
     * @return true if it is the same book (same ISBN)
     */
    // equals/hashCode BY ID (here the ISBN): needed once entities sit in Sets (e.g. Author.books)
    // if 2 have the same ISBN, they are the same book (even if other fields differ)
    @Override
    public boolean equals(Object o) {
        // same object in memory -> obviously equal
        if (this == o) return true;
        // other object is not a Book (or is null) -> not equal
        // (also casts it into variable "other" so we can use it below)
        if (!(o instanceof Book other)) return false;
        // equal only if both have the same ISBN
        // isbn != null: a book without ISBN yet -> equal only to itself (checked above)
        return isbn != null && isbn.equals(other.isbn);
    }

    /**
     * Constant hash code, kept the same way as in the other entities.
     *
     * @return hash code of the class
     */
    // if 2 objects are equal, they must have the same hashCode
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    // Helper methods for authors: change BOTH sides of the N:M at once (this.authors + author.books).
    // YOU call these in the Service layer.

    /**
     * Adds an author to this book (both sides).
     *
     * @param author the author who wrote this book
     */
    public void addAuthor(Author author) {
        authors.add(author);            // owning side -> DB: INSERT INTO writes
        author.getBooks().add(this);    // mirror side (Java only)
    }

    /**
     * Removes an author from this book (both sides).
     *
     * @param author the author to remove
     */
    public void removeAuthor(Author author) {
        authors.remove(author);           // owning side -> DB: DELETE FROM writes
        author.getBooks().remove(this);   // mirror side (Java only)
    }

    // No add/remove helpers for ownerships here: they are managed by Ownership itself
    //   create: new Ownership(library, book, copies)  -> adds itself to both lists
    //   delete: ownership.remove()                    -> removes itself from both lists
}
