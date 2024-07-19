package de.floydkretschmar.fixturize.stategies.constants.value;

import de.floydkretschmar.fixturize.domain.Names;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.RecursiveValueProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;

import static de.floydkretschmar.fixturize.TestFixtures.createDeclaredTypeFixture;
import static de.floydkretschmar.fixturize.TestFixtures.createTypeMirrorFixture;
import static de.floydkretschmar.fixturize.stategies.constants.value.ConstantValueProviderService.DEFAULT_VALUE;
import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.ElementKind.ENUM;
import static javax.lang.model.element.ElementKind.ENUM_CONSTANT;
import static javax.lang.model.type.TypeKind.ARRAY;
import static javax.lang.model.type.TypeKind.DECLARED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConstantValueProviderServiceTest {

    @Mock
    private ValueProviderMap valueProviderMap;

    @Mock
    private VariableElement field;

    @Mock
    private RecursiveValueProvider fallbackValueProvider;

    private ConstantValueProviderService service;

    @BeforeEach
    void setup() {
        service = new ConstantValueProviderService(valueProviderMap, fallbackValueProvider);
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
    }

    @Test
    void getValueFor_whenCalledForEnum_returnCorrespondingValueString() {
        final var enumConstant = mock(Element.class);
        final var type = createDeclaredTypeFixture("EnumType", ENUM, enumConstant);
        when(enumConstant.getKind()).thenReturn(ENUM_CONSTANT);
        when(enumConstant.toString()).thenReturn("CONSTANT_VALUE");
        when(field.asType()).thenReturn(type);

        when(valueProviderMap.containsKey(anyString())).thenReturn(false);

        final var result = service.getValueFor(field);

        assertThat(result).isEqualTo("EnumType.CONSTANT_VALUE");
        verify(valueProviderMap, times(1)).containsKey("EnumType");
        verifyNoMoreInteractions(valueProviderMap);
    }

    @Test
    void getValueFor_whenCalledForEnumWithoutConstants_returnDefaultValue() {
        final var type = createDeclaredTypeFixture("EnumType", ENUM);
        when(field.asType()).thenReturn(type);

        when(valueProviderMap.containsKey(anyString())).thenReturn(false);

        final var result = service.getValueFor(field);

        assertThat(result).isEqualTo(DEFAULT_VALUE);
        verify(valueProviderMap, times(1)).containsKey("EnumType");
        verifyNoMoreInteractions(valueProviderMap);
    }

    @Test
    void getValueFor_whenCalledForArray_returnCorrespondingValueString() {
        final var type = createTypeMirrorFixture(ARRAY, "ArrayType[]");
        when(field.asType()).thenReturn(type);

        when(valueProviderMap.containsKey(anyString())).thenReturn(false);

        final var result = service.getValueFor(field);

        assertThat(result).isEqualTo("new ArrayType[] {}");
        verify(valueProviderMap, times(1)).containsKey("ArrayType[]");
        verifyNoMoreInteractions(valueProviderMap);
    }

    @Test
    void getValueFor_whenCalledForAnyOtherDeclaredClass_returnFallbackValue() {
        final var type = createDeclaredTypeFixture("OtherClass", CLASS, null);
        when(field.asType()).thenReturn(type);

        when(fallbackValueProvider.recursivelyProvideValue(any(), any(), any())).thenReturn("FallbackValue");
        when(valueProviderMap.containsKey(anyString())).thenReturn(false);

        final var result = service.getValueFor(field);

        assertThat(result).isEqualTo("FallbackValue");
        verify(valueProviderMap, times(1)).containsKey("OtherClass");
        verify(fallbackValueProvider, times(1)).recursivelyProvideValue(eq(field), any(Names.class), eq(service));
        verifyNoMoreInteractions(valueProviderMap);
    }
}