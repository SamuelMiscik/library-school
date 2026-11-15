package cz.cvut.fel.sin.library.dto;

import java.util.List;

/**
 * Library returned to the frontend (response), including what it owns.
 *
 * @param id         library id
 * @param name       name
 * @param address    address, can be null
 * @param ownerships books the library owns, with the number of copies
 */
public record LibraryDto(
        Long id,
        String name,
        AddressDto address,
        List<OwnershipDto> ownerships
) {}
