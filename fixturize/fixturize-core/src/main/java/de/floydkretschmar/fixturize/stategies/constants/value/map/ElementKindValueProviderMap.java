package de.floydkretschmar.fixturize.stategies.constants.value.map;

import de.floydkretschmar.fixturize.stategies.constants.value.provider.EnumValueProvider;
import de.floydkretschmar.fixturize.stategies.constants.value.provider.ValueProvider;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import java.util.HashMap;
import java.util.Map;

import static javax.lang.model.element.ElementKind.ENUM;

public class ElementKindValueProviderMap extends HashMap<ElementKind, ValueProvider<Element>> {
    public ElementKindValueProviderMap(Map<? extends ElementKind, ? extends ValueProvider<Element>> map) {
        super(map);
        this.putIfAbsent(ENUM, new EnumValueProvider());
    }
}
