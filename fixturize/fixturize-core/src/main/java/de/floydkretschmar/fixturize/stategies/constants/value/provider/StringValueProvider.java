package de.floydkretschmar.fixturize.stategies.constants.value.provider;

import com.google.common.base.CaseFormat;

import javax.lang.model.element.VariableElement;

public class StringValueProvider implements ValueProvider<VariableElement> {
    @Override
    public String provideValueAsString(VariableElement field) {
        return "\"%s_VALUE\"".formatted(CaseFormat.LOWER_CAMEL.to(
                CaseFormat.UPPER_UNDERSCORE, field.getSimpleName().toString()));
    }
}
