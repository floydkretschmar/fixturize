package de.floydkretschmar.fixturize.stategies.creation;

import de.floydkretschmar.fixturize.domain.FixtureCreationMethod;

import javax.lang.model.element.Element;
import java.util.Collection;

@FunctionalInterface
public interface CreationMethodGenerationStrategy {
    Collection<FixtureCreationMethod> generateCreationMethods(Element element);
}
