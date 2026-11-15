package cz.cvut.fel.sin.library.dto.request;

import jakarta.validation.constraints.NotNull;

/**
 * Request body for signing a contract ({@code POST /authors/{authorId}/contracts}).
 * The author is in the URL, the publisher in the body.
 *
 * @param publisherId id of the publisher, required
 */
public record ContractCreateDto(
        @NotNull Long publisherId   // 400 if missing ("publisherId missing" break in the diagram)
) {}
