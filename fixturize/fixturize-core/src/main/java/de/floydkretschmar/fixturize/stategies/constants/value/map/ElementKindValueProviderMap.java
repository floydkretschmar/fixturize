package de.floydkretschmar.fixturize.stategies.constants.value.map;

import de.floydkretschmar.fixturize.stategies.constants.value.provider.EnumValueProvider;
import de.floydkretschmar.fixturize.stategies.constants.value.provider.ValueProvider;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.VariableElement;
import java.util.HashMap;
import java.util.Map;

import static javax.lang.model.element.ElementKind.ENUM;

public class ElementKindValueProviderMap extends HashMap<ElementKind, ValueProvider<VariableElement>> {
    public ElementKindValueProviderMap(Map<? extends ElementKind, ? extends ValueProvider<VariableElement>> map) {
        super(map);
        this.putIfAbsent(ENUM, new EnumValueProvider());
    }
}
