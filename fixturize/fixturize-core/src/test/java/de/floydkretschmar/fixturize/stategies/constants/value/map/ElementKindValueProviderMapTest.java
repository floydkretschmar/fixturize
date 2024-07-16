package de.floydkretschmar.fixturize.stategies.constants.value.map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ElementKindValueProviderMapTest {

    @ParameterizedTest
    @CsvSource({"ENUM"})
    void containsKey_whenCalledForDefaultValue_shouldReturnTrue(ElementKind elementKind) {
        final var map = new ElementKindValueProviderMap(Map.of());

        assertThat(map.containsKey(elementKind)).isTrue();
    }


    @Test
    void containsKey_whenCalledForUnregisteredValue_shouldReturnFalse() {
        final var map = new ElementKindValueProviderMap(Map.of());

        assertThat(map.containsKey(ElementKind.OTHER)).isFalse();
    }

    @Test
    void get_whenCalledForEnum_shouldReturnFormattedStringValue() {
        final var field = mock(VariableElement.class);
        final var fieldType = mock(DeclaredType.class);
        final var declaredElement = mock(Element.class);
        final var enumConstant = mock(Element.class);
        final var type = mock(TypeMirror.class);
        when(field.asType()).thenReturn(fieldType);
        when(fieldType.asElement()).thenReturn(declaredElement);
        when(enumConstant.getKind()).thenReturn(ElementKind.ENUM_CONSTANT);
        when(type.toString()).thenReturn("EnumType");
        when(declaredElement.asType()).thenReturn(type);
        when(enumConstant.toString()).thenReturn("ENUM_CONSTANT");

        when(declaredElement.getEnclosedElements()).thenReturn((List)List.of(enumConstant));
        final var map = new ElementKindValueProviderMap(Map.of());

        assertThat(map.get(ElementKind.ENUM).provideValueAsString(field)).isEqualTo("EnumType.ENUM_CONSTANT");
    }

    @Test
    void get_whenCalledForEnumWithoutConstants_shouldReturnNullString() {
        final var field = mock(VariableElement.class);
        final var fieldType = mock(DeclaredType.class);
        final var declaredElement = mock(Element.class);
        when(field.asType()).thenReturn(fieldType);
        when(fieldType.asElement()).thenReturn(declaredElement);
        when(declaredElement.getEnclosedElements()).thenReturn(List.of());
        final var map = new ElementKindValueProviderMap(Map.of());

        assertThat(map.get(ElementKind.ENUM).provideValueAsString(field)).isEqualTo("null");
    }
}