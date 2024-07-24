package de.floydkretschmar.fixturize.domain;

import lombok.Builder;
import lombok.Getter;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * The metadata generated for a given {@link VariableElement}.
 *
 * @author Floyd Kretschmar
 */
@Builder
public class VariableElementMetadata {
    /**
     * The element which this metadata represents.
     */
    private VariableElement variableElement;

    /**
     * The element for which defines the type of the underlying variable element. If the variable element was typed by
     * a generic, the type defining element will be the {@link javax.lang.model.element.TypeElement} of the concrete
     * implementation class. If the variable element was typed by a non-generic, the type defining element will be the
     * original {@link VariableElement}.
     */
    @Getter
    private Element typedElement;

    /**
     * Returns the name of the underlying variable element.
     *
     * @return the name
     */
    public String getName() {
        return variableElement.getSimpleName().toString();
    }

    /**
     * Returns the modifiers of the underlying varibale element.
     *
     * @return the modifiers
     */
    public Set<Modifier> getModifiers() {
        return variableElement.getModifiers();
    }

    /**
     * Returns the constant value of the underlying variable element. Null if the variable element does not have a
     * defined constant value.
     *
     * @return the constant value
     */
    public Object getConstantValue() {
        return variableElement.getConstantValue();
    }

    /**
     * Returns the annotations of the underlying variable element of the given type.
     *
     * @param annotationClass - for which the annotations should be retrieved
     * @param <A>             - the type of the annotation
     * @return the annotations
     */
    public <A extends Annotation> A[] getAnnotationsByType(Class<A> annotationClass) {
        return variableElement.getAnnotationsByType(annotationClass);
    }

    @Override
    public String toString() {
        return variableElement.toString();
    }
}
