package cz.cvut.fel.sin.library.controller;

import cz.cvut.fel.sin.library.dto.AuthorDto;
import cz.cvut.fel.sin.library.dto.request.ContractCreateDto;
import cz.cvut.fel.sin.library.service.AuthorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * REST endpoints for authors. Only HTTP work here: read the request, call the service, return the DTO.
 * Validation errors (400) and service exceptions (404/409) are turned into responses by
 * {@link cz.cvut.fel.sin.library.exception.RestExceptionHandler}.
 */
@RestController // Spring: a controller whose return values are written as JSON
@RequestMapping("/authors") // Spring: all URLs here start with /authors
@Tag(name = "Authors", description = "Authors and their contracts with publishers") // Swagger: group name in the UI
public class AuthorController {

    /** Business logic for authors (injected by Spring). */
    private final AuthorService authorService;

    /**
     * Constructor injection.
     *
     * @param authorService author service
     */
    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    /**
     * Signs a contract between the author and a publisher.
     *
     * @param authorId id of the author (URL)
     * @param dto      body with the publisher id
     * @return the author with the new contract
     */
    @PostMapping("/{authorId}/contracts") // Spring: POST /authors/{authorId}/contracts
    @ResponseStatus(HttpStatus.CREATED)   // Spring: success = 201 Created (a new contract was created)
    @Operation(summary = "Sign a contract with a publisher")                         // Swagger: one-line description
    @ApiResponse(responseCode = "201", description = "Contract signed")               // Swagger: documents this response
    @ApiResponse(responseCode = "400", description = "publisherId missing")           // Swagger: documents this response
    @ApiResponse(responseCode = "404", description = "Author or publisher not found") // Swagger: documents this response
    @ApiResponse(responseCode = "409", description = "Contract already exists")       // Swagger: documents this response
    public AuthorDto signContract(
            @Parameter(description = "Author id", example = "1") // Swagger: describes the path parameter
            @PathVariable Long authorId,                          // Spring: {authorId} from the URL
            @Valid @RequestBody ContractCreateDto dto) {          // Spring: JSON body -> record, validated (400)
        return authorService.signContract(authorId, dto.publisherId());
    }
}
