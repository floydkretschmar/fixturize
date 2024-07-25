package de.floydkretschmar.fixturize.stategies.constants.value;

import de.floydkretschmar.fixturize.TestFixtures;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Types;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class ValueProviderMapTest {

    @Mock
    private ValueProviderService service;

    @Mock
    private Types typeUtils;

    @ParameterizedTest
    @CsvSource({
            "boolean",
            "byte",
            "double",
            "float",
            "int",
            "long",
            "short",
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
            "java.util.Date",
            "java.util.Map",
            "java.util.List",
            "java.util.Collection",
            "java.util.Set",
            "java.util.Queue"
    })
    void containsKey_whenCalledForDefaultValue_shouldReturnTrue(String targetClassName) {
        final var map = new ValueProviderMap(Map.of(), typeUtils, service);

        assertThat(map.containsKey(targetClassName)).isTrue();
    }

    @Test
    void containsKey_whenCalledForUnregisteredValue_shouldReturnFalse() {
        final var map = new ValueProviderMap(Map.of(), typeUtils, service);

        assertThat(map.containsKey("unregisteredType")).isFalse();
    }

    @ParameterizedTest
    @CsvSource({
            "java.time.Instant, java.time.Instant.now()",
            "java.time.Duration, java.time.Duration.ZERO",
            "java.time.LocalDate, java.time.LocalDate.now()",
            "java.time.LocalDateTime, java.time.LocalDateTime.now()",
            "java.time.LocalTime, java.time.LocalTime.now()",
            "java.util.Date, new java.util.Date()" })
    void get_whenCalledForDefaultProvidersDefinedAsLambda_shouldReturnExpectedDefaultValue(String targetClassName, String expectedDefaultValue) {
        final var field = mock(VariableElement.class);
        final var map = new ValueProviderMap(Map.of(), typeUtils, service);
        final var names = TestFixtures.createMetadataFixture();

        assertThat(map.get(targetClassName).provideValueAsString(field, names)).isEqualTo(expectedDefaultValue);
    }

    @Test
    void get_whenDefaultIsOverwritten_shouldReturnCustomValue() {
        final var field = mock(VariableElement.class);
        final var names = TestFixtures.createMetadataFixture();
        final var map = new ValueProviderMap(Map.of("java.util.UUID", (f, n) -> "10"), typeUtils, service);

        assertThat(map.get("java.util.UUID").provideValueAsString(field, names)).isEqualTo("10");
    }
}