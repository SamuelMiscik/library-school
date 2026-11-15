package cz.cvut.fel.sin.library.dto;

/**
 * Address as it is sent to / from the frontend.
 * <p>
 * Same fields as the embedded {@link cz.cvut.fel.sin.library.model.Address},
 * but a separate class, so the API does not depend on the model.
 *
 * @param street  street and house number
 * @param city    city
 * @param zip     postal code
 * @param country country
 */
public record AddressDto(
        String street,
        String city,
        String zip,
        String country
) {}
