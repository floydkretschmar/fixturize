package de.floydkretschmar.fixturize.stategies.constants.value.provider;

import de.floydkretschmar.fixturize.CustomValueProviderParser;
import de.floydkretschmar.fixturize.exceptions.FixtureCreationException;
import org.junit.jupiter.api.Test;

import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomValueProviderParserTest {
    @Test
    void parseValueProvider_whenCalledWithValidJS_createValueProvider() {
        final var element = mock(VariableElement.class);

        final var valueProvider = CustomValueProviderParser.parseValueProvider("\"test\"");
        final var result = valueProvider.provideValueAsString(element);

        assertThat(result).isEqualTo("test");
    }

    @Test
    void parseValueProvider_whenUsingField_createValueProvider() {
        final var element = mock(VariableElement.class);
        final var name = mock(Name.class);
        when(element.getSimpleName()).thenReturn(name);
        when(name.toString()).thenReturn("simpleName");

        final var valueProvider = CustomValueProviderParser.parseValueProvider("field.getSimpleName().toString()");
        final var result = valueProvider.provideValueAsString(element);

        assertThat(result).isEqualTo("simpleName");
    }

    @Test
    void parseValueProvider_whenCreatingMultiLineExecution_shouldThrowError() {
        final var element = mock(VariableElement.class);
        final var name = mock(Name.class);
        when(element.getSimpleName()).thenReturn(name);
        when(name.toString()).thenReturn("simpleName");

        assertThrows(FixtureCreationException.class, () -> CustomValueProviderParser.parseValueProvider("""
                var simpleName = field.getSimpleName();
                return simpleName.toString();"""));
    }
}