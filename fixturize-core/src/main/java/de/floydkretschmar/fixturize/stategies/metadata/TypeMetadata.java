package de.floydkretschmar.fixturize.stategies.metadata;

import lombok.Builder;
import lombok.Value;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.Map;

/**
 * Contains all names related to a specific type.
 */
@Value
@Builder
public class TypeMetadata {
    /**
     * The fully qualified name of the type.
     */
    String qualifiedClassName;

    /**
     * The fully qualified name of the type but with the generics erased.
     */
    String qualifiedClassNameWithoutGeneric;

    /**
     * The simple class name of the type
     */
    String simpleClassName;

    /**
     * The simple class name of the type but with the generics erased.
     */
    String simpleClassNameWithoutGeneric;

    /**
     * The name of the package of the type. Empty if the class is located in the base package.
     */
    String packageName;

    /**
     * The fully qualified name of the corresponding fixture class for the type.
     */
    String qualifiedFixtureClassName;

    /**
     * Only the generic part of the class definition. Empty if the type is not a generic.
     */
    String genericPart;

    /**
     * The map that contains the relation between generic types and their concrete implementation for the type. Empty if
     * the type is not a generic.
     */
    Map<? extends TypeMirror, ? extends DeclaredType> genericTypeMap;

    /**
     * Returns a boolean indicating if a package name exists, or if it is part of the base package.
     *
     * @return true if the package name exists, false if it is an empty string
     */
    public boolean hasPackageName() {
        return !this.getPackageName().isEmpty();
    }

    /**
     * Returns a boolean indicating if if the underlying type is a generic or not.
     *
     * @return true if the underlying type is a generic, false otherwise
     */
    public boolean isGeneric() {
        return !this.genericPart.isEmpty();
    }

    /**
     * Creates metadata for the specified variable elements using this type metadata.
     *
     * @param elements - for which metadata will be created
     * @return the variable elements metadata
     */
    public List<VariableElementMetadata> createVariableElementMetadata(List<? extends VariableElement> elements) {
        return elements.stream().map(field -> {
            Element returnValue = field;
            if (this.getGenericTypeMap().containsKey(field.asType()))
                returnValue = this.getGenericTypeMap().get(field.asType()).asElement();

            return VariableElementMetadata.builder()
                    .variableElement(field)
                    .typedElement(returnValue)
                    .build();
        }).toList();
    }
}
