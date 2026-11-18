package cz.cvut.fel.sin.library.controller;

import cz.cvut.fel.sin.library.controller.interceptors.MockResponse;
import cz.cvut.fel.sin.library.dto.LibraryDto;
import cz.cvut.fel.sin.library.dto.OwnershipDto;
import cz.cvut.fel.sin.library.dto.request.AddBookDto;
import cz.cvut.fel.sin.library.service.LibraryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * REST endpoints for libraries. Only HTTP work here: read the request, call the service, return the DTO.
 */
@RestController // Spring: a controller whose return values are written as JSON
@RequestMapping("/libraries") // Spring: all URLs here start with /libraries
@Tag(name = "Libraries", description = "Libraries and the books they own") // Swagger: group name in the UI
public class LibraryController {

    /** Business logic for libraries (injected by Spring). */
    private final LibraryService libraryService;

    /**
     * Constructor injection.
     *
     * @param libraryService library service
     */
    public LibraryController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    /**
     * Adds an existing book to the library.
     *
     * @param libraryId id of the library (URL)
     * @param dto       body with the ISBN and the number of copies
     * @return the created ownership
     */
    @PostMapping("/{libraryId}/books") // Spring: POST /libraries/{libraryId}/books
    @ResponseStatus(HttpStatus.CREATED) // Spring: success = 201 Created
    @Operation(summary = "Add a book to the library")                                   // Swagger: one-line description
    @ApiResponse(responseCode = "201", description = "Book added")                     // Swagger: documents this response
    @ApiResponse(responseCode = "400", description = "ISBN missing or copies not 1-5") // Swagger: documents this response
    @ApiResponse(responseCode = "404", description = "Library or book not found")      // Swagger: documents this response
    @ApiResponse(responseCode = "409", description = "Library already owns the book")  // Swagger: documents this response
    public OwnershipDto addBook(
            @Parameter(description = "Library id", example = "1") // Swagger: describes the path parameter
            @PathVariable Long libraryId,                          // Spring: {libraryId} from the URL
            @Valid @RequestBody AddBookDto dto) {                  // Spring: JSON body -> record, validated (400)
        return libraryService.addBookToLibrary(libraryId, dto);
    }

    /**
     * Returns one library with the books it owns. <b>Mocked</b>: answered by
     * {@link cz.cvut.fel.sin.library.controller.interceptors.MockResponseInterceptor} with static JSON,
     * the method body never runs.
     *
     * @param libraryId id of the library (URL), ignored while mocked
     * @return the library
     */
    @GetMapping("/{libraryId}") // Spring: GET /libraries/{libraryId}
    @MockResponse(body = """
            {"id":1,"name":"Městská knihovna Praha",
             "address":{"street":"Mariánské nám. 1","city":"Praha","zip":"11000","country":"CZ"},
             "ownerships":[{"id":1,"libraryId":1,"bookIsbn":"978-80-7335-001-1","copies":3}]}
            """) // Mock: static body returned instead of calling this method
    @Operation(summary = "Get a library (mocked)",
            description = "Returns static data until the real implementation exists.") // Swagger: endpoint description
    @ApiResponse(responseCode = "200", description = "Library (static mock data)")     // Swagger: documents this response
    public LibraryDto getLibrary(
            @Parameter(description = "Library id", example = "1") // Swagger: describes the path parameter
            @PathVariable Long libraryId) {                        // Spring: {libraryId} from the URL
        throw new UnsupportedOperationException("Mocked by @MockResponse, not implemented yet"); // if anyone deletes the annotatipns, exception will show up
    }
}
