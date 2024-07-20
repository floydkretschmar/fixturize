package de.floydkretschmar.fixturize.stategies.constants.value.providers.fallback;

import de.floydkretschmar.fixturize.domain.Names;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.ValueProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.lang.model.element.VariableElement;

import static de.floydkretschmar.fixturize.TestFixtures.createDeclaredTypeFixture;
import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.ElementKind.ENUM;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeclaredTypeValueProviderTest {

    @Mock
    private VariableElement field;

    @Mock
    private ValueProvider enumValueProvider;

    @Mock
    private ValueProvider classValueProvider;

    private DeclaredTypeValueProvider valueProvider;

    private Names names;

    @BeforeEach
    void setup() {
        valueProvider = new DeclaredTypeValueProvider(enumValueProvider, classValueProvider);
        names = Names.from("some.test.Class");
    }

    @Test
    void getValueFor_whenCalledForEnum_returnEnumValueProviderValueString() {
        final var type = createDeclaredTypeFixture(ENUM);
        when(field.asType()).thenReturn(type);

        when(enumValueProvider.provideValueAsString(any(), any())).thenReturn("enumValueProviderValue");

        final var result = valueProvider.provideValueAsString(field, names);

        assertThat(result).isEqualTo("enumValueProviderValue");
        verify(enumValueProvider, times(1)).provideValueAsString(eq(field), any(Names.class));
        verifyNoInteractions(classValueProvider);
    }

    @Test
    void getValueFor_whenCalledForAnyOtherDeclaredClass_returnClassValueProviderValueString() {
        final var type = createDeclaredTypeFixture(CLASS);
        when(field.asType()).thenReturn(type);

        when(classValueProvider.provideValueAsString(any(), any())).thenReturn("FallbackValue");

        final var result = valueProvider.provideValueAsString(field, names);

        assertThat(result).isEqualTo("FallbackValue");
        verify(classValueProvider, times(1)).provideValueAsString(eq(field), any(Names.class));
        verifyNoMoreInteractions(classValueProvider);
        verifyNoInteractions(enumValueProvider);
    }
}