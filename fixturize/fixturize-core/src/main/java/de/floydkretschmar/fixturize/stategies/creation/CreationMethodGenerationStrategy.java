package de.floydkretschmar.fixturize.stategies.creation;

import de.floydkretschmar.fixturize.domain.FixtureConstantDefinition;
import de.floydkretschmar.fixturize.domain.FixtureCreationMethodDefinition;

import javax.lang.model.element.TypeElement;
import java.util.Collection;
import java.util.Map;

@FunctionalInterface
public interface CreationMethodGenerationStrategy {
    Collection<FixtureCreationMethodDefinition> generateCreationMethods(TypeElement element, Map<String, FixtureConstantDefinition> constantMap);
}
