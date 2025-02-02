package de.floydkretschmar.fixturize.stategies.constants.naming;

/**
 * Defines the strategy to be used to name constants base on the name of the corresponding field.
 *
 * @author Floyd Kretschmar
 */
@FunctionalInterface
public interface NamingStrategy {
    /**
     * Returns a constant name based on the provided field name.
     * @param fieldName - that is used to create the constant name
     * @return the constant name
     */
    String createName(String fieldName);
}
