package cz.cvut.fel.sin.library.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Request body for creating an author ({@code POST /authors}).
 * The id is NOT here, the DB generates it.
 *
 * @param firstName first name, required
 * @param lastName  last name, required
 * @param email     e-mail, optional
 */
public record AuthorCreateDto(
        @NotBlank String firstName, // 400 if missing / empty
        @NotBlank String lastName,  // 400 if missing / empty
        @Email String email         // 400 if not an e-mail (null is OK)
) {}
