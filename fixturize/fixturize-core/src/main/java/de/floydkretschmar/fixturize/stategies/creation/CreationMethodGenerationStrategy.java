package de.floydkretschmar.fixturize.stategies.creation;

import de.floydkretschmar.fixturize.domain.FixtureCreationMethodDefinition;
import de.floydkretschmar.fixturize.stategies.constants.ConstantDefinitionMap;

import javax.lang.model.element.TypeElement;
import java.util.Collection;
import java.util.Map;

@FunctionalInterface
public interface CreationMethodGenerationStrategy {
    Collection<FixtureCreationMethodDefinition> generateCreationMethods(TypeElement element, ConstantDefinitionMap constantMap);
}
