package stategies.constants;

@FunctionalInterface
public interface ConstantsNamingStrategy {
    String rename(String parameterName);
}
