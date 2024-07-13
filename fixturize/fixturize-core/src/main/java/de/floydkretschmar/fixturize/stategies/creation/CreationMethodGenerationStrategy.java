package de.floydkretschmar.fixturize.stategies.creation;

import javax.lang.model.element.Element;
import java.util.Collection;

@FunctionalInterface
public interface CreationMethodGenerationStrategy {
    Collection<String> generateCreationMethods(Element element);
}
