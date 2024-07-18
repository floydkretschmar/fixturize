package de.floydkretschmar.fixturize.stategies.constants.value;

import de.floydkretschmar.fixturize.stategies.constants.value.map.ClassValueProviderMap;
import de.floydkretschmar.fixturize.stategies.constants.value.map.TypeKindValueProviderMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;

import static de.floydkretschmar.fixturize.TestFixtures.createDeclaredTypeFixture;
import static de.floydkretschmar.fixturize.TestFixtures.createTypeMirrorFixture;
import static javax.lang.model.element.ElementKind.ENUM;
import static javax.lang.model.element.ElementKind.ENUM_CONSTANT;
import static javax.lang.model.type.TypeKind.DECLARED;
import static javax.lang.model.type.TypeKind.INT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConstantValueProviderServiceTest {

    @Mock
    private TypeKindValueProviderMap typeKindMap;

    @Mock
    private ClassValueProviderMap classMap;

    @Mock
    private VariableElement field;

    private ConstantValueProviderService service;

    @BeforeEach
    void setup() {
        service = new ConstantValueProviderService(typeKindMap, classMap);
    }

    // TODO: test all paths including null fallback

    @Test
    void getValueFor_whenCalledForDefinedTypeKind_returnCorrespondingValueString() {
        final var type = createTypeMirrorFixture(INT);
        when(field.asType()).thenReturn(type);
        when(typeKindMap.containsKey(any(TypeKind.class))). thenReturn(true);
        when(typeKindMap.get(any(TypeKind.class))). thenReturn(f -> "value");

        final var result = service.getValueFor(field);

        assertThat(result).isEqualTo("value");
        verify(typeKindMap, times(1)).containsKey(INT);
        verify(typeKindMap, times(1)).get(INT);
        verifyNoMoreInteractions(typeKindMap);
        verifyNoInteractions(classMap);
    }

    @Test
    void getValueFor_whenCalledForDefinedClass_returnCorrespondingValueString() {
        final var type = createTypeMirrorFixture(DECLARED, "ClassName");

        when(field.asType()).thenReturn(type);

        when(typeKindMap.containsKey(any(TypeKind.class))). thenReturn(false);
        when(classMap.containsKey(any(String.class))). thenReturn(true);
        when(classMap.get(any(String.class))). thenReturn(f -> "value");

        final var result = service.getValueFor(field);

        assertThat(result).isEqualTo("value");
        verify(typeKindMap, times(1)).containsKey(DECLARED);
        verify(classMap, times(1)).containsKey("ClassName");
        verify(classMap, times(1)).get("ClassName");
        verifyNoMoreInteractions(typeKindMap, classMap);
    }

    @Test
    void getValueFor_whenCalledForEnum_returnCorrespondingValueString() {
        final var enumConstant = mock(Element.class);
        final var type = createDeclaredTypeFixture("EnumType", ENUM, enumConstant);
        when(enumConstant.getKind()).thenReturn(ENUM_CONSTANT);
        when(enumConstant.toString()).thenReturn("CONSTANT_VALUE");
        when(field.asType()).thenReturn(type);

        when(typeKindMap.containsKey(any(TypeKind.class))). thenReturn(false);
        when(classMap.containsKey(anyString())).thenReturn(false);

        final var result = service.getValueFor(field);

        assertThat(result).isEqualTo("EnumType.CONSTANT_VALUE");
        verify(typeKindMap, times(1)).containsKey(DECLARED);
        verify(classMap, times(1)).containsKey("EnumType");
    }
}