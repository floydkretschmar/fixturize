package de.floydkretschmar.fixturize.domain;

import lombok.Builder;
import lombok.Getter;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import java.lang.annotation.Annotation;
import java.util.Set;

@Builder
public class ElementMetadata {
    private VariableElement field;

    @Getter
    private Element element;

    public String getName() {
        return field.getSimpleName().toString();
    }

    public Set<Modifier> getModifiers() {
        return field.getModifiers();
    }

    public Object getConstantValue() {
        return field.getConstantValue();
    }

    public <A extends Annotation> A[] getAnnotationsByType(Class<A> annotationClass) {
        return field.getAnnotationsByType(annotationClass);
    }

    @Override
    public String toString() {
        return field.toString();
    }
}
