package de.floydkretschmar.fixturize.stategies.constants.value.map;

import de.floydkretschmar.fixturize.stategies.constants.value.provider.BooleanValueProvider;
import de.floydkretschmar.fixturize.stategies.constants.value.provider.ByteValueProvider;
import de.floydkretschmar.fixturize.stategies.constants.value.provider.CharacterValueProvider;
import de.floydkretschmar.fixturize.stategies.constants.value.provider.DoubleValueProvider;
import de.floydkretschmar.fixturize.stategies.constants.value.provider.FloatValueProvider;
import de.floydkretschmar.fixturize.stategies.constants.value.provider.IntegerValueProvider;
import de.floydkretschmar.fixturize.stategies.constants.value.provider.LongValueProvider;
import de.floydkretschmar.fixturize.stategies.constants.value.provider.ShortValueProvider;
import de.floydkretschmar.fixturize.stategies.constants.value.provider.StringValueProvider;
import de.floydkretschmar.fixturize.stategies.constants.value.provider.UUIDValueProvider;
import de.floydkretschmar.fixturize.stategies.constants.value.provider.ValueProvider;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

/***
 * An extension of the {@link HashMap} class that registers a default {@link ValueProvider}s for a number of different
 * classes, given no custom provider has been defined for the specified class.
 *
 * @author Floyd Kretschmar
 */
public class ClassValueProviderMap extends HashMap<String, ValueProvider> {

    /***
     * Constructs a {@link ElementKindValueProviderMap } registering default {@link ValueProvider}s for a number of different
     * classes, given no custom {@link ValueProvider} has been provided for the specified class.
     *
     * @param customClassValueProviders - the list of custom {@link ValueProvider}s
     */
    public ClassValueProviderMap(Map<? extends String, ? extends ValueProvider> customClassValueProviders) {
        super(customClassValueProviders);
        this.putIfAbsent(String.class.getName(), new StringValueProvider());
        this.putIfAbsent(UUID.class.getName(), new UUIDValueProvider());
        this.putIfAbsent(Boolean.class.getName(),  new BooleanValueProvider());
        this.putIfAbsent(Byte.class.getName(), new ByteValueProvider());
        this.putIfAbsent(Character.class.getName(), new CharacterValueProvider());
        this.putIfAbsent(Double.class.getName(), new DoubleValueProvider());
        this.putIfAbsent(Float.class.getName(), new FloatValueProvider());
        this.putIfAbsent(Integer.class.getName(), new IntegerValueProvider());
        this.putIfAbsent(Long.class.getName(), new LongValueProvider());
        this.putIfAbsent(Short.class.getName(), new ShortValueProvider());
        this.putIfAbsent(BigDecimal.class.getName(), field -> "BigDecimal.ZERO");
        this.putIfAbsent(BigInteger.class.getName(), field -> "BigInteger.ZERO");
        this.putIfAbsent(Instant.class.getName(), field -> "java.time.Instant.now()");
        this.putIfAbsent(Duration.class.getName(), field -> "java.time.Duration.ZERO");
        this.putIfAbsent(LocalDate.class.getName(), field -> "java.time.LocalDate.now()");
        this.putIfAbsent(LocalDateTime.class.getName(), field -> "java.time.LocalDateTime.now()");
        this.putIfAbsent(LocalTime.class.getName(), field -> "java.time.LocalTime.now()");
        this.putIfAbsent(Date.class.getName(), field -> "new java.util.Date()");
        this.putIfAbsent(Collection.class.getName(), field -> "java.util.List.of()");
        this.putIfAbsent(List.class.getName(), field -> "java.util.List.of()");
        this.putIfAbsent(Map.class.getName(), field -> "java.util.Map.of()");
        this.putIfAbsent(Set.class.getName(), field -> "java.util.Set.of()");
        this.putIfAbsent(Queue.class.getName(), field -> "new java.util.PriorityQueue<>()");
    }
}
