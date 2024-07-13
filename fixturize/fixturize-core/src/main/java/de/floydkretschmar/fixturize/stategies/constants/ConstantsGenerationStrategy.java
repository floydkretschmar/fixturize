package de.floydkretschmar.fixturize.stategies.constants;

import de.floydkretschmar.fixturize.domain.FixtureConstant;

import javax.lang.model.element.TypeElement;
import java.util.Map;

public interface ConstantsGenerationStrategy {
    Map<String, FixtureConstant> generateConstants(TypeElement element);
}
