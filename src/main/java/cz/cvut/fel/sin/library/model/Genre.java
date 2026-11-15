package cz.cvut.fel.sin.library.model;

/**
 * Genre of a {@link Book}.
 * <p>
 * Stored in the DB as text (e.g. {@code "NOVEL"}) because {@code Book.genre}
 * uses {@code @Enumerated(EnumType.STRING)}.
 */
public enum Genre {
    NOVEL,
    POETRY,
    SCIENCE,
    CHILDREN
}
