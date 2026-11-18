package cz.cvut.fel.sin.library.controller.interceptors;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a controller method as mocked: {@link MockResponseInterceptor} returns {@link #body()}
 * right away and the method itself never runs.
 * <p>
 * Used for endpoints that are agreed with the frontend but not implemented yet.
 * Remove the annotation once the real implementation exists.
 */
@Retention(RetentionPolicy.RUNTIME) // Java: annotation is kept at run time, so the interceptor can read it
@Target(ElementType.METHOD)         // Java: may only be put on methods
public @interface MockResponse {

    /**
     * Static JSON returned to the client.
     *
     * @return response body
     */
    String body();

    /**
     * HTTP status of the mocked response.
     *
     * @return status code, 200 by default
     */
    int status() default 200;
}
