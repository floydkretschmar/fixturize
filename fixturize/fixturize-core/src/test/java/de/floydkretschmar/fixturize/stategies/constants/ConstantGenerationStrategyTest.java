package de.floydkretschmar.fixturize.stategies.constants;

import de.floydkretschmar.fixturize.annotations.FixtureConstant;
import de.floydkretschmar.fixturize.annotations.FixtureConstants;
import de.floydkretschmar.fixturize.domain.FixtureConstantDefinition;
import de.floydkretschmar.fixturize.stategies.constants.value.ValueProviderService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ConstantGenerationStrategyTest {
    @Test
    void generateConstants_whenCalledWithValidClass_shouldGeneratedConstants() {
        final var stategy = new ConstantGenerationStrategy(new CamelCaseToScreamingSnakeCaseNamingStrategy(), mockValueMap());

        final var fields = List.of(
                createVariableElemementMock("booleanField", boolean.class, null),
                createVariableElemementMock("intField", int.class, null)
        );

        final var element = mock(TypeElement.class);
        when(element.getEnclosedElements()).thenReturn((List)fields);
        final var result = stategy.generateConstants(element);

        assertThat(result).containsAllEntriesOf(Map.of(
                "booleanField", FixtureConstantDefinition.builder().originalFieldName("booleanField").value("false").type("boolean").name("BOOLEAN_FIELD").build(),
                "intField", FixtureConstantDefinition.builder().originalFieldName("intField").value("0").type("int").name("INT_FIELD").build()));
    }

    @Test
    void generateConstants_whenCalledWithAttributeWithUnknownValue_shouldSetValueToNull() {

        final var fields = List.of(
                createVariableElemementMock("booleanField", boolean.class, null),
                createVariableElemementMock("intField", int.class, null),
                createVariableElemementMock("unknownObject", Date.class, null)
        );
        final var stategy = new ConstantGenerationStrategy(new CamelCaseToScreamingSnakeCaseNamingStrategy(), mockValueMap());
        final var element = mock(TypeElement.class);
        when(element.getEnclosedElements()).thenReturn((List)fields);
        final var result = stategy.generateConstants(element);

        assertThat(result).containsAllEntriesOf(Map.of(
                "booleanField", FixtureConstantDefinition.builder().originalFieldName("booleanField").value("false").type("boolean").name("BOOLEAN_FIELD").build(),
                "intField", FixtureConstantDefinition.builder().originalFieldName("intField").value("0").type("int").name("INT_FIELD").build(),
                "unknownObject", FixtureConstantDefinition.builder().originalFieldName("unknownObject").value("null").type("java.util.Date").name("UNKNOWN_OBJECT").build()));
    }

    @Test
    void generateConstants_whenCalledWithFixtureConstantAnnotation_shouldUseNameFromAnnotationAsKeyAndName() {
        final var stategy = new ConstantGenerationStrategy(new CamelCaseToScreamingSnakeCaseNamingStrategy(), mockValueMap());

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
        final var stategy = new ConstantGenerationStrategy(new CamelCaseToScreamingSnakeCaseNamingStrategy(), mockValueMap());

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

    private ValueProviderService mockValueMap() {
        final var valueService = mock(ValueProviderService.class);
        when(valueService.getValueFor(any())).thenReturn("null");
        when(valueService.getValueFor(ArgumentMatchers.argThat(arg -> Objects.nonNull(arg) && arg.asType().toString().equals("boolean")))).thenReturn("false");
        when(valueService.getValueFor(ArgumentMatchers.argThat(arg -> Objects.nonNull(arg) && arg.asType().toString().equals("int")))).thenReturn("0");
        return valueService;
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