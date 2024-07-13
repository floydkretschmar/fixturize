package de.floydkretschmar.fixturize.stategies.constants;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DefaultConstantGenerationStrategyTest {

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
        final Collection<String> result = stategy.generateConstants(element);

        assertThat(result).containsAll(List.of(
                "\tpublic static boolean BOOLEAN_FIELD = false;",
                "\tpublic static int INT_FIELD = 0;",
                "\tpublic static java.lang.String STRING_FIELD = \"STRING_FIELD_VALUE\";",
                "\tpublic static java.util.UUID UUID_FIELD = UUID.randomUUID();"));
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
        final Collection<String> result = stategy.generateConstants(element);

        assertThat(result).containsAll(List.of(
                "\tpublic static boolean BOOLEAN_FIELD = false;",
                "\tpublic static int INT_FIELD = 0;",
                "\tpublic static java.lang.String STRING_FIELD = \"STRING_FIELD_VALUE\";",
                "\tpublic static java.util.UUID UUID_FIELD = EXTERNAL_VALUE;"));
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
        final Collection<String> result = stategy.generateConstants(element);

        assertThat(result).containsAll(List.of(
                "\tpublic static boolean BOOLEAN_FIELD = false;",
                "\tpublic static int INT_FIELD = 0;",
                "\tpublic static java.lang.String STRING_FIELD = \"STRING_FIELD_VALUE\";",
                "\tpublic static java.util.Date UNKNOWN_OBJECT = null;",
                "\tpublic static java.util.UUID UUID_FIELD = UUID.randomUUID();"));
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