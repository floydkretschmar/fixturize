package de.floydkretschmar.fixturize;

import de.floydkretschmar.fixturize.exceptions.FixtureCreationException;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.ValueProvider;

import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * Creates value providers from string definitions using the nashorn script engine.
 *
 * @author Floyd Kretschmar
 */
public class CustomValueProviderParser {
    /**
     * Returns a valid {@link ValueProvider} for a provided string representation defining a custom {@link ValueProvider}.
     * The string representation has to be a single line of code that in the end returns a string, which will in turn be used
     * to create a constant during fixture generation.
     * @param valueProviderDefinition - that defines the value provider function as a string
     * @return the parsed {@link ValueProvider}
     * @throws FixtureCreationException if the provided definition is not a valid line of code
     */
    public static ValueProvider parseValueProvider(String valueProviderDefinition) {
        final var engineManager = new ScriptEngineManager();
        final var engine = engineManager.getEngineByName("nashorn");

        try {
            final var valueProviderAsJs = "function(field, names) %s".formatted(valueProviderDefinition);
            final var evaluationResult = engine.eval("""
                    var ValueProvider = Java.type('de.floydkretschmar.fixturize.stategies.constants.value.providers.ValueProvider');
                    new ValueProvider(%s);""".formatted(valueProviderAsJs));

            return (ValueProvider) evaluationResult;
        } catch (ScriptException e) {
            throw new FixtureCreationException("The provided custom value provider %s could not be parsed as a valid value provider".formatted(valueProviderDefinition));
        }
    }
}
