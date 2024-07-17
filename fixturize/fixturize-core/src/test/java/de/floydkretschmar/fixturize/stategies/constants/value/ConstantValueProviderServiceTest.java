package de.floydkretschmar.fixturize.stategies.constants.value;

import de.floydkretschmar.fixturize.stategies.constants.value.map.ClassValueProviderMap;
import de.floydkretschmar.fixturize.stategies.constants.value.map.TypeKindValueProviderMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.List;

import static javax.lang.model.element.ElementKind.ENUM;
import static javax.lang.model.element.ElementKind.ENUM_CONSTANT;
import static javax.lang.model.type.TypeKind.DECLARED;
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

    @Test
    void getValueFor_whenCalledForExistingTypeKind_returnCorrespondingValueString() {
        final var type = mock(TypeMirror.class);
        when(field.asType()).thenReturn(type);
        when(type.getKind()).thenReturn(TypeKind.INT);
        when(typeKindMap.containsKey(any(TypeKind.class))). thenReturn(true);
        when(typeKindMap.get(any(TypeKind.class))). thenReturn(f -> "value");

        final var service = new ConstantValueProviderService(typeKindMap, classMap);

        final var result = service.getValueFor(field);
        assertThat(result).isEqualTo("value");
        verify(typeKindMap, times(1)).containsKey(TypeKind.INT);
        verify(typeKindMap, times(1)).get(TypeKind.INT);
        verifyNoMoreInteractions(typeKindMap);
        verifyNoInteractions(classMap);
    }

    @Test
    void getValueFor_whenCalledForDeclaredElementOfKnownKind_returnCorrespondingValueString() {
        final var type = mock(DeclaredType.class);
        final var typeElement = mock(Element.class);
        when(typeElement.getKind()).thenReturn(ENUM);
        when(type.asElement()).thenReturn(typeElement);
        when(field.asType()).thenReturn(type);
        when(type.getKind()).thenReturn(DECLARED);
        when(type.toString()).thenReturn("declaredType");

        final var enumConstant = mock(Element.class);
        when(enumConstant.getKind()).thenReturn(ENUM_CONSTANT);
        when(enumConstant.toString()).thenReturn("CONSTANT_VALUE");
        when(typeElement.getEnclosedElements()).thenReturn((List) List.of(enumConstant));


        when(typeKindMap.containsKey(any(TypeKind.class))). thenReturn(false);
        when(classMap.containsKey(anyString())).thenReturn(false);

        final var service = new ConstantValueProviderService(typeKindMap, classMap);

        final var result = service.getValueFor(field);
        assertThat(result).isEqualTo("declaredType.CONSTANT_VALUE");
        verify(typeKindMap, times(1)).containsKey(DECLARED);
        verify(classMap, times(1)).containsKey("declaredType");
    }

    @Test
    void getValueFor_whenCalledForDeclaredElementOfUnknownKind_returnNullString() {
        final var type = mock(DeclaredType.class);
        final var typeElement = mock(Element.class);
        when(typeElement.getKind()).thenReturn(ENUM);
        when(type.asElement()).thenReturn(typeElement);
        when(field.asType()).thenReturn(type);
        when(type.getKind()).thenReturn(DECLARED);
        when(type.toString()).thenReturn("declaredType");

        when(typeKindMap.containsKey(any(TypeKind.class))). thenReturn(false);
        when(classMap.containsKey(any(String.class))). thenReturn(false);

        final var service = new ConstantValueProviderService(typeKindMap, classMap);

        final var result = service.getValueFor(field);
        assertThat(result).isEqualTo("null");
        verify(typeKindMap, times(1)).containsKey(DECLARED);
        verify(classMap, times(1)).containsKey("declaredType");
    }

    @Test
    void getValueFor_whenCalledForOtherKnownClass_returnCorrespondingValueString() {
        final var type = mock(TypeMirror.class);
        when(field.asType()).thenReturn(type);
        when(type.getKind()).thenReturn(TypeKind.INT);
        when(type.toString()).thenReturn("declaredType");
        when(typeKindMap.containsKey(any(TypeKind.class))). thenReturn(false);
        when(classMap.containsKey(any(String.class))). thenReturn(true);
        when(classMap.get(any(String.class))). thenReturn(f -> "value");

        final var service = new ConstantValueProviderService(typeKindMap, classMap);

        final var result = service.getValueFor(field);
        assertThat(result).isEqualTo("value");
        verify(typeKindMap, times(1)).containsKey(TypeKind.INT);
        verify(classMap, times(1)).containsKey("declaredType");
        verify(classMap, times(1)).get("declaredType");
        verifyNoMoreInteractions(typeKindMap, classMap);
    }

    @Test
    void getValueFor_whenCalledUnknownType_returnNullString() {
        final var type = mock(TypeMirror.class);
        when(field.asType()).thenReturn(type);
        when(type.getKind()).thenReturn(TypeKind.INT);
        when(type.toString()).thenReturn("declaredType");
        when(typeKindMap.containsKey(any(TypeKind.class))). thenReturn(false);
        when(classMap.containsKey(any(String.class))). thenReturn(false);

        final var service = new ConstantValueProviderService(typeKindMap, classMap);

        final var result = service.getValueFor(field);
        assertThat(result).isEqualTo("null");
        verify(typeKindMap, times(1)).containsKey(TypeKind.INT);
        verify(classMap, times(1)).containsKey("declaredType");
        verifyNoMoreInteractions(typeKindMap, classMap);
    }
}