package de.floydkretschmar.fixturize.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a fixture should be generated for the annotated object. The fixture will be generated according to the
 * strategies defined through the other annotations of this library.
 *
 * @author Floyd Kretschmar
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface Fixture {
    /**
     * Returns the list of class names that should be used for implementation of a fixture for a generic class. For non-generic
     * classes this will be ignored/empty.
     */
    String[] genericImplementations() default {};
}