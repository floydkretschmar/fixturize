package de.floydkretschmar.fixturize.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a generated fixture should contain a method to create said fixture using the builder pattern. This
 * implies that the annotated class has the corresponding methods and classes to facilitate the use of the builder
 * pattern.
 *
 * @author Floyd Kretschmar
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Repeatable(FixtureBuilders.class)
public @interface FixtureBuilder {
    /**
     * Returns the name of the creation method.
     *
     * @return the name.
     */
    String methodName();

    /**
     * Returns the string representation of the setter methods used during for initialization of the builder object.
     *
     * @return The setter method representations
     */
    FixtureBuilderSetter[] usedSetters();

    /**
     * Returns the string representation of the static method used to create a builder object for the annotated class.
     *
     * @return The builder method representation
     */
    String builderMethod() default "builder";
}
