package cz.cvut.fel.sin.library.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

/**
 * Turns exceptions thrown anywhere below the controllers into HTTP responses.
 * <p>
 * Body = {@link ProblemDetail} (standard RFC 9457 error JSON), e.g.
 * {@code {"title":"Not Found","status":404,"detail":"Author 5 not found"}}
 */
@Slf4j // logger of this class
@RestControllerAdvice // Spring: watches ALL controllers, catches their exceptions, return value -> JSON
public class RestExceptionHandler {

    /**
     * Entity does not exist -> 404. Already logged as warn in the service.
     *
     * @param ex the exception
     * @return 404 problem detail with the precise message
     */
    @ExceptionHandler(NotFoundException.class) // Spring: called when NotFoundException flies out of a controller
    @ResponseStatus(HttpStatus.NOT_FOUND)      // Spring: HTTP status; Swagger: also listed as a possible response
    public ProblemDetail handleNotFound(NotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * Conflict with current data (duplicate, missing contract) -> 409. Already logged as warn in the service.
     *
     * @param ex the exception
     * @return 409 problem detail with the precise message
     */
    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)       // Spring: HTTP status; Swagger: also listed as a possible response
    public ProblemDetail handleConflict(ConflictException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }

    /**
     * Invalid request body ({@code @Valid} failed) -> 400 with the list of wrong fields.
     *
     * @param ex the validation exception
     * @return 400 problem detail, e.g. "copies: must be less than or equal to 5"
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)    // Spring: HTTP status; Swagger: also listed as a possible response
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        String detail = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.warn("Invalid request: {}", detail);
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
    }

    /**
     * Body is not valid JSON or has a wrong type (e.g. "copies": "abc") -> 400.
     * Without this it would fall into the 500 handler below.
     *
     * @param ex the parsing exception
     * @return 400 problem detail
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleUnreadable(HttpMessageNotReadableException ex) {
        log.warn("Unreadable request body: {}", ex.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Malformed JSON request body");
    }

    /**
     * Path variable has a wrong type (e.g. /authors/abc/contracts) -> 400.
     *
     * @param ex the conversion exception
     * @return 400 problem detail naming the parameter
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.warn("Wrong type of parameter {}: {}", ex.getName(), ex.getValue());
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "Parameter '" + ex.getName() + "' has a wrong type: " + ex.getValue());
    }

    /**
     * Anything unexpected (a bug, DB down) -> 500. Logged as ERROR with the stack trace;
     * the client gets no internal details.
     *
     * @param ex the exception
     * @return 500 problem detail with a generic message
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ProblemDetail handleUnexpected(Exception ex) {
        log.error("Unexpected error", ex); // exception last -> whole stack trace in the log
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
    }
}
