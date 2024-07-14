package de.floydkretschmar.fixturize.stategies.constants.value.map;

import de.floydkretschmar.fixturize.stategies.constants.value.provider.StringValueProvider;
import de.floydkretschmar.fixturize.stategies.constants.value.provider.UUIDValueProvider;
import de.floydkretschmar.fixturize.stategies.constants.value.provider.ValueProvider;

import javax.lang.model.element.VariableElement;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClassValueProviderMap extends HashMap<String, ValueProvider<VariableElement>> {
    public ClassValueProviderMap(Map<? extends String, ? extends ValueProvider<VariableElement>> map) {
        super(map);
        this.putIfAbsent(String.class.getName(), new StringValueProvider());
        this.putIfAbsent(UUID.class.getName(), new UUIDValueProvider());
    }
}
