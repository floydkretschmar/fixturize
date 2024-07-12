package stategies.constants;

import java.util.Collection;

public interface ConstantsGenerationStrategy {
    <T> Collection<String> generateConstants(Class<T> targetClass);
}
