package de.floydkretschmar.fixturize.stategies.constants.value.providers;

import de.floydkretschmar.fixturize.CustomValueProviderParser;
import de.floydkretschmar.fixturize.TestFixtures;
import org.junit.jupiter.api.Test;

import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomValueProviderParserTest {
    @Test
    void parseValueProvider_whenCalledWithValidJS_createValueProvider() {
        final var element = mock(VariableElement.class);
        final var names = TestFixtures.createMetadataFixture();

        final var valueProvider = CustomValueProviderParser.parseValueProvider("function(field, names) `test`");
        final var result = valueProvider.provideValueAsString(element, names);

        assertThat(result).isEqualTo("test");
    }

    @Test
    void parseValueProvider_whenUsingField_createValueProvider() {
        final var element = mock(VariableElement.class);
        final var name = mock(Name.class);
        when(element.getSimpleName()).thenReturn(name);
        when(name.toString()).thenReturn("simpleName");
        final var names = TestFixtures.createMetadataFixture();

        final var valueProvider = CustomValueProviderParser.parseValueProvider("function(field, names) `${field.getSimpleName().toString()}`");
        final var result = valueProvider.provideValueAsString(element, names);

        assertThat(result).isEqualTo("simpleName");
    }

    @Test
    void parseValueProvider_whenUsingNames_createValueProvider() {
        final var element = mock(VariableElement.class);
        final var names = TestFixtures.createMetadataFixture();

        final var valueProvider = CustomValueProviderParser.parseValueProvider("function(field, names) `${names.getQualifiedClassName()}`");
        final var result = valueProvider.provideValueAsString(element, names);

        assertThat(result).isEqualTo("some.test.Class");
    }

    @Test
    void parseValueProvider_whenCreatingMultiLineExecution_shouldThrowError() {
        final var element = mock(VariableElement.class);
        final var name = mock(Name.class);
        when(element.getSimpleName()).thenReturn(name);
        when(name.toString()).thenReturn("simpleName");
        final var names = TestFixtures.createMetadataFixture();

        final var valueProvider = CustomValueProviderParser.parseValueProvider("""
                function(field, names) {
                    var simpleName = field.getSimpleName();
                    return `${simpleName.toString()}`;
                }""");
        final var result = valueProvider.provideValueAsString(element, names);

        assertThat(result).isEqualTo("simpleName");
    }
}