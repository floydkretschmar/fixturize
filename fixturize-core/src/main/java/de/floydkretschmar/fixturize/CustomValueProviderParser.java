package de.floydkretschmar.fixturize;

import de.floydkretschmar.fixturize.exceptions.FixtureCreationException;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.ValueProvider;
import lombok.RequiredArgsConstructor;
import org.graalvm.polyglot.Context;

import java.io.Closeable;

/**
 * Creates value providers from string definitions using the nashorn script engine.
 *
 * @author Floyd Kretschmar
 */
@RequiredArgsConstructor
public class CustomValueProviderParser implements Closeable {

    private final Context context;
    /**
     * Returns a valid {@link ValueProvider} for a provided string representation defining a custom {@link ValueProvider}.
     * The string representation has to be a single line of code that in the end returns a string, which will in turn be used
     * to create a constant during fixture generation.
     * @param valueProviderDefinition - that defines the value provider function as a string
     * @return the parsed {@link ValueProvider}
     * @throws FixtureCreationException if the provided definition is not a valid line of code
     */
    public ValueProvider parseValueProvider(String valueProviderDefinition) {
        var jsFunctionValue = this.context.eval("js", "(%s)".formatted(valueProviderDefinition));
        return jsFunctionValue.as(ValueProvider.class);
    }

    @Override
    public void close() {
        this.context.close();
    }
}
