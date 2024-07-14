package de.floydkretschmar.fixturize.stategies.constants.value.map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import java.util.Map;

import static javax.lang.model.type.TypeKind.INT;
import static javax.lang.model.type.TypeKind.OTHER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class TypeKindValueProviderMapTest {

    @ParameterizedTest
    @CsvSource({
        "BOOLEAN",
        "CHAR",
        "BYTE",
        "INT",
        "SHORT",
        "LONG",
        "FLOAT",
        "DOUBLE",
    })
    void containsKey_whenCalledForDefaultValue_shouldReturnTrue(TypeKind typeKind) {
        final var map = new TypeKindValueProviderMap(Map.of());

        assertThat(map.containsKey(typeKind)).isTrue();
    }

    @Test
    void containsKey_whenCalledForUnregisteredValue_shouldReturnFalse() {
        final var map = new TypeKindValueProviderMap(Map.of());

        assertThat(map.containsKey(OTHER)).isFalse();
    }

    @ParameterizedTest
    @CsvSource({
            "BOOLEAN, false",
            "CHAR, '\u0000'",
            "BYTE, 0",
            "INT, 0",
            "SHORT, Short.valueOf((short)0)",
            "LONG, 0L",
            "FLOAT, 0.0F",
            "DOUBLE, 0.0" })
    void get_whenCalledForDefaultValue_shouldReturnExpectedDefaultValue(TypeKind targetClassName, String expectedDefaultValue) {
        final var field = mock(VariableElement.class);
        final var map = new TypeKindValueProviderMap(Map.of());

        assertThat(map.get(targetClassName).provideValueAsString(field)).isEqualTo(expectedDefaultValue);
    }

    @Test
    void get_whenDefaultIsOverwritten_shouldReturnCustomValue() {
        final var field = mock(VariableElement.class);
        final var map = new TypeKindValueProviderMap(Map.of(INT, f -> "10"));

        assertThat(map.get(INT).provideValueAsString(field)).isEqualTo("10");
    }
}