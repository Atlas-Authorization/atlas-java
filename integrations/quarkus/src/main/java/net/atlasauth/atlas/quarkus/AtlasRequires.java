package net.atlasauth.atlas.quarkus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The authorization a session must satisfy on top of {@link AtlasAuthenticated}. Every non-empty
 * field must hold (an access token has no organization claims, so it only passes scope checks); a method-level annotation replaces the class-level one.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface AtlasRequires {
    /** The organization role. */
    String role() default "";

    /** An organization permission. */
    String permission() default "";

    /** At least one of these organization permissions. */
    String[] anyPermission() default {};

    /** All of these organization permissions. */
    String[] allPermissions() default {};

    /** An app-level role. */
    String appRole() default "";

    /** An app-level permission. */
    String appPermission() default "";

    /** Scopes an OAuth access token must carry (403 otherwise). A session has no scopes and never passes a non-empty list. */
    String[] scopes() default {};
}
