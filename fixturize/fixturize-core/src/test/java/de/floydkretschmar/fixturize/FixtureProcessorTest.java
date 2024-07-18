package de.floydkretschmar.fixturize;

import com.google.common.io.Resources;
import com.google.testing.compile.JavaFileObjects;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import javax.annotation.processing.Processor;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;
import static de.floydkretschmar.fixturize.TestConstants.RANDOM_UUID;

class FixtureProcessorTest {
    public static Stream<Arguments> process_getParameters() {
        return Stream.of(
                Arguments.of(
                        "classes/SingleConstructorClass.java",
                        "de.floydkretschmar.fixturize.mocks.SingleConstructorClassFixture",
                        "fixtures/SingleConstructorClassFixture.java"),
                Arguments.of(
                        "classes/MultiConstructorClass.java",
                        "de.floydkretschmar.fixturize.mocks.MultiConstructorClassFixture",
                        "fixtures/MultiConstructorClassFixture.java"),
                Arguments.of(
                        "classes/CustomConstantDefinitionsClass.java",
                        "de.floydkretschmar.fixturize.mocks.CustomConstantDefinitionsClassFixture",
                        "fixtures/CustomConstantDefinitionsClassFixture.java"),
                Arguments.of(
                        "classes/LombokClass.java",
                        "de.floydkretschmar.fixturize.mocks.LombokClassFixture",
                        "fixtures/LombokClassFixture.java"),
                Arguments.of(
                        "classes/SupportedTypesClass.java",
                        "de.floydkretschmar.fixturize.mocks.SupportedTypesClassFixture",
                        "fixtures/SupportedTypesClassFixture.java"),
                Arguments.of(
                        "classes/CustomValueProviderClass.java",
                        "de.floydkretschmar.fixturize.mocks.CustomValueProviderClassFixture",
                        "fixtures/CustomValueProviderClassFixture.java")
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("process_getParameters")
    void process_whenCalled_generateFixtureClass(String classPath, String expectedFixtureClassName, String expectedFixtureClassPath) {
        final var expectedFixture = loadExpectedFixture(expectedFixtureClassPath);

        final var uuid = UUID.fromString(RANDOM_UUID);
        try (final var uuidStatic = Mockito.mockStatic(UUID.class)) {
            uuidStatic.when(UUID::randomUUID).thenReturn(uuid);
            assertCompiledClasses(List.of(classPath), Map.of(expectedFixtureClassName, expectedFixture));
        }
    }

    @Test
    void process_whenFixtureIsCrossReferencing_generateFixtureClass() {
        final var expectedFixture = loadExpectedFixture("fixtures/cross-referencing/CrossReferencingClassFixture.java");

        final var uuid = UUID.fromString(RANDOM_UUID);
        try (final var uuidStatic = Mockito.mockStatic(UUID.class)) {
            uuidStatic.when(UUID::randomUUID).thenReturn(uuid);
            assertCompiledClasses(
                    List.of(
                            "classes/cross-referencing/CrossReferencingClass.java",
                            "classes/cross-referencing/CrossReferencedClass.java",
                            "classes/SingleConstructorClass.java",
                            "classes/cross-referencing/CrossReferencedBuilderClass.java",
                            "classes/cross-referencing/CrossReferencedLombokClass.java",
                            "classes/cross-referencing/CrossReferencedConstructorClass.java"),
                    Map.of(
                            "de.floydkretschmar.fixturize.mocks.CrossReferencingClassFixture", expectedFixture
                    ));
        }
    }

    @SneakyThrows
    private static String loadExpectedFixture(String expectedFixtureClassPath) {
        final var url = Resources.getResource(expectedFixtureClassPath);
        return Resources.toString(url, StandardCharsets.UTF_8);
    }

    @SneakyThrows
    private void assertCompiledClasses(List<String> classPaths, Map<String, String> expectedFixture) {
        final var lombokAnnotationProcessor = getClass().getClassLoader().loadClass("lombok.launch.AnnotationProcessorHider$AnnotationProcessor");
        final var lombokClaimingProcessor = getClass().getClassLoader().loadClass("lombok.launch.AnnotationProcessorHider$ClaimingProcessor");

        final var compilation = javac()
                .withProcessors(
                        new FixtureProcessor(),
                        (Processor) lombokAnnotationProcessor.getDeclaredConstructor().newInstance(),
                        (Processor) lombokClaimingProcessor.getDeclaredConstructor().newInstance())
                .compile(classPaths.stream().map(JavaFileObjects::forResource).collect(Collectors.toSet()));
        assertThat(compilation).succeeded();

        expectedFixture.forEach((expectedFixtureName, expectedFixtureValue) -> {
            assertThat(compilation)
                    .generatedSourceFile(expectedFixtureName)
                    .contentsAsString(StandardCharsets.UTF_8).isEqualTo(expectedFixtureValue);
        });
    }
}