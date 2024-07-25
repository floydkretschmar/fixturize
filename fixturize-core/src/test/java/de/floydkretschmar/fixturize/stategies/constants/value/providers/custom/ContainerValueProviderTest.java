package de.floydkretschmar.fixturize.stategies.constants.value.providers.custom;

import de.floydkretschmar.fixturize.stategies.constants.value.ValueProviderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.List;
import java.util.stream.Stream;

import static de.floydkretschmar.fixturize.TestFixtures.createMetadataFixture;
import static de.floydkretschmar.fixturize.TestFixtures.createVariableElementFixture;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContainerValueProviderTest {
    private ContainerValueProvider valueProvider;

    @Mock
    private Types typeUtils;

    @Mock
    private ValueProviderService service;

    @BeforeEach
    void setup() {

    }

    public static Stream<Arguments> getParametersForCollectionTypeTest() {
        return Stream.of(
                Arguments.of(
                        "java.util.List.of(%s)",
                        "java.util.Collection<java.lang.String>",
                        "java.util.List.of(java.lang.String.Value)",
                        List.of("java.lang.String")),
                Arguments.of(
                        "java.util.Map.of(%s)",
                        "java.util.Map<java.lang.String, java.lang.Integer>",
                        "java.util.Map.of(java.lang.String.Value, java.lang.Integer.Value)",
                        List.of("java.lang.String", "java.lang.Integer")),
                Arguments.of(
                        "java.util.List.of(%s)",
                        "java.util.List<java.lang.String>",
                        "java.util.List.of(java.lang.String.Value)",
                        List.of("java.lang.String")),
                Arguments.of(
                        "java.util.Set.of(%s)",
                        "java.util.Set<java.lang.String>",
                        "java.util.Set.of(java.lang.String.Value)",
                        List.of("java.lang.String")),
                Arguments.of(
                        "new java.util.PriorityQueue<>(java.util.List.of(%s))",
                        "java.util.Queue<java.lang.String>",
                        "new java.util.PriorityQueue<>(java.util.List.of(java.lang.String.Value))",
                        List.of("java.lang.String"))
        );
    }


    @ParameterizedTest
    @MethodSource("getParametersForCollectionTypeTest")
    void provideValueAsString_whenCalled_returnContainerValue(String wildcardValue, String qualifiedTypeName, String expectedResult, List<String> genericTypes) {
        valueProvider = new ContainerValueProvider(service, typeUtils, wildcardValue);
        when(service.getValueFor(any())).thenAnswer(params -> {
            final var element = (Element) params.getArgument(0);
            return element.getSimpleName().toString() + ".Value";
        });
        when(typeUtils.asElement(any())).thenAnswer(params -> {
            final var type = (TypeMirror) params.getArgument(0);
            return createTypeElement(type.toString());
        });

        final var fieldType = mock(DeclaredType.class);
        final var field = createVariableElementFixture(
                "field",
                fieldType);
        final var genericArguments = genericTypes.stream().map(
                ContainerValueProviderTest::createDeclaredType).toList();
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