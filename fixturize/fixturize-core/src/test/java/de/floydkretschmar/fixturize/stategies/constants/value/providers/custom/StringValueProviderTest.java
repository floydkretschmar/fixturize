package de.floydkretschmar.fixturize.stategies.constants.value.providers.custom;

import de.floydkretschmar.fixturize.TestFixtures;
import org.junit.jupiter.api.Test;

import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StringValueProviderTest {

    @Test
    void provideValueAsString_whenCalled_returnsValue() {
        final var provider = new StringValueProvider();

        final var field = mock(VariableElement.class);
        final var name = mock(Name.class);
        when(name.toString()).thenReturn("stringFieldName");
        when(field.getSimpleName()).thenReturn(name);

        assertThat(provider.provideValueAsString(field, TestFixtures.createMetadataFixture("java.lang.String")))
                .isEqualTo("\"STRING_FIELD_NAME_VALUE\"");
    }

}