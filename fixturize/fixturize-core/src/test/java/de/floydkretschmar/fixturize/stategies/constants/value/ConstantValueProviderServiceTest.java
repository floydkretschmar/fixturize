package de.floydkretschmar.fixturize.stategies.constants.value;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
    private DeclaredKindValueProviderMap declaredKindMap;

    @Mock
    private VariableElement field;

    @Mock
    private TypeMirror type;

    @BeforeEach
    void setup() {
        when(field.asType()).thenReturn(type);
        when(type.getKind()).thenReturn(TypeKind.INT);
    }

    @Test
    void getValueFor_whenCalledForExistingTypeKind_returnCorrespondingValueString() {
        when(typeKindMap.containsKey(any(TypeKind.class))). thenReturn(true);
        when(typeKindMap.get(any(TypeKind.class))). thenReturn(f -> "value");

        final var service = new ConstantValueProviderService(typeKindMap, declaredKindMap);

        final var result = service.getValueFor(field);
        assertThat(result).isEqualTo("value");
        verify(typeKindMap, times(1)).containsKey(TypeKind.INT);
        verify(typeKindMap, times(1)).get(TypeKind.INT);
        verifyNoMoreInteractions(typeKindMap);
        verifyNoInteractions(declaredKindMap);
    }

    @Test
    void getValueFor_whenCalledForDeclaredTypeKind_returnCorrespondingValueString() {
        when(typeKindMap.containsKey(any(TypeKind.class))). thenReturn(false);
        when(declaredKindMap.containsKey(any(String.class))). thenReturn(true);
        when(declaredKindMap.get(any(String.class))). thenReturn(f -> "value");
        when(type.toString()).thenReturn("declaredType");

        final var service = new ConstantValueProviderService(typeKindMap, declaredKindMap);

        final var result = service.getValueFor(field);
        assertThat(result).isEqualTo("value");
        verify(typeKindMap, times(1)).containsKey(TypeKind.INT);
        verify(declaredKindMap, times(1)).containsKey("declaredType");
        verify(declaredKindMap, times(1)).get("declaredType");
        verifyNoMoreInteractions(typeKindMap, declaredKindMap);
    }

    @Test
    void getValueFor_whenCalledUnknownType_returnNullString() {
        when(typeKindMap.containsKey(any(TypeKind.class))). thenReturn(false);
        when(declaredKindMap.containsKey(any(String.class))). thenReturn(false);
        when(type.toString()).thenReturn("declaredType");

        final var service = new ConstantValueProviderService(typeKindMap, declaredKindMap);

        final var result = service.getValueFor(field);
        assertThat(result).isEqualTo("null");
        verify(typeKindMap, times(1)).containsKey(TypeKind.INT);
        verify(declaredKindMap, times(1)).containsKey("declaredType");
        verifyNoMoreInteractions(typeKindMap, declaredKindMap);
    }
}