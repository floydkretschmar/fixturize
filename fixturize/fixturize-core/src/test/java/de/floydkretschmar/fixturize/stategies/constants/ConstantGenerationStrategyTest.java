package de.floydkretschmar.fixturize.stategies.constants;

import de.floydkretschmar.fixturize.annotations.FixtureConstant;
import de.floydkretschmar.fixturize.domain.FixtureConstantDefinition;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ConstantGenerationStrategyTest {

    private static final String RANDOM_UUID = "6b21f215-bf9e-445a-9dd2-5808a3a98d52";

    @Test
    void generateConstants_whenCalledWithValidClass_shouldGeneratedConstants() {
        final var stategy = new ConstantGenerationStrategy(new CamelCaseToScreamingSnakeCaseNamingStrategy(), Map.of());

        final List<? extends Element> fields = Stream.of(
                Map.entry("booleanField", boolean.class),
                Map.entry("intField", int.class),
                Map.entry("stringField", String.class),
                Map.entry("uuidField", UUID.class)
        ).map(field -> createVariableElemementMock(field.getKey(), field.getValue(), null)).collect(Collectors.toList());

        final var element = mock(TypeElement.class);
        when(element.getEnclosedElements()).thenReturn((List)fields);
        final var uuid = UUID.fromString(RANDOM_UUID);

        try (MockedStatic<UUID> uuidStatic = Mockito.mockStatic(UUID.class)) {
            uuidStatic.when(UUID::randomUUID).thenReturn(uuid);
            final Map<String, FixtureConstantDefinition> result = stategy.generateConstants(element);

            assertThat(result).containsAllEntriesOf(Map.of(
                    "booleanField", FixtureConstantDefinition.builder().value("false").type("boolean").name("BOOLEAN_FIELD").build(),
                    "intField", FixtureConstantDefinition.builder().value("0").type("int").name("INT_FIELD").build(),
                    "stringField", FixtureConstantDefinition.builder().value("\"STRING_FIELD_VALUE\"").type("java.lang.String").name("STRING_FIELD").build(),
                    "uuidField", FixtureConstantDefinition.builder().value("java.util.UUID.fromString(\"%s\")".formatted(RANDOM_UUID)).type("java.util.UUID").name("UUID_FIELD").build()));
        }
    }

    @Test
    void generateConstants_whenDefaultValueProviderGetsOverwritten_shouldGeneratedConstantsUsingExternalValueProvider() {
        var stategy = new ConstantGenerationStrategy(new CamelCaseToScreamingSnakeCaseNamingStrategy(),
                Map.of(UUID.class.getName(), field -> "EXTERNAL_VALUE"));

        final List<? extends Element> fields = Stream.of(
                Map.entry("booleanField", boolean.class),
                Map.entry("intField", int.class),
                Map.entry("stringField", String.class),
                Map.entry("uuidField", UUID.class)
        ).map(field -> createVariableElemementMock(field.getKey(), field.getValue(), null)).collect(Collectors.toList());
        final var element = mock(TypeElement.class);
        when(element.getEnclosedElements()).thenReturn((List)fields);

        final Map<String, FixtureConstantDefinition> result = stategy.generateConstants(element);

        assertThat(result).containsAllEntriesOf(Map.of(
                "booleanField", FixtureConstantDefinition.builder().value("false").type("boolean").name("BOOLEAN_FIELD").build(),
                "intField", FixtureConstantDefinition.builder().value("0").type("int").name("INT_FIELD").build(),
                "stringField", FixtureConstantDefinition.builder().value("\"STRING_FIELD_VALUE\"").type("java.lang.String").name("STRING_FIELD").build(),
                "uuidField", FixtureConstantDefinition.builder().value("EXTERNAL_VALUE").type("java.util.UUID").name("UUID_FIELD").build()));
    }

    @Test
    void generateConstants_whenCalledWithAttributeWithUnknownValue_shouldSetValueToNull() {
        var stategy = new ConstantGenerationStrategy(new CamelCaseToScreamingSnakeCaseNamingStrategy(), Map.of());

        final List<? extends Element> fields = Stream.of(
                Map.entry("booleanField", boolean.class),
                Map.entry("intField", int.class),
                Map.entry("stringField", String.class),
                Map.entry("unknownObject", Date.class)
        ).map(field -> createVariableElemementMock(field.getKey(), field.getValue(), null)).collect(Collectors.toList());
        final var element = mock(TypeElement.class);
        when(element.getEnclosedElements()).thenReturn((List)fields);
        final Map<String, FixtureConstantDefinition> result = stategy.generateConstants(element);

        assertThat(result).containsAllEntriesOf(Map.of(
                "booleanField", FixtureConstantDefinition.builder().value("false").type("boolean").name("BOOLEAN_FIELD").build(),
                "intField", FixtureConstantDefinition.builder().value("0").type("int").name("INT_FIELD").build(),
                "stringField", FixtureConstantDefinition.builder().value("\"STRING_FIELD_VALUE\"").type("java.lang.String").name("STRING_FIELD").build(),
                "unknownObject", FixtureConstantDefinition.builder().value("null").type("java.util.Date").name("UNKNOWN_OBJECT").build()));
    }

    @Test
    void generateConstants_whenCalledWithFixtureConstantAnnotation_shouldUseNameFromAnnotation() {
        final var stategy = new ConstantGenerationStrategy(new CamelCaseToScreamingSnakeCaseNamingStrategy(), Map.of());

        final FixtureConstant annotation = mock(FixtureConstant.class);
        when(annotation.name()).thenReturn("CUSTOM_NAME");

        final List<? extends Element> fields = Stream.of(
            Map.entry("booleanField", boolean.class)
        ).map(field -> createVariableElemementMock(field.getKey(), field.getValue(), annotation)).collect(Collectors.toList());
        final var element = mock(TypeElement.class);
        when(element.getEnclosedElements()).thenReturn((List)fields);

        final Map<String, FixtureConstantDefinition> result = stategy.generateConstants(element);

        assertThat(result).containsAllEntriesOf(Map.of(
                "booleanField", FixtureConstantDefinition.builder().value("false").type("boolean").name("CUSTOM_NAME").build()));
    }

    private static @NotNull VariableElement createVariableElemementMock(String name, Class<?> targetClass, FixtureConstant annotation) {
        final VariableElement fieldElement = mock(VariableElement.class);
        final TypeMirror typeMirror = mock(TypeMirror.class);
        final Name fieldName = mock(Name.class);
        when(fieldElement.getAnnotation(any())).thenReturn(annotation);
        when(fieldName.toString()).thenReturn(name);
        when(fieldElement.asType()).thenReturn(typeMirror);
        when(typeMirror.toString()).thenReturn(targetClass.getName());
        when(fieldElement.getSimpleName()).thenReturn(fieldName);
        when(fieldElement.getKind()).thenReturn(ElementKind.FIELD);
        return fieldElement;
    }
}