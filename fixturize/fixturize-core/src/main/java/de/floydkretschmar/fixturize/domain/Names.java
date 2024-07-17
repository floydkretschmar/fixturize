package de.floydkretschmar.fixturize.domain;

import lombok.Builder;
import lombok.Value;

import javax.lang.model.element.TypeElement;

/**
 * Contains all names related to the generation of a fixture class.
 */
@Value
@Builder
public class Names {
    /**
     * The fully qualified name of the class for which the fixture is being generated
     */
    String qualifiedClassName;

    /**
     * The simple class name of the class for which the fixture is being generated
     */
    String simpleClassName;

    /**
     * The name of the package which both the original class and the generated fixture will be part of
     */
    String packageName;

    /**
     * The fully qualified name of the generated fixture class.
     */
    String qualifiedFixtureClassName;

    /**
     * Returns a boolean indicating if a package name exists, or if it is empty.
     *
     * @return true if the package name exists, false if it is an empty string
     */
    public boolean hasPackageName() {
        return !this.getPackageName().isEmpty();
    }

    public static Names from(TypeElement element) {
        final var qualifiedClassName = element.getQualifiedName().toString();
        final var lastDot = qualifiedClassName.lastIndexOf('.');
        var packageName = "";
        if (lastDot > 0) {
            packageName = qualifiedClassName.substring(0, lastDot);
        }
        final var simpleClassName = qualifiedClassName.substring(lastDot + 1);
        final var qualifiedFixtureClassName = qualifiedClassName + "Fixture";

        return Names.builder()
                .qualifiedClassName(qualifiedClassName)
                .simpleClassName(simpleClassName)
                .packageName(packageName)
                .qualifiedFixtureClassName(qualifiedFixtureClassName).build();
    }
}
