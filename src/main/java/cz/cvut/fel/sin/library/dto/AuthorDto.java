package cz.cvut.fel.sin.library.dto;

import java.util.Set;

/**
 * Author returned to the frontend (response).
 * Relations are sent only as IDs, never as whole objects (no endless nesting, no lazy loading problems).
 *
 * @param id           author id
 * @param firstName    first name
 * @param lastName     last name
 * @param email        e-mail, can be null
 * @param publisherIds ids of publishers the author has a contract with
 * @param bookIsbns    ISBNs of books the author wrote
 */
public record AuthorDto(
        Long id,
        String firstName,
        String lastName,
        String email,
        Set<Long> publisherIds,
        Set<String> bookIsbns
) {}
