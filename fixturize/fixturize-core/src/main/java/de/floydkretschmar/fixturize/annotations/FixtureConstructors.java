package de.floydkretschmar.fixturize.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotation container that allows the repeated use of {@link FixtureConstructor}.
 *
 * @author Floyd Kretschmar
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface FixtureConstructors {
    /**
     * Returns all instances of {@link FixtureConstructor} with which the target has been annotated.
     * @return all fixture constant annotations
     */
    FixtureConstructor[] value();
}