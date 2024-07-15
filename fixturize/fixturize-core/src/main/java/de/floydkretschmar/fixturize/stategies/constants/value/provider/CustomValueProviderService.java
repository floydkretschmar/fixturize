package de.floydkretschmar.fixturize.stategies.constants.value.provider;

import de.floydkretschmar.fixturize.exceptions.FixtureCreationException;

import javax.lang.model.element.Element;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.function.Function;

public class CustomValueProviderService {

    public <T extends Element> ValueProvider<T> createClassValueProvider(String valueProviderDefinition) {
        final var engineManager = new ScriptEngineManager();
        final var engine = engineManager.getEngineByName("nashorn");

        try {
            final var valueProviderAsJs = "function(field) %s".formatted(valueProviderDefinition);
            final var evaluationResult = engine.eval("new java.util.function.Function(%s)".formatted(valueProviderAsJs));
            final var function = (Function<Object, Object>) evaluationResult;

            return field -> function.apply(field).toString();
        } catch (ScriptException e) {
            throw new FixtureCreationException("The provided custom value provider %s could not be parsed as a valid value provider".formatted(valueProviderDefinition));
        }
    }
}
