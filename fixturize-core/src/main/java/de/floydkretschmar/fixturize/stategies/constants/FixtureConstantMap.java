package de.floydkretschmar.fixturize.stategies.constants;

import de.floydkretschmar.fixturize.domain.Constant;
import de.floydkretschmar.fixturize.exceptions.FixtureCreationException;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An extension of the {@link LinkedHashMap} that contains all methods for storing and retrieving of {@link Constant}s
 * where the order of elements has to be preserved.
 *
 * @author Floyd Kretschmar
 */
public class FixtureConstantMap extends LinkedHashMap<String, Constant> implements ConstantMap {

    /**
     * Constructs a {@link FixtureConstantMap } using a specified map as a base.
     *
     * @param fixtureConstantMap - the map of custom {@link Constant}s
     */
    public FixtureConstantMap(Map<? extends String, ? extends Constant> fixtureConstantMap) {
        super(fixtureConstantMap);
    }

    /**
     * Returns all {@link Constant}s for the provided set of keys.
     *
     * @param keys - for which the {@link Constant}s will be retrieved
     * @return the collection of {@link Constant}s
     * @throws FixtureCreationException if a specified key does not have a corresponding value
     */
    @Override
    public Collection<Constant> getMatchingConstants(Collection<String> keys) {
        return keys.stream().map(parameterName -> {
            if (this.containsKey(parameterName))
                return this.get(parameterName);

            throw new FixtureCreationException("The parameter %s specified in @FixtureConstructor has no corresponding field definition."
                    .formatted(parameterName));
        }).toList();
    }
}
