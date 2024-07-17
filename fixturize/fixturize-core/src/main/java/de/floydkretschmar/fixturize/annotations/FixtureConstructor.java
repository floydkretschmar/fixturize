package de.floydkretschmar.fixturize.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a generated fixture should contain a method to create said fixture using the specified constructor. This
 * implies that the annotated class has the corresponding constructor to facilitate the use of said constructor for
 * fixture generation.
 *
 * @author Floyd Kretschmar
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Repeatable(FixtureConstructors.class)
public @interface FixtureConstructor {
    /**
     * Returns the string representation of the parameters defining the constructor that should be used for the creation
     * of the fixture. The parameters have to be in the same order in the constructor that is being referenced. The value
     * itself has to be either the name of the corresponding field or {@link FixtureConstant#name()} if specified.
     * @return the constructor parameter representations
     */
    String[] constructorParameters();
}
