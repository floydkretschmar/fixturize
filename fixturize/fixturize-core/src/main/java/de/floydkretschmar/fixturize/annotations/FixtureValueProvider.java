package de.floydkretschmar.fixturize.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines a custom strategy used to provide all values matching the specified target type. There are three types for
 * which custom value providers can be defined:
 * <ul>
 *     <li>{@link javax.lang.model.element.ElementKind}</li>
 *     <li>{@link javax.lang.model.type.TypeKind}</li>
 *     <li>on a per class level</li>
 * </ul>
 *
 * @author Floyd Kretschmar
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Repeatable(FixtureValueProviders.class)
public @interface FixtureValueProvider {
    /**
     * Returns the string representation of the type for which a strategy should be registered. Valid types for registration
     * are:
     * <ul>
     *     <li>{@link javax.lang.model.element.ElementKind}</li>
     *     <li>{@link javax.lang.model.type.TypeKind}</li>
     *     <li>on a per class level</li>
     * </ul>
     *
     * @return the target type representation
     */
    String targetType();

    /**
     * Returns the code that should be executed to provide the value for all constants
     * of the same type as specified by {@link FixtureValueProvider#targetType()}. The line of code can execute information
     * about the field for which the value should be provided via the variable <b>field</b> of type {@link javax.lang.model.element.VariableElement}.
     *
     * @return the code to provide values for all constants of a type
     */
    String valueProviderCallback();
}
