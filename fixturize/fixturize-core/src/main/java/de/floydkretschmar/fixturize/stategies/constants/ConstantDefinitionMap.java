package de.floydkretschmar.fixturize.stategies.constants;

import de.floydkretschmar.fixturize.domain.FixtureConstantDefinition;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ConstantDefinitionMap extends Map<String, FixtureConstantDefinition> {
    List<FixtureConstantDefinition> getMatchingConstants(Collection<String> keys);
}
