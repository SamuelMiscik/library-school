package cz.cvut.fel.sin.library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point of the Library application.
 * <p>
 * {@code @SpringBootApplication} turns on auto-configuration (DB, JPA, web server...)
 * and scans this package and its subpackages ({@code entities}, ...) for Spring components and entities.
 */
@SpringBootApplication
public class LibraryApplication {

	/**
	 * Starts the Spring Boot application.
	 *
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(LibraryApplication.class, args);
	}

}
