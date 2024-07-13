package de.floydkretschmar.fixturize.stategies.constants;

import javax.lang.model.element.Element;
import java.util.Collection;

public interface ConstantsGenerationStrategy {
    Collection<String> generateConstants(Element element);
}
