package de.floydkretschmar.fixturize.stategies.constants;

import de.floydkretschmar.fixturize.domain.FixtureConstantDefinition;
import de.floydkretschmar.fixturize.exceptions.FixtureCreationException;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FixtureConstantDefinitionMap extends HashMap<String, FixtureConstantDefinition> implements ConstantDefinitionMap {
    public FixtureConstantDefinitionMap(Map<? extends String, ? extends FixtureConstantDefinition> m) {
        super(m);
    }

    @Override
    public List<FixtureConstantDefinition> getMatchingConstants(Collection<String> keys) {
        return keys.stream().map(parameterName -> {
            if (this.containsKey(parameterName))
                return this.get(parameterName);

            throw new FixtureCreationException("The parameter %s specified in @FixtureConstructor has no corresponding field definition."
                    .formatted(parameterName));
        }).toList();
    }
}
