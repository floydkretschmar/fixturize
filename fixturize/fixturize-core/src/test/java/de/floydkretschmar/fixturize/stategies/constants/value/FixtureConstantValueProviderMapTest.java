package de.floydkretschmar.fixturize.stategies.constants.value;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;

import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;
import java.util.Map;
import java.util.UUID;

import static de.floydkretschmar.fixturize.TestConstants.RANDOM_UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FixtureConstantValueProviderMapTest {

    @ParameterizedTest
    @CsvSource({
            "boolean",
            "char",
            "byte",
            "int",
            "short",
            "long",
            "float",
            "double",
            "java.lang.String",
            "java.util.UUID"})
    void containsKey_whenCalledForDefaultValue_shouldReturnTrue(String targetClassName) {
        final var map = new FixtureConstantValueProviderMap(Map.of());

        assertThat(map.containsKey(targetClassName)).isTrue();
    }

    @Test
    void containsKey_whenCalledForUnregisteredValue_shouldReturnFalse() {
        final var map = new FixtureConstantValueProviderMap(Map.of());

        assertThat(map.containsKey("unregisteredType")).isFalse();
    }

    @ParameterizedTest
    @CsvSource({
            "boolean, false",
            "char, '\u0000'",
            "byte, 0",
            "int, 0",
            "short, Short.valueOf((short)0)",
            "long, 0L",
            "float, 0.0F",
            "double, 0.0" })
    void get_whenCalledForDefaultValue_shouldReturnExpectedDefaultValue(String targetClassName, String expectedDefaultValue) {
        final var field = mock(VariableElement.class);
        final var map = new FixtureConstantValueProviderMap(Map.of());

        assertThat(map.get(targetClassName).provideValueAsString(field)).isEqualTo(expectedDefaultValue);
    }

    @Test
    void get_whenCalledForStringDefaultValue_shouldReturnFormattedStringValue() {
        final var field = mock(VariableElement.class);
        final var name = mock(Name.class);
        when(name.toString()).thenReturn("stringFieldName");
        when(field.getSimpleName()).thenReturn(name);

        final var map = new FixtureConstantValueProviderMap(Map.of());

        assertThat(map.get("java.lang.String").provideValueAsString(field)).isEqualTo("\"STRING_FIELD_NAME_VALUE\"");
    }

    @Test
    void get_whenCalledForUUIDDefaultValue_shouldReturnFormattedStringValue() {
        final var field = mock(VariableElement.class);
        final var uuid = UUID.fromString(RANDOM_UUID);
        try (final var uuidStatic = Mockito.mockStatic(UUID.class)) {
            uuidStatic.when(UUID::randomUUID).thenReturn(uuid);
            final var map = new FixtureConstantValueProviderMap(Map.of());

            assertThat(map.get("java.util.UUID").provideValueAsString(field)).isEqualTo("java.util.UUID.fromString(\"%s\")".formatted(RANDOM_UUID));
        }
    }

    @Test
    void get_whenDefaultIsOverwritten_shouldReturnCustomValue() {
        final var field = mock(VariableElement.class);
        final var map = new FixtureConstantValueProviderMap(Map.of("int", f -> "10"));

        assertThat(map.get("int").provideValueAsString(field)).isEqualTo("10");
    }
}