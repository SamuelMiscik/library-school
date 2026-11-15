package cz.cvut.fel.sin.library.dto.request;

import cz.cvut.fel.sin.library.dto.AddressDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

/**
 * Request body for creating a library ({@code POST /libraries}).
 *
 * @param name    name, required
 * @param address address, optional
 */
public record LibraryCreateDto(
        @NotBlank String name,      // 400 if missing / empty
        @Valid AddressDto address   // validate inside the address too
) {}
