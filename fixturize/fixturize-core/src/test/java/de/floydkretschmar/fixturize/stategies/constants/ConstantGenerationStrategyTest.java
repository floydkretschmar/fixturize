package de.floydkretschmar.fixturize.stategies.constants;

import de.floydkretschmar.fixturize.TestFixtures;
import de.floydkretschmar.fixturize.annotations.FixtureConstant;
import de.floydkretschmar.fixturize.stategies.constants.value.ValueProviderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.lang.model.element.TypeElement;
import java.util.List;
import java.util.Map;

import static de.floydkretschmar.fixturize.TestFixtures.BOOLEAN_FIELD_DEFINITION;
import static de.floydkretschmar.fixturize.TestFixtures.BOOLEAN_FIELD_NAME;
import static de.floydkretschmar.fixturize.TestFixtures.INT_FIELD_DEFINITION;
import static de.floydkretschmar.fixturize.TestFixtures.INT_FIELD_NAME;
import static de.floydkretschmar.fixturize.TestFixtures.createConstantFixture;
import static de.floydkretschmar.fixturize.TestFixtures.createFixtureConstantFixture;
import static de.floydkretschmar.fixturize.TestFixtures.createNamingStrategyMock;
import static de.floydkretschmar.fixturize.TestFixtures.createValueProviderServiceMock;
import static de.floydkretschmar.fixturize.TestFixtures.createVariableElementFixture;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class ConstantGenerationStrategyTest {
    private ConstantsNamingStrategy namingStrategy;

    private ValueProviderService valueProviderService;

    private ConstantGenerationStrategy strategy;

    @BeforeEach
    void setup() {
        namingStrategy = createNamingStrategyMock();
        valueProviderService = createValueProviderServiceMock();
        strategy = new ConstantGenerationStrategy(namingStrategy, valueProviderService);
    }
    @Test
    void generateConstants_whenCalledWithValidClass_shouldGenerateConstants() {
        final var fields = List.of(
                TestFixtures.<FixtureConstant>createVariableElementFixture(BOOLEAN_FIELD_NAME),
                TestFixtures.<FixtureConstant>createVariableElementFixture(INT_FIELD_NAME)
        );

        final var element = mock(TypeElement.class);
        when(element.getEnclosedElements()).thenReturn((List)fields);

        final var result = strategy.generateConstants(element);

        assertThat(result).containsAllEntriesOf(Map.of(
                BOOLEAN_FIELD_NAME, BOOLEAN_FIELD_DEFINITION,
                INT_FIELD_NAME, INT_FIELD_DEFINITION));
        fields.forEach(field -> verify(field, times(1)).getAnnotationsByType(FixtureConstant.class));
        verify(namingStrategy, times(1)).createConstantName(BOOLEAN_FIELD_NAME);
        verify(namingStrategy, times(1)).createConstantName(INT_FIELD_NAME);
        verify(valueProviderService, times(1)).getValueFor(argThat(arg -> arg.getSimpleName().toString().equals(BOOLEAN_FIELD_NAME)));
        verify(valueProviderService, times(1)).getValueFor(argThat(arg -> arg.getSimpleName().toString().equals(INT_FIELD_NAME)));
        verifyNoMoreInteractions(namingStrategy, valueProviderService);
    }

    @Test
    void generateConstants_whenCalledWithFixtureConstantAnnotation_shouldUseNameFromAnnotationAsKeyAndName() {
        final var annotation = createFixtureConstantFixture("CUSTOM_NAME", "true");
        final var annotation2 = createFixtureConstantFixture("CUSTOM_NAME_2", "");

        final var fields = List.of(
                createVariableElementFixture(BOOLEAN_FIELD_NAME, annotation),
                createVariableElementFixture("booleanField2", annotation2)
        );
        final var element = mock(TypeElement.class);
        when(element.getEnclosedElements()).thenReturn((List) fields);

        final var result = strategy.generateConstants(element);

        assertThat(result).containsAllEntriesOf(Map.of(
                "CUSTOM_NAME", createConstantFixture(BOOLEAN_FIELD_NAME, "CUSTOM_NAME", "true"),
                "CUSTOM_NAME_2", createConstantFixture("booleanField2", "CUSTOM_NAME_2")
        ));
        fields.forEach(field -> verify(field, times(1)).getAnnotationsByType(FixtureConstant.class));
        verify(valueProviderService, times(1)).getValueFor(argThat(arg -> arg.getSimpleName().toString().equals("booleanField2")));
        verifyNoMoreInteractions(valueProviderService);
        verifyNoInteractions(namingStrategy);
    }

    @Test
    void generateConstants_whenCalledWithMultipleFixtureConstantsAnnotations_shouldGenerateConstantPerAnnotation() {
        final var annotation = createFixtureConstantFixture("CUSTOM_NAME", "true");
        final var annotation2 = createFixtureConstantFixture("CUSTOM_NAME_2", "");

        final var fields = List.of(
                createVariableElementFixture(BOOLEAN_FIELD_NAME, annotation, annotation2)
        );
        final var element = mock(TypeElement.class);
        when(element.getEnclosedElements()).thenReturn((List) fields);

        final var result = strategy.generateConstants(element);

        assertThat(result).containsAllEntriesOf(Map.of(
                "CUSTOM_NAME", createConstantFixture(BOOLEAN_FIELD_NAME, "CUSTOM_NAME", "true"),
                "CUSTOM_NAME_2", createConstantFixture(BOOLEAN_FIELD_NAME, "CUSTOM_NAME_2")
        ));
        fields.forEach(field -> verify(field, times(1)).getAnnotationsByType(FixtureConstant.class));
        verify(valueProviderService, times(1)).getValueFor(argThat(arg -> arg.getSimpleName().toString().equals(BOOLEAN_FIELD_NAME)));
        verifyNoMoreInteractions(valueProviderService);
        verifyNoInteractions(namingStrategy);
    }
}