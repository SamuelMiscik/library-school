package cz.cvut.fel.sin.library;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Smoke test: checks that the whole Spring application (incl. DB and JPA mapping) starts.
 */
@SpringBootTest // starts the full application context for the test
class LibraryApplicationTests {

	/** Passes if the application context starts without errors (e.g. wrong entity mapping). */
	@Test
	void contextLoads() {
	}

}
