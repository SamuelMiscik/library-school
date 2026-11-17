package cz.cvut.fel.sin.library.controller;

import cz.cvut.fel.sin.library.dto.BookDto;
import cz.cvut.fel.sin.library.dto.request.BookCreateDto;
import cz.cvut.fel.sin.library.service.PublisherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * REST endpoints for publishers. Only HTTP work here: read the request, call the service, return the DTO.
 */
@RestController // Spring: a controller whose return values are written as JSON
@RequestMapping("/publishers") // Spring: all URLs here start with /publishers
@Tag(name = "Publishers", description = "Publishers and the books they publish") // Swagger: group name in the UI
public class PublisherController {

    /** Business logic for publishers (injected by Spring). */
    private final PublisherService publisherService;

    /**
     * Constructor injection.
     *
     * @param publisherService publisher service
     */
    public PublisherController(PublisherService publisherService) {
        this.publisherService = publisherService;
    }

    /**
     * Publishes a new book.
     *
     * @param publisherId id of the publisher (URL)
     * @param dto         body with the book data and author ids
     * @return the created book
     */
    @PostMapping("/{publisherId}/books") // Spring: POST /publishers/{publisherId}/books
    @ResponseStatus(HttpStatus.CREATED)  // Spring: success = 201 Created
    @Operation(summary = "Publish a new book",
            description = "Every author must exist and have a contract with the publisher.")      // Swagger: endpoint description
    @ApiResponse(responseCode = "201", description = "Book published")                           // Swagger: documents this response
    @ApiResponse(responseCode = "400", description = "Invalid BookCreateDto")                    // Swagger: documents this response
    @ApiResponse(responseCode = "404", description = "Publisher or an author not found")         // Swagger: documents this response
    @ApiResponse(responseCode = "409", description = "ISBN exists or an author has no contract") // Swagger: documents this response
    public BookDto publishBook(
            @Parameter(description = "Publisher id", example = "1") // Swagger: describes the path parameter
            @PathVariable Long publisherId,                          // Spring: {publisherId} from the URL
            @Valid @RequestBody BookCreateDto dto) {                 // Spring: JSON body -> record, validated (400)
        return publisherService.publishBook(publisherId, dto);
    }
}
