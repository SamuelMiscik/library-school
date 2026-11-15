package cz.cvut.fel.sin.library.dto;

import java.util.Set;

/**
 * Publisher returned to the frontend (response).
 *
 * @param id        publisher id
 * @param name      name
 * @param address   address, can be null
 * @param authorIds ids of authors with a contract
 * @param bookIsbns ISBNs of published books
 */
public record PublisherDto(
        Long id,
        String name,
        AddressDto address,
        Set<Long> authorIds,
        Set<String> bookIsbns
) {}
