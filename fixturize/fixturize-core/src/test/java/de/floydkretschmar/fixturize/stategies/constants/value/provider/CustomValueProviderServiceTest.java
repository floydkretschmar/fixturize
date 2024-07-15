package de.floydkretschmar.fixturize.stategies.constants.value.provider;

import de.floydkretschmar.fixturize.exceptions.FixtureCreationException;
import org.junit.jupiter.api.Test;

import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomValueProviderServiceTest {
    @Test
    void createClassValueProvider_whenCalledWithValidJS_createValueProvider() {
        final var element = mock(VariableElement.class);
        final var service = new CustomValueProviderService();

        final var valueProvider = service.createClassValueProvider("\"test\"");

        final var result = valueProvider.provideValueAsString(element);

        assertThat(result).isEqualTo("test");
    }

    @Test
    void createClassValueProvider_whenUsingField_createValueProvider() {
        final var element = mock(VariableElement.class);
        final var name = mock(Name.class);
        when(element.getSimpleName()).thenReturn(name);
        when(name.toString()).thenReturn("simpleName");

        final var service = new CustomValueProviderService();

        final var valueProvider = service.createClassValueProvider("field.getSimpleName().toString()");

        final var result = valueProvider.provideValueAsString(element);

        assertThat(result).isEqualTo("simpleName");
    }

    @Test
    void createClassValueProvider_whenCreatingMultiLineExecution_shouldThrowError() {
        final var element = mock(VariableElement.class);
        final var name = mock(Name.class);
        when(element.getSimpleName()).thenReturn(name);
        when(name.toString()).thenReturn("simpleName");

        final var service = new CustomValueProviderService();

        assertThrows(FixtureCreationException.class, () -> service.createClassValueProvider("""
                var simpleName = field.getSimpleName();
                return simpleName.toString();"""));
    }
}