package de.floydkretschmar.fixturize.stategies.constants.value;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DeclaredKindValueProviderMap extends HashMap<String, ValueProvider> {
    public DeclaredKindValueProviderMap(Map<? extends String, ? extends ValueProvider> map) {
        super(map);
        this.putIfAbsent(String.class.getName(), new StringValueProvider());
        this.putIfAbsent(UUID.class.getName(), new UUIDValueProvider());
    }
}
