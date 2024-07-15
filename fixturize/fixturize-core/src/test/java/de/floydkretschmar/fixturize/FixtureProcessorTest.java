package de.floydkretschmar.fixturize;

import com.google.common.io.Resources;
import com.google.testing.compile.JavaFileObjects;
import lombok.SneakyThrows;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import javax.annotation.processing.Processor;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
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
                        "classes/BuilderClass.java",
                        "de.floydkretschmar.fixturize.mocks.BuilderClassFixture",
                        "fixtures/BuilderClassFixture.java"),
                Arguments.of(
                        "classes/LombokClass.java",
                        "de.floydkretschmar.fixturize.mocks.LombokClassFixture",
                        "fixtures/LombokClassFixture.java")
        );
    }

    @SneakyThrows
    @ParameterizedTest(name = "{0}")
    @MethodSource("process_getParameters")
    void process_whenCalled_generateFixtureClass(String classPath, String expectedFixtureClassName, String expectedFixtureClassPath) {
        final var uuid = UUID.fromString(RANDOM_UUID);
        final var url = Resources.getResource(expectedFixtureClassPath);
        final var expectedFixture = Resources.toString(url, StandardCharsets.UTF_8);

        Class<?> lombokAnnotationProcessor = getClass().getClassLoader().loadClass("lombok.launch.AnnotationProcessorHider$AnnotationProcessor");
        Class<?> lombokClaimingProcessor = getClass().getClassLoader().loadClass("lombok.launch.AnnotationProcessorHider$ClaimingProcessor");

        try (final var uuidStatic = Mockito.mockStatic(UUID.class)) {
            uuidStatic.when(UUID::randomUUID).thenReturn(uuid);
            final var compilation = javac()
                    .withProcessors(
                            new FixtureProcessor(),
                            (Processor)lombokAnnotationProcessor.getDeclaredConstructor().newInstance(),
                            (Processor)lombokClaimingProcessor.getDeclaredConstructor().newInstance())
                    .compile(JavaFileObjects.forResource(classPath));
            assertThat(compilation).succeeded();
            assertThat(compilation)
                    .generatedSourceFile(expectedFixtureClassName)
                    .contentsAsString(StandardCharsets.UTF_8).isEqualTo(expectedFixture);
        }
    }
}