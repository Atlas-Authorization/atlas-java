package net.atlasauth.atlas.quarkus;

import jakarta.ws.rs.NameBinding;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Requires a verified Atlas session on a resource class or method. Without one the request is
 * answered {@code 401}. Combine with {@link AtlasRequires} to also require a permission or role
 * ({@code 403} when it is missing).
 */
@NameBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface AtlasAuthenticated {
}
