package de.floydkretschmar.fixturize.stategies.constants;

@FunctionalInterface
public interface ConstantsNamingStrategy {
    String rename(String parameterName);
}
