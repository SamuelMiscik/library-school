package cz.cvut.fel.sin.library.controller.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * Logs how long every HTTP request took, e.g. {@code POST /authors/5/contracts -> 201 (23 ms)}.
 * <p>
 * Runs between the DispatcherServlet and the controller. The start time is stored as a request
 * attribute (not a field), because this one bean serves all requests at the same time.
 * Registered in {@link cz.cvut.fel.sin.library.controller.interceptors.config.InterceptorAppConfig}.
 */
@Component // Spring: creates one instance (bean) so it can be injected into the config
@Slf4j     // Lombok: generates "private static final Logger log" for this class
public class TimeLoggerInterceptor implements HandlerInterceptor {

    /**
     * Called before the controller. Remembers the start time on this request.
     *
     * @param request  current HTTP request (the same object reaches {@link #afterCompletion})
     * @param response current HTTP response
     * @param handler  the controller method that will handle the request
     * @return always {@code true} = continue to the controller
     */
    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {

        request.setAttribute("startTime", System.nanoTime()); // put the note ON this request
        return true;
    }

    /**
     * Called after the response is finished, also when the controller threw an exception.
     * Reads the start time back and logs method, URI, status and duration.
     *
     * @param request  the same request as in {@link #preHandle}
     * @param response finished response (status is already set)
     * @param handler  the controller method that handled the request
     * @param ex       exception that was not handled, otherwise {@code null}
     */
    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception ex) throws Exception {

        long startTime = (Long) request.getAttribute("startTime"); // read the note back from the SAME request
        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        long ms = duration / 1_000_000; // convert to milliseconds
        log.info("{} {} -> {} ({} ms)", request.getMethod(), request.getRequestURI(), response.getStatus(), ms);
    }
}
