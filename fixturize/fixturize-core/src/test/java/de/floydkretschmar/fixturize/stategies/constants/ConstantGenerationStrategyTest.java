package de.floydkretschmar.fixturize.stategies.constants;

import de.floydkretschmar.fixturize.TestFixtures;
import de.floydkretschmar.fixturize.annotations.FixtureConstant;
import de.floydkretschmar.fixturize.domain.Constant;
import de.floydkretschmar.fixturize.stategies.constants.value.ValueProviderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.Map;

import static de.floydkretschmar.fixturize.TestFixtures.BOOLEAN_FIELD_DEFINITION;
import static de.floydkretschmar.fixturize.TestFixtures.BOOLEAN_FIELD_NAME;
import static de.floydkretschmar.fixturize.TestFixtures.INT_FIELD_DEFINITION;
import static de.floydkretschmar.fixturize.TestFixtures.INT_FIELD_NAME;
import static de.floydkretschmar.fixturize.TestFixtures.createConstantFixture;
import static de.floydkretschmar.fixturize.TestFixtures.createDeclaredTypeFixture;
import static de.floydkretschmar.fixturize.TestFixtures.createFixtureConstantFixture;
import static de.floydkretschmar.fixturize.TestFixtures.createTypeMirrorFixture;
import static de.floydkretschmar.fixturize.TestFixtures.createVariableElementFixture;
import static javax.lang.model.element.ElementKind.FIELD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConstantGenerationStrategyTest {
    @Mock
    private ConstantsNamingStrategy namingStrategy;

    @Mock
    private ValueProviderService valueProviderService;

    private ConstantGenerationStrategy strategy;

    @BeforeEach
    void setup() {
        strategy = new ConstantGenerationStrategy(namingStrategy, valueProviderService);
        when(valueProviderService.getValueFor(any())).thenAnswer(param -> {
            final var element = (Element) param.getArguments()[0];

            if (element instanceof VariableElement)
                return "%sValue".formatted(element.getSimpleName().toString());
            else
                return "value";
        });
    }

    @Test
    void generateConstants_whenCalledWithValidClass_shouldGenerateConstants() {
        when(namingStrategy.createConstantName(anyString())).thenAnswer(param -> "%sName".formatted(param.getArguments()[0]));
        final var field1 = TestFixtures.<FixtureConstant>createVariableElementFixture(BOOLEAN_FIELD_NAME, createTypeMirrorFixture(BOOLEAN_FIELD_DEFINITION.getType()), ElementKind.FIELD);
        final var field2 = TestFixtures.<FixtureConstant>createVariableElementFixture(INT_FIELD_NAME, createTypeMirrorFixture(INT_FIELD_DEFINITION.getType()), ElementKind.FIELD);

        final var element = mock(TypeElement.class);
        when(element.getEnclosedElements()).thenReturn((List) List.of(field1, field2));

        final var result = strategy.generateConstants(element, TestFixtures.createMetadataFixture());

        assertThat(result).containsAllEntriesOf(Map.of(
                BOOLEAN_FIELD_NAME, BOOLEAN_FIELD_DEFINITION,
                INT_FIELD_NAME, INT_FIELD_DEFINITION));

        verify(field1, times(1)).getAnnotationsByType(FixtureConstant.class);
        verify(field2, times(1)).getAnnotationsByType(FixtureConstant.class);
        verify(namingStrategy, times(1)).createConstantName(BOOLEAN_FIELD_NAME);
        verify(namingStrategy, times(1)).createConstantName(INT_FIELD_NAME);
        verify(valueProviderService, times(1)).getValueFor(argThat(arg -> arg.getSimpleName().toString().equals(BOOLEAN_FIELD_NAME)));
        verify(valueProviderService, times(1)).getValueFor(argThat(arg -> arg.getSimpleName().toString().equals(INT_FIELD_NAME)));
        verifyNoMoreInteractions(namingStrategy, valueProviderService);
    }


    @Test
    void generateConstants_whenCalledWithGeneric_shouldGenerateConstants() {
        when(namingStrategy.createConstantName(anyString())).thenAnswer(param -> "%sName".formatted(param.getArguments()[0]));
        final var field1 = TestFixtures.<FixtureConstant>createVariableElementFixture(BOOLEAN_FIELD_NAME, createTypeMirrorFixture(BOOLEAN_FIELD_DEFINITION.getType()), ElementKind.FIELD);

        final var field2 = TestFixtures.<FixtureConstant>createVariableElementFixture("genericField", mock(TypeMirror.class), FIELD);
        final var genericFieldType = createDeclaredTypeFixture("java.lang.String");
        when(genericFieldType.asElement().asType()).thenReturn(genericFieldType);
        final var genericMetadata = TestFixtures.createMetadataFixtureBuilder("Class", "<String>")
                .genericTypeMap(Map.of(field2.asType(), genericFieldType)).build();
        final var element = mock(TypeElement.class);
        when(element.getEnclosedElements()).thenReturn((List) List.of(field1, field2));

        final var result = strategy.generateConstants(element, genericMetadata);

        assertThat(result).containsAllEntriesOf(Map.of(
                BOOLEAN_FIELD_NAME, BOOLEAN_FIELD_DEFINITION,
                "genericField", Constant.builder().name("genericFieldName").originalFieldName("genericField").type("java.lang.String").value("value").build()));

        verify(field1, times(1)).getAnnotationsByType(FixtureConstant.class);
        verify(field2, times(1)).getAnnotationsByType(FixtureConstant.class);
        verify(namingStrategy, times(1)).createConstantName(BOOLEAN_FIELD_NAME);
        verify(namingStrategy, times(1)).createConstantName("genericField");
        verify(valueProviderService, times(1)).getValueFor(field1);
        verify(valueProviderService, times(1)).getValueFor(genericFieldType.asElement());
        verifyNoMoreInteractions(namingStrategy, valueProviderService);
    }

    @Test
    void generateConstants_whenCalledWithFixtureConstantAnnotation_shouldUseNameFromAnnotationAsKeyAndName() {
        final var annotation = createFixtureConstantFixture("CUSTOM_NAME", "true");
        final var annotation2 = createFixtureConstantFixture("CUSTOM_NAME_2", "");

        final var field1 = createVariableElementFixture(BOOLEAN_FIELD_NAME, createTypeMirrorFixture(BOOLEAN_FIELD_DEFINITION.getType()), ElementKind.FIELD, annotation);
        final var field2 = createVariableElementFixture("booleanField2", createTypeMirrorFixture("booleanField2Type"), ElementKind.FIELD, annotation2);

        final var element = mock(TypeElement.class);
        when(element.getEnclosedElements()).thenReturn((List) List.of(field1, field2));

        final var result = strategy.generateConstants(element, TestFixtures.createMetadataFixture());

        assertThat(result).containsAllEntriesOf(Map.of(
                "CUSTOM_NAME", createConstantFixture(BOOLEAN_FIELD_NAME, "CUSTOM_NAME", "true"),
                "CUSTOM_NAME_2", createConstantFixture("booleanField2", "CUSTOM_NAME_2")
        ));
        verify(field1, times(1)).getAnnotationsByType(FixtureConstant.class);
        verify(field2, times(1)).getAnnotationsByType(FixtureConstant.class);
        verify(valueProviderService, times(1)).getValueFor(argThat(arg -> arg.getSimpleName().toString().equals("booleanField2")));
        verifyNoMoreInteractions(valueProviderService);
        verifyNoInteractions(namingStrategy);
    }

    @Test
    void generateConstants_whenCalledWithMultipleFixtureConstantsAnnotations_shouldGenerateConstantPerAnnotation() {
        final var annotation = createFixtureConstantFixture("CUSTOM_NAME", "true");
        final var annotation2 = createFixtureConstantFixture("CUSTOM_NAME_2", "");

        final var field1 = createVariableElementFixture(BOOLEAN_FIELD_NAME, createTypeMirrorFixture(BOOLEAN_FIELD_DEFINITION.getType()), ElementKind.FIELD, annotation, annotation2);

        final var element = mock(TypeElement.class);
        when(element.getEnclosedElements()).thenReturn((List) List.of(field1));

        final var result = strategy.generateConstants(element, TestFixtures.createMetadataFixture());

        assertThat(result).containsAllEntriesOf(Map.of(
                "CUSTOM_NAME", createConstantFixture(BOOLEAN_FIELD_NAME, "CUSTOM_NAME", "true"),
                "CUSTOM_NAME_2", createConstantFixture(BOOLEAN_FIELD_NAME, "CUSTOM_NAME_2")
        ));
        verify(field1, times(1)).getAnnotationsByType(FixtureConstant.class);
        verify(valueProviderService, times(1)).getValueFor(argThat(arg -> arg.getSimpleName().toString().equals(BOOLEAN_FIELD_NAME)));
        verifyNoMoreInteractions(valueProviderService);
        verifyNoInteractions(namingStrategy);
    }
}