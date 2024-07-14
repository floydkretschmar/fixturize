package de.floydkretschmar.fixturize.domain;

import de.floydkretschmar.fixturize.stategies.constants.value.ConstantValueProviderMap;
import de.floydkretschmar.fixturize.stategies.constants.value.StringValueProvider;
import de.floydkretschmar.fixturize.stategies.constants.value.UUIDValueProvider;
import de.floydkretschmar.fixturize.stategies.constants.value.ValueProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FixtureConstantValueProviderMap extends HashMap<String, ValueProvider> implements ConstantValueProviderMap {
    public FixtureConstantValueProviderMap(Map<? extends String, ? extends ValueProvider> map) {
        super(map);
        this.putIfAbsent(boolean.class.getName(), field -> "false");
        this.putIfAbsent(char.class.getName(), field -> "\u0000");
        this.putIfAbsent(byte.class.getName(), field -> "0");
        this.putIfAbsent(int.class.getName(), field -> "0");
        this.putIfAbsent(short.class.getName(), field -> "Short.valueOf((short)0)");
        this.putIfAbsent(long.class.getName(), field -> "0L");
        this.putIfAbsent(float.class.getName(), field -> "0.0F");
        this.putIfAbsent(double.class.getName(), field -> "0.0");
        this.putIfAbsent(String.class.getName(), new StringValueProvider());
        this.putIfAbsent(UUID.class.getName(), new UUIDValueProvider());
    }
}
