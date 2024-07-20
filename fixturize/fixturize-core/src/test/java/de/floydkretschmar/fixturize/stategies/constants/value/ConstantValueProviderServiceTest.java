package de.floydkretschmar.fixturize.stategies.constants.value;

import de.floydkretschmar.fixturize.domain.Names;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.ValueProvider;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.ValueProviderFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.lang.model.element.VariableElement;
import java.util.Map;

import static de.floydkretschmar.fixturize.TestFixtures.createDeclaredTypeFixture;
import static de.floydkretschmar.fixturize.TestFixtures.createTypeMirrorFixture;
import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.ElementKind.ENUM;
import static javax.lang.model.type.TypeKind.ARRAY;
import static javax.lang.model.type.TypeKind.DECLARED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConstantValueProviderServiceTest {

    @Mock
    private ValueProviderMap valueProviderMap;

    @Mock
    private VariableElement field;

    @Mock
    private ValueProvider classValueProvider;

    @Mock
    private ValueProvider enumValueProvider;

    @Mock
    private ValueProvider containerValueProvider;

    private ConstantValueProviderService service;

    @BeforeEach
    void setup() {
        final var valueProviderFactory = mock(ValueProviderFactory.class);
        when(valueProviderFactory.createValueProviders(anyMap())).thenReturn(valueProviderMap);
        when(valueProviderFactory.createClassValueProvider(any())).thenReturn(classValueProvider);
        when(valueProviderFactory.createEnumValueProvider()).thenReturn(enumValueProvider);
        when(valueProviderFactory.createContainerValueProvider()).thenReturn(containerValueProvider);
        service = new ConstantValueProviderService(Map.of(), valueProviderFactory);
    }

    @Test
    void getValueFor_whenCalledForDefinedType_returnCorrespondingValueString() {
        final var type = createTypeMirrorFixture(DECLARED, "ClassName");

        when(field.asType()).thenReturn(type);

        when(valueProviderMap.containsKey(any(String.class))).thenReturn(true);
        when(valueProviderMap.get(any(String.class))).thenReturn((f, n) -> "value");

        final var result = service.getValueFor(field);

        assertThat(result).isEqualTo("value");
        verify(valueProviderMap, times(1)).containsKey("ClassName");
        verify(valueProviderMap, times(1)).get("ClassName");
        verifyNoMoreInteractions(valueProviderMap);
        verifyNoInteractions(classValueProvider, enumValueProvider, containerValueProvider);
    }

    @Test
    void getValueFor_whenCalledForEnum_returnEnumValueProviderValueString() {
        final var type = createDeclaredTypeFixture("EnumType", ENUM);
        when(field.asType()).thenReturn(type);

        when(valueProviderMap.containsKey(anyString())).thenReturn(false);
        when(enumValueProvider.provideValueAsString(any(), any())).thenReturn("enumValueProviderValue");

        final var result = service.getValueFor(field);

        assertThat(result).isEqualTo("enumValueProviderValue");
        verify(valueProviderMap, times(1)).containsKey("EnumType");
        verify(enumValueProvider, times(1)).provideValueAsString(eq(field), any(Names.class));
        verifyNoMoreInteractions(valueProviderMap, enumValueProvider);
        verifyNoInteractions(classValueProvider, containerValueProvider);
    }

    @Test
    void getValueFor_whenCalledForArray_returnCorrespondingValueString() {
        final var type = createTypeMirrorFixture(ARRAY, "ArrayType[]");
        when(field.asType()).thenReturn(type);

        when(valueProviderMap.containsKey(anyString())).thenReturn(false);
        when(containerValueProvider.provideValueAsString(any(), any())).thenReturn("containerValueProviderValue");

        final var result = service.getValueFor(field);

        assertThat(result).isEqualTo("containerValueProviderValue");
        verify(valueProviderMap, times(1)).containsKey("ArrayType[]");
        verify(containerValueProvider, times(1)).provideValueAsString(eq(field), any(Names.class));
        verifyNoMoreInteractions(valueProviderMap, containerValueProvider);
        verifyNoInteractions(classValueProvider, enumValueProvider);
    }

    @Test
    void getValueFor_whenCalledForAnyOtherDeclaredClass_returnClassValueProviderValueString() {
        final var type = createDeclaredTypeFixture("OtherClass", CLASS);
        when(field.asType()).thenReturn(type);

        when(valueProviderMap.containsKey(anyString())).thenReturn(false);
        when(classValueProvider.provideValueAsString(any(), any())).thenReturn("FallbackValue");

        final var result = service.getValueFor(field);

        assertThat(result).isEqualTo("FallbackValue");
        verify(valueProviderMap, times(1)).containsKey("OtherClass");
        verify(classValueProvider, times(1)).provideValueAsString(eq(field), any(Names.class));
        verifyNoMoreInteractions(valueProviderMap, classValueProvider);
        verifyNoInteractions(enumValueProvider, containerValueProvider);
    }
}