package de.floydkretschmar.fixturize.stategies.constants.value;

import javax.lang.model.element.VariableElement;

public interface ValueProviderService {
    String getValueFor(VariableElement field);
}
