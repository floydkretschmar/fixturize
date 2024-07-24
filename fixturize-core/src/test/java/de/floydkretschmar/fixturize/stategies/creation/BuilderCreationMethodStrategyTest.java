package de.floydkretschmar.fixturize.stategies.creation;

import de.floydkretschmar.fixturize.TestFixtures;
import de.floydkretschmar.fixturize.annotations.FixtureBuilder;
import de.floydkretschmar.fixturize.domain.CreationMethod;
import de.floydkretschmar.fixturize.exceptions.FixtureCreationException;
import de.floydkretschmar.fixturize.stategies.constants.ConstantDefinitionMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.lang.model.element.VariableElement;
import java.util.Collection;
import java.util.List;

import static de.floydkretschmar.fixturize.FormattingConstants.WHITESPACE_16;
import static de.floydkretschmar.fixturize.TestFixtures.INT_FIELD_DEFINITION;
import static de.floydkretschmar.fixturize.TestFixtures.STRING_FIELD_DEFINITION;
import static de.floydkretschmar.fixturize.TestFixtures.createConstantDefinitionMapMock;
import static de.floydkretschmar.fixturize.TestFixtures.createDeclaredTypeFixture;
import static de.floydkretschmar.fixturize.TestFixtures.createExecutableElementFixture;
import static de.floydkretschmar.fixturize.TestFixtures.createFixtureBuilderFixture;
import static de.floydkretschmar.fixturize.TestFixtures.createTypeElementFixture;
import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.ElementKind.METHOD;
import static javax.lang.model.element.Modifier.PUBLIC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class BuilderCreationMethodStrategyTest {
    private ConstantDefinitionMap constantMap;
    private BuilderCreationMethodStrategy strategy;

    @BeforeEach
    void setup() {
        constantMap = createConstantDefinitionMapMock();
        strategy = new BuilderCreationMethodStrategy();
    }

    @Test
    void createCreationMethods_whenMultipleBuildersDefined_shouldCreateCreationMethodsForDefinedBuilders() {
        final var element = createTypeElementFixture(
                "TestObject",
                createFixtureBuilderFixture("methodName", "builder", "stringField", "intField"),
                createFixtureBuilderFixture("methodName2", "builder2", "uuidField"));

        final var result = strategy.generateCreationMethods(element, constantMap, TestFixtures.createMetadataFixture("TestObject"));

        assertThat(result).hasSize(2);
        assertThat(result.stream()).contains(
                CreationMethod.builder()
                        .returnType("TestObject.TestObjectBuilder")
                        .returnValue("TestObject.builder()\n%s.stringField(stringFieldName)\n%s.intField(intFieldName)"
                                .formatted(WHITESPACE_16, WHITESPACE_16))
                        .name("methodName")
                        .build(),
                CreationMethod.builder()
                        .returnType("TestObject.TestObjectBuilder")
                        .returnValue("TestObject.builder2()\n%s.uuidField(uuidFieldName)"
                                .formatted(WHITESPACE_16))
                        .name("methodName2")
                        .build());

        verify(constantMap, times(1)).getMatchingConstants(List.of("stringField", "intField"));
        verify(constantMap, times(1)).getMatchingConstants(List.of("uuidField"));
        verify(element, times(1)).getAnnotationsByType(FixtureBuilder.class);
        verify(element, times(2)).getEnclosedElements();
        verifyNoMoreInteractions(constantMap, element);
    }

    @Test
    void createCreationMethods_whenBuildMethodIsFound_shouldCreateCreationMethodUsingBuilderClass() {
        final var builderClassType = createDeclaredTypeFixture("TestObject.TestObjectBuilder", CLASS);
        final var stringSetter = createExecutableElementFixture("setStringField", METHOD, builderClassType, PUBLIC);
        when(stringSetter.getParameters()).thenReturn((List) List.of(mock(VariableElement.class)));
        final var intSetter = createExecutableElementFixture("setIntField", METHOD, builderClassType, PUBLIC);
        when(intSetter.getParameters()).thenReturn((List) List.of(mock(VariableElement.class)));
        when(builderClassType.asElement().getEnclosedElements()).thenReturn((List) List.of(stringSetter, intSetter));

        final var builderMethod = createExecutableElementFixture("builder", METHOD, builderClassType);
        final var element = createTypeElementFixture(
                "TestObject",
                createFixtureBuilderFixture("methodName", "builder", "stringField", "intField"));
        when(element.getEnclosedElements()).thenReturn((List) List.of(builderMethod));

        final var result = strategy.generateCreationMethods(element, constantMap, TestFixtures.createMetadataFixture("TestObject"));

        assertThat(result).hasSize(1);
        assertThat(result.stream()).contains(
                CreationMethod.builder()
                        .returnType("TestObject.TestObjectBuilder")
                        .returnValue("TestObject.builder()\n%s.setStringField(stringFieldName)\n%s.setIntField(intFieldName)"
                                .formatted(WHITESPACE_16, WHITESPACE_16))
                        .name("methodName")
                        .build());
        verify(constantMap, times(1)).getMatchingConstants(List.of("stringField", "intField"));
        verify(element, times(1)).getAnnotationsByType(FixtureBuilder.class);
        verify(element, times(1)).getEnclosedElements();
        verifyNoMoreInteractions(constantMap, element);
    }


    @Test
    void createCreationMethods_whenSingleBuilderDefined_shouldCreateCreationMethodForDefinedBuilder() {
        final var element = createTypeElementFixture(
                "TestObject",
                createFixtureBuilderFixture("methodName", "builder", "stringField", "intField"));

        final var result = strategy.generateCreationMethods(element, constantMap, TestFixtures.createMetadataFixture("TestObject"));

        assertThat(result).hasSize(1);
        assertThat(result.stream()).contains(
                CreationMethod.builder()
                        .returnType("TestObject.TestObjectBuilder")
                        .returnValue("TestObject.builder()\n%s.stringField(stringFieldName)\n%s.intField(intFieldName)"
                                .formatted(WHITESPACE_16, WHITESPACE_16))
                        .name("methodName")
                        .build());
        verify(constantMap, times(1)).getMatchingConstants(List.of("stringField", "intField"));
        verify(element, times(1)).getAnnotationsByType(FixtureBuilder.class);
        verify(element, times(1)).getEnclosedElements();
        verifyNoMoreInteractions(constantMap, element);
    }

    @Test
    void createCreationMethods_whenGenericDefined_shouldCreateCreationMethodForDefinedBuilder() {
        final var element = createTypeElementFixture(
                "TestObject",
                createFixtureBuilderFixture("methodName", "builder", "stringField", "intField"));

        final var result = strategy.generateCreationMethods(element, constantMap, TestFixtures.createMetadataFixtureBuilder("TestObject", "<String>").build());

        assertThat(result).hasSize(1);
        assertThat(result.stream()).contains(
                CreationMethod.builder()
                        .returnType("TestObject.TestObjectBuilder<String>")
                        .returnValue("TestObject.<String>builder()\n%s.stringField(stringFieldName)\n%s.intField(intFieldName)"
                                .formatted(WHITESPACE_16, WHITESPACE_16))
                        .name("methodName")
                        .build());
        verify(constantMap, times(1)).getMatchingConstants(List.of("stringField", "intField"));
        verify(element, times(1)).getAnnotationsByType(FixtureBuilder.class);
        verify(element, times(1)).getEnclosedElements();
        verifyNoMoreInteractions(constantMap, element);
    }

    @Test
    void createCreationMethods_whenNoBuilderDefined_shouldReturnEmptyList() {
        final var element = TestFixtures.<FixtureBuilder>createTypeElementFixture("TestObject");

        final Collection<CreationMethod> result = strategy.generateCreationMethods(element, constantMap, TestFixtures.createMetadataFixture("TestObject"));

        assertThat(result).hasSize(0);

        verify(element, times(1)).getAnnotationsByType(FixtureBuilder.class);
        verifyNoMoreInteractions(element);
        verifyNoInteractions(constantMap);
    }

    @Test
    void createCreationMethods_whenBuilderHasNoFieldsDefined_shouldCreateCreationMethodUsingAllFields() {
        final var element = createTypeElementFixture(
                "TestObject",
                createFixtureBuilderFixture("methodName", "builder"));

        constantMap = mock(ConstantDefinitionMap.class);
        when(constantMap.values()).thenReturn(List.of(STRING_FIELD_DEFINITION, INT_FIELD_DEFINITION));

        final var result = strategy.generateCreationMethods(element, constantMap, TestFixtures.createMetadataFixture("TestObject"));

        assertThat(result).hasSize(1);
        assertThat(result.stream()).contains(
                CreationMethod.builder()
                        .returnType("TestObject.TestObjectBuilder")
                        .returnValue("TestObject.builder()\n%s.stringField(stringFieldName)\n%s.intField(intFieldName)"
                                .formatted(WHITESPACE_16, WHITESPACE_16))
                        .name("methodName")
                        .build());

        verify(constantMap, times(1)).values();
        verify(element, times(1)).getAnnotationsByType(FixtureBuilder.class);
        verify(element, times(1)).getEnclosedElements();
        verifyNoMoreInteractions(constantMap, element);
    }

    @Test
    void createCreationMethods_whenCalledWithParameterThatDoesNotMatchConstant_shouldThrowFixtureCreationException() {
        final var element = createTypeElementFixture(
                "TestObject",
                createFixtureBuilderFixture("methodName", "builder", "stringField"));

        constantMap = mock(ConstantDefinitionMap.class);
        when(constantMap.getMatchingConstants(anyCollection())).thenThrow(new FixtureCreationException("error"));

        assertThrows(FixtureCreationException.class, () -> strategy.generateCreationMethods(element, constantMap, TestFixtures.createMetadataFixture("TestObject")));
        verify(constantMap, times(1)).getMatchingConstants(List.of("stringField"));
        verify(element, times(1)).getAnnotationsByType(FixtureBuilder.class);
        verifyNoMoreInteractions(constantMap, element);
    }
}