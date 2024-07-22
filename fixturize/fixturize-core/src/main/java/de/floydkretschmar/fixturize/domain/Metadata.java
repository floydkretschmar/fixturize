package de.floydkretschmar.fixturize.domain;

import lombok.Builder;
import lombok.Value;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.Map;

/**
 * Contains all names related to the generation of a fixture class.
 */
@Value
@Builder
public class Metadata {
    /**
     * The fully qualified name of the class.
     */
    String qualifiedClassName;

    /**
     * The fully qualified name of the class but with the generics erased.
     */
    String qualifiedClassNameWithoutGeneric;

    /**
     * The simple class name of the class
     */
    String simpleClassName;

    /**
     * The simple class name of the class but with the generics erased.
     */
    String simpleClassNameWithoutGeneric;

    /**
     * The name of the package of the class. Empty if the class is located in the base package.
     */
    String packageName;

    /**
     * The fully qualified name of the corresponding fixture class.
     */
    String qualifiedFixtureClassName;

    /**
     * Only the generics part of the class definition. Empty if the class is not a generic.
     */
    String genericPart;

    /**
     * The map that contains the relation between generic types and their concrete implementation for the class. Empty if
     * the class is not a generic.
     */
    Map<? extends TypeMirror, ? extends DeclaredType> genericTypeMap;

    /**
     * Returns a boolean indicating if a package name exists, or if it is empty.
     *
     * @return true if the package name exists, false if it is an empty string
     */
    public boolean hasPackageName() {
        return !this.getPackageName().isEmpty();
    }

    public boolean isGeneric() {
        return !this.genericPart.isEmpty();
    }

    public List<ElementMetadata> createElementMetadata(List<? extends VariableElement> elements) {
        return elements.stream().map(field -> {
            Element returnValue = field;
            if (this.getGenericTypeMap().containsKey(field.asType()))
                returnValue = this.getGenericTypeMap().get(field.asType()).asElement();

            return ElementMetadata.builder()
                    .field(field)
                    .element(returnValue)
                    .build();
        }).toList();
    }
}
