package cz.cvut.fel.sin.library.model;

import jakarta.persistence.Embeddable;

/**
 * Postal address (UML {@code <<dataType>>}).
 * <p>
 * Not an entity: it has no own table and no id. Its fields become columns
 * ({@code street, city, zip, country}) in the table of the entity that embeds it
 * (e.g. {@link Library}).
 *
 * @param street  street and house number
 * @param city    city
 * @param zip     postal code
 * @param country country
 */
@Embeddable// no own table, its fields become columns in the owner's table
public record Address(
        String street,
        String city,
        String zip,
        String country
) {}
