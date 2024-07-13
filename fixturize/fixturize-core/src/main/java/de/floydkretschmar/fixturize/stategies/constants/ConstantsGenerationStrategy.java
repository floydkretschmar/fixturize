package de.floydkretschmar.fixturize.stategies.constants;

import de.floydkretschmar.fixturize.domain.FixtureConstant;

import javax.lang.model.element.Element;
import java.util.Collection;

public interface ConstantsGenerationStrategy {
    Collection<FixtureConstant> generateConstants(Element element);
}
