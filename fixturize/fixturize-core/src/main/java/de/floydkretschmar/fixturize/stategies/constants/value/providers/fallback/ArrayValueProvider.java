package de.floydkretschmar.fixturize.stategies.constants.value.providers.fallback;

import de.floydkretschmar.fixturize.domain.Names;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.ValueProvider;

import javax.lang.model.element.VariableElement;

public class ArrayValueProvider implements ValueProvider {
    @Override
    public String provideValueAsString(VariableElement field, Names names) {
        return "new %s {}".formatted(names.getQualifiedClassName());
    }
}
