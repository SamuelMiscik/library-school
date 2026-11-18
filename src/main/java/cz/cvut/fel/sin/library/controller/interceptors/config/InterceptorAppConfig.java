package cz.cvut.fel.sin.library.controller.interceptors.config;

import cz.cvut.fel.sin.library.controller.interceptors.MockResponseInterceptor;
import cz.cvut.fel.sin.library.controller.interceptors.TimeLoggerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Registers interceptors in Spring MVC. Without this, Spring never calls them.
 */
@Component // Spring: bean; Spring MVC finds every WebMvcConfigurer bean and applies it
public class InterceptorAppConfig implements WebMvcConfigurer {

    /** Interceptor that logs request duration (injected by Spring). */
    private final TimeLoggerInterceptor timeLoggerInterceptor;

    /** Interceptor that answers {@code @MockResponse} methods with static JSON (injected by Spring). */
    private final MockResponseInterceptor mockResponseInterceptor;

    /**
     * Constructor injection.
     *
     * @param timeLoggerInterceptor   the timing interceptor
     * @param mockResponseInterceptor the mock interceptor
     */
    @Autowired // Spring: inject via this constructor (optional when there is only one constructor)
    public InterceptorAppConfig(TimeLoggerInterceptor timeLoggerInterceptor,
                                MockResponseInterceptor mockResponseInterceptor) {
        this.timeLoggerInterceptor = timeLoggerInterceptor;
        this.mockResponseInterceptor = mockResponseInterceptor;
    }

    /**
     * Adds the interceptors for all URLs. Order matters: the timer is first, so mocked
     * requests are timed and logged too.
     *
     * @param registry Spring's list of interceptors
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(timeLoggerInterceptor);
        registry.addInterceptor(mockResponseInterceptor);
    }
}
