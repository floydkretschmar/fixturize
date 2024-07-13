package de.floydkretschmar.fixturize.stategies.creation;

import javax.lang.model.element.Element;
import java.util.Collection;

@FunctionalInterface
public interface CreationMethodGenerationStrategy {
    <T> Collection<String> generateCreationMethods(Element element);
}
