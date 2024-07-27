package de.floydkretschmar.fixturize.stategies.constants;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * An extension of the {@link Map} that defined all methods for storing and retrieving of {@link Constant}s.
 *
 * @author Floyd Kretschmar
 */
public interface ConstantMap extends Map<String, Constant> {

    /**
     * Returns all {@link Constant}s for the provided set of keys.
     *
     * @param keys - for which the {@link Constant}s will be retrieved
     * @return the collection of {@link Constant}s
     */
    Map<String, Optional<Constant>> getMatchingConstants(Collection<String> keys);
}
