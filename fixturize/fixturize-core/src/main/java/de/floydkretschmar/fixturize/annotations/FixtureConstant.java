package de.floydkretschmar.fixturize.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/***
 * Defines how an annotated field should be transformed into a corresponding constant in the generated fixture.
 *
 * @author Floyd Kretschmar
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
@Repeatable(FixtureConstants.class)
public @interface FixtureConstant {
    /***
     * Returns the name of the constant in the generated fixture.
     * @return the constant name
     */
    String name();

    /**
     * Returns the string representation of the value that the constant should take in the generated fixture. This could be a
     * static value as well as valid inline java code.
     *
     * @return the representation of the constant value
     */
    String value() default "";
}
