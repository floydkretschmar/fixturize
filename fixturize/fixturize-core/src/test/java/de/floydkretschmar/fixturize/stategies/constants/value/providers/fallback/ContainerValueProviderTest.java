package de.floydkretschmar.fixturize.stategies.constants.value.providers.fallback;

import de.floydkretschmar.fixturize.domain.Names;
import de.floydkretschmar.fixturize.stategies.constants.value.ValueProviderService;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.ValueProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static de.floydkretschmar.fixturize.TestFixtures.createTypeMirrorFixture;
import static javax.lang.model.type.TypeKind.ARRAY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContainerValueProviderTest {
    private ContainerValueProvider valueProvider;

    @Mock
    private VariableElement field;

    @Mock
    private Elements elementUtils;

    @Mock
    private Types typeUtils;

    @Mock
    private ValueProvider arrayValueProvider;

    @Mock
    private ValueProviderService service;

    @BeforeEach
    void setup() {
        valueProvider = new ContainerValueProvider(elementUtils, typeUtils, arrayValueProvider, service);
    }

    @Test
    void getValueFor_whenCalledForArray_returnContainerValueProviderValueString() {
        final var type = createTypeMirrorFixture(ARRAY);
        final var names = Names.from("some.test.ArrayClass[]");
        when(field.asType()).thenReturn(type);
        when(arrayValueProvider.provideValueAsString(any(), any())).thenReturn("arrayValueProviderValue");

        final var result = valueProvider.provideValueAsString(field, names);

        assertThat(result).isEqualTo("arrayValueProviderValue");
        verify(arrayValueProvider, times(1)).provideValueAsString(eq(field), any(Names.class));
        verifyNoMoreInteractions(arrayValueProvider);
    }
}