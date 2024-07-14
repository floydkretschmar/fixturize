package de.floydkretschmar.fixturize.stategies.creation;

import de.floydkretschmar.fixturize.domain.FixtureConstantDefinition;
import java.util.Collection;

@FunctionalInterface
public interface CreationMethodNamingStrategy {
    String createMethodName(String className, Collection<FixtureConstantDefinition> usedFields);
}
