package de.floydkretschmar.fixturize.stategies.constants.value.providers.fallback;

import de.floydkretschmar.fixturize.TestFixtures;
import de.floydkretschmar.fixturize.stategies.constants.value.ValueProviderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.List;
import java.util.stream.Stream;

import static de.floydkretschmar.fixturize.TestFixtures.createMetadataFixture;
import static de.floydkretschmar.fixturize.TestFixtures.createTypeMirrorFixture;
import static de.floydkretschmar.fixturize.TestFixtures.createVariableElementFixture;
import static javax.lang.model.type.TypeKind.ARRAY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContainerValueProviderTest {
    private ContainerValueProvider valueProvider;

    @Mock
    private VariableElement field;

    @Mock
    private Elements elementUtils;

    @Mock
    private Types typeUtils;

    @Mock
    private ValueProviderService service;

    @BeforeEach
    void setup() {
        valueProvider = new ContainerValueProvider(elementUtils, typeUtils, service);
    }

    @Test
    void provideValueAsString_whenCalledForArray_returnContainerValue() {
        final var type = createTypeMirrorFixture(ARRAY);
        final var metadata = TestFixtures.createMetadataFixture("Class[]");
        when(field.asType()).thenReturn(type);

        final var result = valueProvider.provideValueAsString(field, metadata);

        assertThat(result).isEqualTo("new some.test.Class[] {}");
    }

    public static Stream<Arguments> getParametersForCollectionTypeTest() {
        return Stream.of(
                Arguments.of(
                        "java.util.Collection<java.lang.String>",
                        "java.util.List.of(java.lang.String.Value)",
                        List.of("java.lang.String")),
                Arguments.of(
                        "java.util.Map<java.lang.String, java.lang.Integer>",
                        "java.util.Map.of(java.lang.String.Value, java.lang.Integer.Value)",
                        List.of("java.lang.String", "java.lang.Integer")),
                Arguments.of(
                        "java.util.List<java.lang.String>",
                        "java.util.List.of(java.lang.String.Value)",
                        List.of("java.lang.String")),
                Arguments.of(
                        "java.util.Set<java.lang.String>",
                        "java.util.Set.of(java.lang.String.Value)",
                        List.of("java.lang.String")),
                Arguments.of(
                        "java.util.Queue<java.lang.String>",
                        "new java.util.PriorityQueue<>(java.util.List.of(java.lang.String.Value))",
                        List.of("java.lang.String"))
        );
    }

    @ParameterizedTest
    @MethodSource("getParametersForCollectionTypeTest")
    void provideValueAsString_whenCalledForCollectionType_returnContainerValue(String qualifiedTypeName, String expectedResult, List<String> genericTypes) {
        when(typeUtils.isSameType(any(), any())).thenAnswer(params -> {
            final var fieldType = (DeclaredType) params.getArgument(0);
            final var mapType = (DeclaredType) params.getArgument(1);

            return fieldType.toString().equals(mapType.toString());
        });
        when(typeUtils.asElement(any())).thenAnswer(params -> {
            final var type = (TypeMirror) params.getArgument(0);
            return createTypeElement(type.toString());
        });
        when(typeUtils.erasure(any())).thenAnswer(params -> {
            final var type = (TypeMirror) params.getArgument(0);
            return createDeclaredType(type.toString().replaceAll("<.*>", ""));
        });
        when(elementUtils.getTypeElement(any())).thenAnswer(params -> {
            final var typeName = (String) params.getArgument(0);
            final var element = mock(TypeElement.class);
            final var type = createTypeMirrorFixture(typeName);
            when(element.asType()).thenReturn(type);
            return element;
        });
        when(service.getValueFor(any())).thenAnswer(params -> {
            final var element = (Element) params.getArgument(0);
            return element.getSimpleName().toString() + ".Value";
        });

        final var fieldType = createDeclaredType(qualifiedTypeName);
        final var field = createVariableElementFixture(
                "field",
                fieldType);
        final var genericArguments = genericTypes.stream().map(ContainerValueProviderTest::createDeclaredType).toList();
        when(fieldType.getTypeArguments()).thenReturn((List) genericArguments);

        final var result = valueProvider.provideValueAsString(field, createMetadataFixture(qualifiedTypeName));
        assertThat(result).isEqualTo(expectedResult);
    }

    private static DeclaredType createDeclaredType(String name) {
        final var type = mock(DeclaredType.class);
        when(type.toString()).thenReturn(name);
        return type;
    }


    private static TypeElement createTypeElement(String name) {
        final var typeElement = mock(TypeElement.class);
        final var typeName = mock(Name.class);

        when(typeName.toString()).thenReturn(name);
        when(typeElement.getSimpleName()).thenReturn(typeName);

        return typeElement;
    }
}