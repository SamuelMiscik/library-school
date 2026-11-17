package cz.cvut.fel.sin.library.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Header of the generated OpenAPI document (title, description, version).
 * <p>
 * Swagger only: without this class springdoc still works, it just shows a default title.
 * <ul>
 *   <li>OpenAPI JSON: {@code http://localhost:8080/v3/api-docs}</li>
 *   <li>OpenAPI YAML: {@code http://localhost:8080/v3/api-docs.yaml}</li>
 *   <li>Swagger UI:   {@code http://localhost:8080/swagger-ui.html}</li>
 * </ul>
 */
@Configuration // Spring: this class defines beans
public class OpenApiConfig {

    /**
     * The info block shown at the top of Swagger UI.
     *
     * @return OpenAPI model with title, description and version
     */
    @Bean // Swagger: springdoc picks up this bean and uses it as the document header
    public OpenAPI libraryOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Library API")
                .description("Central information system for libraries: authors, publishers, books, ownerships.")
                .version("0.0.3"));
    }
}
