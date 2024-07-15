package de.floydkretschmar.fixturize.stategies.constants;

import de.floydkretschmar.fixturize.domain.FixtureConstantDefinition;

import java.util.Collection;
import java.util.Map;

public interface ConstantDefinitionMap extends Map<String, FixtureConstantDefinition> {
    Collection<FixtureConstantDefinition> getMatchingConstants(Collection<String> keys);
}
