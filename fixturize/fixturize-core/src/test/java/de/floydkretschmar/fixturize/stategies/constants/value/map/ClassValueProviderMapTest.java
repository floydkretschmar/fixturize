package de.floydkretschmar.fixturize.stategies.constants.value.map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;

import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;
import java.util.Map;
import java.util.UUID;

import static de.floydkretschmar.fixturize.TestFixtures.RANDOM_UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ClassValueProviderMapTest {

    @ParameterizedTest
    @CsvSource({
            "java.lang.String",
            "java.util.UUID",
            "java.lang.Boolean",
            "java.lang.Byte",
            "java.lang.Double",
            "java.lang.Float",
            "java.lang.Integer",
            "java.lang.Long",
            "java.lang.Short",
            "java.time.Instant",
            "java.time.Duration",
            "java.time.LocalDate",
            "java.time.LocalDateTime",
            "java.time.LocalTime",
            "java.util.Collection",
            "java.util.List",
            "java.util.Map",
            "java.util.Set",
            "java.util.Queue",
            "java.util.Date"})
    void containsKey_whenCalledForDefaultValue_shouldReturnTrue(String targetClassName) {
        final var map = new ClassValueProviderMap(Map.of());

        assertThat(map.containsKey(targetClassName)).isTrue();
    }

    @Test
    void containsKey_whenCalledForUnregisteredValue_shouldReturnFalse() {
        final var map = new ClassValueProviderMap(Map.of());

        assertThat(map.containsKey("unregisteredType")).isFalse();
    }

    @ParameterizedTest
    @CsvSource({
            "java.lang.Boolean, false",
            "java.lang.Byte, 0",
            "java.lang.Double, 0.0",
            "java.lang.Float, 0.0F",
            "java.lang.Integer, 0",
            "java.lang.Long, 0L",
            "java.lang.Short, Short.valueOf((short)0)",
            "java.time.Instant, java.time.Instant.now()",
            "java.time.Duration, java.time.Duration.ZERO",
            "java.time.LocalDate, java.time.LocalDate.now()",
            "java.time.LocalDateTime, java.time.LocalDateTime.now()",
            "java.time.LocalTime, java.time.LocalTime.now()",
            "java.util.Collection, java.util.List.of()",
            "java.util.List, java.util.List.of()",
            "java.util.Map, java.util.Map.of()",
            "java.util.Set, java.util.Set.of()",
            "java.util.Queue, new java.util.PriorityQueue<>()",
            "java.util.Date, new java.util.Date()" })
    void get_whenCalledForDefaultValue_shouldReturnExpectedDefaultValue(String targetClassName, String expectedDefaultValue) {
        final var field = mock(VariableElement.class);
        final var map = new ClassValueProviderMap(Map.of());

        assertThat(map.get(targetClassName).provideValueAsString(field)).isEqualTo(expectedDefaultValue);
    }

    @Test
    void get_whenCalledForCharacter_shouldReturnExpectedDefaultValue() {
        final var field = mock(VariableElement.class);
        final var map = new ClassValueProviderMap(Map.of());

        assertThat(map.get("java.lang.Character").provideValueAsString(field)).isEqualTo("' '");
    }

    @Test
    void get_whenCalledForStringDefaultValue_shouldReturnFormattedStringValue() {
        final var field = mock(VariableElement.class);
        final var name = mock(Name.class);
        when(name.toString()).thenReturn("stringFieldName");
        when(field.getSimpleName()).thenReturn(name);

        final var map = new ClassValueProviderMap(Map.of());

        assertThat(map.get("java.lang.String").provideValueAsString(field)).isEqualTo("\"STRING_FIELD_NAME_VALUE\"");
    }

    @Test
    void get_whenCalledForUUIDDefaultValue_shouldReturnFormattedStringValue() {
        final var field = mock(VariableElement.class);
        final var uuid = UUID.fromString(RANDOM_UUID);
        try (final var uuidStatic = Mockito.mockStatic(UUID.class)) {
            uuidStatic.when(UUID::randomUUID).thenReturn(uuid);
            final var map = new ClassValueProviderMap(Map.of());

            assertThat(map.get("java.util.UUID").provideValueAsString(field)).isEqualTo("java.util.UUID.fromString(\"%s\")".formatted(RANDOM_UUID));
        }
    }

    @Test
    void get_whenDefaultIsOverwritten_shouldReturnCustomValue() {
        final var field = mock(VariableElement.class);
        final var map = new ClassValueProviderMap(Map.of("java.util.UUID", f -> "10"));

        assertThat(map.get("java.util.UUID").provideValueAsString(field)).isEqualTo("10");
    }
}