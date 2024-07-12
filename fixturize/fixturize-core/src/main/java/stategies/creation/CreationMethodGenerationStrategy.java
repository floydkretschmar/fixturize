package stategies.creation;

import java.util.Collection;

@FunctionalInterface
public interface CreationMethodGenerationStrategy {
    <T> Collection<String> generateCreationMethods(Class<T> targetClass);
}
