package de.floydkretschmar.fixturize.stategies.creation;

import de.floydkretschmar.fixturize.domain.Constant;

import java.util.Collection;

/**
 * Defined function that creates a method name for a creation method based on the constants used when generating the
 * creation method.
 *
 * @author Floyd Kretschmar
 */
@FunctionalInterface
public interface CreationMethodNamingStrategy {
    /**
     * Returns the name used when generating a creation method based on the class name and constants used.
     *
     * @param className     - of the class for whose fixture the creation method is being named
     * @param usedConstants - in the creation method
     * @return the creation method name
     */
    String createMethodName(String className, Collection<Constant> usedConstants);
}
