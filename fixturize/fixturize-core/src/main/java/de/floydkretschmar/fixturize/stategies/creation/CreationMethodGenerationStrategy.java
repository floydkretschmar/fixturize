package de.floydkretschmar.fixturize.stategies.creation;

import de.floydkretschmar.fixturize.domain.FixtureConstant;
import de.floydkretschmar.fixturize.domain.FixtureCreationMethod;

import javax.lang.model.element.TypeElement;
import java.util.Collection;
import java.util.Map;

@FunctionalInterface
public interface CreationMethodGenerationStrategy {
    Collection<FixtureCreationMethod> generateCreationMethods(TypeElement element, Map<String, FixtureConstant> constantMap);
}
