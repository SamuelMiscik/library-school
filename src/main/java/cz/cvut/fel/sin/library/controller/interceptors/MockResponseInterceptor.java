package cz.cvut.fel.sin.library.controller.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Answers requests to methods annotated with {@link MockResponse} with their static body,
 * so the controller (and service, DB) is never called.
 */
@Component // Spring: creates one instance (bean) so it can be injected into the config
@Slf4j     // Lombok: generates "private static final Logger log" for this class
public class MockResponseInterceptor implements HandlerInterceptor {

    /**
     * Called before the controller. If the target method is mocked, writes the static body and stops.
     *
     * @param request  current HTTP request
     * @param response current HTTP response, the mock body is written here
     * @param handler  the controller method that would handle the request
     * @return {@code false} = request answered here, controller is skipped; {@code true} = continue normally
     * @throws IOException when the body cannot be written
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {
        // handler is a HandlerMethod for controller methods (other handlers, e.g. static files, are skipped)
        if (handler instanceof HandlerMethod method) {
            MockResponse mock = method.getMethodAnnotation(MockResponse.class);
            if (mock != null) {
                log.debug("Mocked response for {} {}", request.getMethod(), request.getRequestURI());
                response.setStatus(mock.status());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                response.getWriter().write(mock.body());
                return false; // STOP: the controller never runs
            }
        }
        return true; // not mocked -> continue to the controller
    }
}
