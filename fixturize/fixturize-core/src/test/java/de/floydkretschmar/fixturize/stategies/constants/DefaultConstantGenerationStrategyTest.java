package de.floydkretschmar.fixturize.stategies.constants;

import de.floydkretschmar.fixturize.domain.FixtureConstant;
import de.floydkretschmar.fixturize.domain.FixtureCreationMethod;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DefaultConstantGenerationStrategyTest {

    private static final String RANDOM_UUID = "6b21f215-bf9e-445a-9dd2-5808a3a98d52";

    @Test
    void generateConstants_whenCalledWithValidClass_shouldGeneratedConstants() {
        final var stategy = new DefaultConstantGenerationStrategy(new CamelCaseToScreamingSnakeCaseNamingStrategy(), Map.of());

        final List<? extends Element> fields = mockVariableElements(List.of(
                Map.entry("booleanField", boolean.class),
                Map.entry("intField", int.class),
                Map.entry("stringField", String.class),
                Map.entry("uuidField", UUID.class)
        ));
        final var element = mock(Element.class);
        when(element.getEnclosedElements()).thenReturn((List)fields);
        final var uuid = UUID.fromString(RANDOM_UUID);
        try (MockedStatic<UUID> uuidStatic = Mockito.mockStatic(UUID.class)) {
            uuidStatic.when(UUID::randomUUID).thenReturn(uuid);
            final Collection<FixtureConstant> result = stategy.generateConstants(element);

            assertThat(result).containsAll(List.of(
                    FixtureConstant.builder().value("false").type("boolean").name("BOOLEAN_FIELD").build(),
                    FixtureConstant.builder().value("0").type("int").name("INT_FIELD").build(),
                    FixtureConstant.builder().value("\"STRING_FIELD_VALUE\"").type("java.lang.String").name("STRING_FIELD").build(),
                    FixtureConstant.builder().value("java.util.UUID.fromString(\"%s\")".formatted(RANDOM_UUID)).type("java.util.UUID").name("UUID_FIELD").build()));
        }
    }

    @Test
    void generateConstants_whenDefaultValueProviderGetsOverwritten_shouldGeneratedConstantsUsingExternalValueProvider() {
        var stategy = new DefaultConstantGenerationStrategy(new CamelCaseToScreamingSnakeCaseNamingStrategy(),
                Map.of(UUID.class.getName(), field -> "EXTERNAL_VALUE"));

        final List<? extends Element> fields = mockVariableElements(List.of(
                Map.entry("booleanField", boolean.class),
                Map.entry("intField", int.class),
                Map.entry("stringField", String.class),
                Map.entry("uuidField", UUID.class)
        ));
        final var element = mock(Element.class);
        when(element.getEnclosedElements()).thenReturn((List)fields);

        final Collection<FixtureConstant> result = stategy.generateConstants(element);

        assertThat(result).containsAll(List.of(
                FixtureConstant.builder().value("false").type("boolean").name("BOOLEAN_FIELD").build(),
                FixtureConstant.builder().value("0").type("int").name("INT_FIELD").build(),
                FixtureConstant.builder().value("\"STRING_FIELD_VALUE\"").type("java.lang.String").name("STRING_FIELD").build(),
                FixtureConstant.builder().value("EXTERNAL_VALUE").type("java.util.UUID").name("UUID_FIELD").build()));
    }

    @Test
    void generateConstants_whenCalledWithAttributeWithUnknownValue_shouldSetValueToNull() {
        var stategy = new DefaultConstantGenerationStrategy(new CamelCaseToScreamingSnakeCaseNamingStrategy(),
                Map.of());

        final List<? extends Element> fields = mockVariableElements(List.of(
                Map.entry("booleanField", boolean.class),
                Map.entry("intField", int.class),
                Map.entry("stringField", String.class),
                Map.entry("uuidField", UUID.class),
                Map.entry("unknownObject", Date.class)
        ));
        final var element = mock(Element.class);
        when(element.getEnclosedElements()).thenReturn((List)fields);
        final var uuid = UUID.fromString(RANDOM_UUID);
        try (MockedStatic<UUID> uuidStatic = Mockito.mockStatic(UUID.class)) {
            uuidStatic.when(UUID::randomUUID).thenReturn(uuid);
            final Collection<FixtureConstant> result = stategy.generateConstants(element);

            assertThat(result).containsAll(List.of(
                    FixtureConstant.builder().value("false").type("boolean").name("BOOLEAN_FIELD").build(),
                    FixtureConstant.builder().value("0").type("int").name("INT_FIELD").build(),
                    FixtureConstant.builder().value("\"STRING_FIELD_VALUE\"").type("java.lang.String").name("STRING_FIELD").build(),
                    FixtureConstant.builder().value("null").type("java.util.Date").name("UNKNOWN_OBJECT").build(),
                    FixtureConstant.builder().value("java.util.UUID.fromString(\"%s\")".formatted(RANDOM_UUID)).type("java.util.UUID").name("UUID_FIELD").build()));
        }
    }

    private static @NotNull List<VariableElement> mockVariableElements(List<Map.Entry<String, Class<?>>> fields) {
        return fields.stream().map(field -> {
            final VariableElement fieldElement = mock(VariableElement.class);
            final TypeMirror typeMirror = mock(TypeMirror.class);
            final Name fieldName = mock(Name.class);
            when(fieldName.toString()).thenReturn(field.getKey());
            when(fieldElement.asType()).thenReturn(typeMirror);
            when(typeMirror.toString()).thenReturn(field.getValue().getName());
            when(fieldElement.getSimpleName()).thenReturn(fieldName);
            when(fieldElement.getKind()).thenReturn(ElementKind.FIELD);
            return fieldElement;
        }).collect(Collectors.toList());
    }
}