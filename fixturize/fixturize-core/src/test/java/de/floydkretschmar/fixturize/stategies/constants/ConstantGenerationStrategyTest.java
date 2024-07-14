package de.floydkretschmar.fixturize.stategies.constants;

import de.floydkretschmar.fixturize.annotations.FixtureConstant;
import de.floydkretschmar.fixturize.annotations.FixtureConstants;
import de.floydkretschmar.fixturize.domain.FixtureConstantDefinition;
import de.floydkretschmar.fixturize.domain.FixtureConstantValueProviderMap;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ConstantGenerationStrategyTest {

    private static final String RANDOM_UUID = "6b21f215-bf9e-445a-9dd2-5808a3a98d52";

    @Test
    void generateConstants_whenCalledWithValidClass_shouldGeneratedConstants() {
        final var stategy = new ConstantGenerationStrategy(new CamelCaseToScreamingSnakeCaseNamingStrategy(), new FixtureConstantValueProviderMap(Map.of()));

        final var fields = List.of(
                createVariableElemementMock("booleanField", boolean.class, null),
                createVariableElemementMock("intField", int.class, null),
                createVariableElemementMock("stringField", String.class, null),
                createVariableElemementMock("uuidField", UUID.class, null)
        );

        final var element = mock(TypeElement.class);
        when(element.getEnclosedElements()).thenReturn((List)fields);
        final var uuid = UUID.fromString(RANDOM_UUID);

        try (final var uuidStatic = Mockito.mockStatic(UUID.class)) {
            uuidStatic.when(UUID::randomUUID).thenReturn(uuid);
            final var result = stategy.generateConstants(element);

            assertThat(result).containsAllEntriesOf(Map.of(
                    "booleanField", FixtureConstantDefinition.builder().originalFieldName("booleanField").value("false").type("boolean").name("BOOLEAN_FIELD").build(),
                    "intField", FixtureConstantDefinition.builder().originalFieldName("intField").value("0").type("int").name("INT_FIELD").build(),
                    "stringField", FixtureConstantDefinition.builder().originalFieldName("stringField").value("\"STRING_FIELD_VALUE\"").type("java.lang.String").name("STRING_FIELD").build(),
                    "uuidField", FixtureConstantDefinition.builder().originalFieldName("uuidField").value("java.util.UUID.fromString(\"%s\")".formatted(RANDOM_UUID)).type("java.util.UUID").name("UUID_FIELD").build()));
        }
    }

    @Test
    void generateConstants_whenDefaultValueProviderGetsOverwritten_shouldGeneratedConstantsUsingExternalValueProvider() {
        final var stategy = new ConstantGenerationStrategy(new CamelCaseToScreamingSnakeCaseNamingStrategy(),
                new FixtureConstantValueProviderMap(Map.of(UUID.class.getName(), field -> "EXTERNAL_VALUE")));

        final var fields = List.of(
                createVariableElemementMock("booleanField", boolean.class, null),
                createVariableElemementMock("intField", int.class, null),
                createVariableElemementMock("stringField", String.class, null),
                createVariableElemementMock("uuidField", UUID.class, null)
        );
        final var element = mock(TypeElement.class);
        when(element.getEnclosedElements()).thenReturn((List)fields);

        final var result = stategy.generateConstants(element);

        assertThat(result).containsAllEntriesOf(Map.of(
                "booleanField", FixtureConstantDefinition.builder().originalFieldName("booleanField").value("false").type("boolean").name("BOOLEAN_FIELD").build(),
                "intField", FixtureConstantDefinition.builder().originalFieldName("intField").value("0").type("int").name("INT_FIELD").build(),
                "stringField", FixtureConstantDefinition.builder().originalFieldName("stringField").value("\"STRING_FIELD_VALUE\"").type("java.lang.String").name("STRING_FIELD").build(),
                "uuidField", FixtureConstantDefinition.builder().originalFieldName("uuidField").value("EXTERNAL_VALUE").type("java.util.UUID").name("UUID_FIELD").build()));
    }

    @Test
    void generateConstants_whenCalledWithAttributeWithUnknownValue_shouldSetValueToNull() {
        final var stategy = new ConstantGenerationStrategy(new CamelCaseToScreamingSnakeCaseNamingStrategy(), new FixtureConstantValueProviderMap(Map.of()));

        final var fields = List.of(
                createVariableElemementMock("booleanField", boolean.class, null),
                createVariableElemementMock("intField", int.class, null),
                createVariableElemementMock("stringField", String.class, null),
                createVariableElemementMock("unknownObject", Date.class, null)
        );
        final var element = mock(TypeElement.class);
        when(element.getEnclosedElements()).thenReturn((List)fields);
        final var result = stategy.generateConstants(element);

        assertThat(result).containsAllEntriesOf(Map.of(
                "booleanField", FixtureConstantDefinition.builder().originalFieldName("booleanField").value("false").type("boolean").name("BOOLEAN_FIELD").build(),
                "intField", FixtureConstantDefinition.builder().originalFieldName("intField").value("0").type("int").name("INT_FIELD").build(),
                "stringField", FixtureConstantDefinition.builder().originalFieldName("stringField").value("\"STRING_FIELD_VALUE\"").type("java.lang.String").name("STRING_FIELD").build(),
                "unknownObject", FixtureConstantDefinition.builder().originalFieldName("unknownObject").value("null").type("java.util.Date").name("UNKNOWN_OBJECT").build()));
    }

    @Test
    void generateConstants_whenCalledWithFixtureConstantAnnotation_shouldUseNameFromAnnotationAsKeyAndName() {
        final var stategy = new ConstantGenerationStrategy(new CamelCaseToScreamingSnakeCaseNamingStrategy(), new FixtureConstantValueProviderMap(Map.of()));

        final var annotation = mock(FixtureConstant.class);
        when(annotation.name()).thenReturn("CUSTOM_NAME");
        when(annotation.value()).thenReturn("true");
        final var annotation2 = mock(FixtureConstant.class);
        when(annotation2.name()).thenReturn("CUSTOM_NAME_2");
        when(annotation2.value()).thenReturn("");

        final var fields = List.of(
            createVariableElemementMock("booleanField", boolean.class, new FixtureConstant[]{annotation}),
            createVariableElemementMock("booleanField2", boolean.class, new FixtureConstant[]{annotation2})
        );
        final var element = mock(TypeElement.class);
        when(element.getEnclosedElements()).thenReturn((List)fields);

        final var result = stategy.generateConstants(element);

        assertThat(result).containsAllEntriesOf(Map.of(
                "CUSTOM_NAME", FixtureConstantDefinition.builder().originalFieldName("booleanField").value("true").type("boolean").name("CUSTOM_NAME").build(),
                "CUSTOM_NAME_2", FixtureConstantDefinition.builder().originalFieldName("booleanField2").value("false").type("boolean").name("CUSTOM_NAME_2").build()
        ));
    }

    @Test
    void generateConstants_whenCalledWithMultipleFixtureConstantsAnnotations_shouldGenerateConstantPerAnnotation() {
        final var stategy = new ConstantGenerationStrategy(new CamelCaseToScreamingSnakeCaseNamingStrategy(), new FixtureConstantValueProviderMap(Map.of()));

        final FixtureConstant annotation = mock(FixtureConstant.class);
        when(annotation.name()).thenReturn("CUSTOM_NAME");
        when(annotation.value()).thenReturn("true");
        final FixtureConstant annotation2 = mock(FixtureConstant.class);
        when(annotation2.name()).thenReturn("CUSTOM_NAME_2");
        when(annotation2.value()).thenReturn("");

        final var fields = List.of(
            createVariableElemementMock("booleanField", boolean.class, new FixtureConstant[]{annotation, annotation2})
        );
        final var element = mock(TypeElement.class);
        when(element.getEnclosedElements()).thenReturn((List)fields);

        final var result = stategy.generateConstants(element);

        assertThat(result).containsAllEntriesOf(Map.of(
                "CUSTOM_NAME", FixtureConstantDefinition.builder().originalFieldName("booleanField").value("true").type("boolean").name("CUSTOM_NAME").build(),
                "CUSTOM_NAME_2", FixtureConstantDefinition.builder().originalFieldName("booleanField").value("false").type("boolean").name("CUSTOM_NAME_2").build()
        ));
    }

    private static VariableElement createVariableElemementMock(String name, Class<?> targetClass, FixtureConstant[] fixtureConstants) {
        final var fieldElement = mock(VariableElement.class);
        final var typeMirror = mock(TypeMirror.class);
        final var fieldName = mock(Name.class);

        when(fieldName.toString()).thenReturn(name);
        when(fieldElement.asType()).thenReturn(typeMirror);
        when(typeMirror.toString()).thenReturn(targetClass.getName());
        when(fieldElement.getSimpleName()).thenReturn(fieldName);
        when(fieldElement.getKind()).thenReturn(ElementKind.FIELD);

        if (Objects.nonNull(fixtureConstants) && fixtureConstants.length == 1)
            when(fieldElement.getAnnotation(ArgumentMatchers.argThat(param -> param.equals(FixtureConstant.class)))).thenReturn(fixtureConstants[0]);
        else if (Objects.nonNull(fixtureConstants) && fixtureConstants.length > 1) {
            final var constants = mock(FixtureConstants.class);
            when(constants.value()).thenReturn(fixtureConstants);
            when(fieldElement.getAnnotation(ArgumentMatchers.argThat(param -> param.equals(FixtureConstants.class)))).thenReturn(constants);
        }

        return fieldElement;
    }
}