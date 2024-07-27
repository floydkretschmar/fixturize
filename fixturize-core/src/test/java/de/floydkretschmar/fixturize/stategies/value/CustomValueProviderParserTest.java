package de.floydkretschmar.fixturize.stategies.value;

import de.floydkretschmar.fixturize.TestFixtures;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomValueProviderParserTest {

    private CustomValueProviderParser parser;

    @BeforeEach
    void setup() {
        Context context = Context.newBuilder("js")
                .allowHostAccess(HostAccess.ALL)
                .build();
        parser = new CustomValueProviderParser(context);
    }

    @AfterEach
    void teardown() {
        parser.close();
    }

    @Test
    void parseValueProvider_whenCalledWithValidJS_createValueProvider() {
        final var element = mock(VariableElement.class);
        final var names = TestFixtures.createMetadataFixture();

        final var valueProvider = parser.parseValueProvider("function(field, names) { return `test`; }");
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

        final var valueProvider = parser.parseValueProvider("(field, names) => `${field.getSimpleName().toString()}`");
        final var result = valueProvider.provideValueAsString(element, names);

        assertThat(result).isEqualTo("simpleName");
    }

    @Test
    void parseValueProvider_whenUsingNames_createValueProvider() {
        final var element = mock(VariableElement.class);
        final var names = TestFixtures.createMetadataFixture();

        final var valueProvider = parser.parseValueProvider("(field, names) => `${names.getQualifiedClassName()}`");
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

        final var valueProvider = parser.parseValueProvider("""
                function(field, names) {
                    var simpleName = field.getSimpleName();
                    return `${simpleName.toString()}`;
                }""");
        final var result = valueProvider.provideValueAsString(element, names);

        assertThat(result).isEqualTo("simpleName");
    }
}