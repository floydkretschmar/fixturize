package de.floydkretschmar.fixturize;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.stream.Stream;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;

class FixtureProcessorTest {
    private static final String RANDOM_UUID = "6b21f215-bf9e-445a-9dd2-5808a3a98d52";

    public static Stream<Arguments> process_getParameters() {
        return Stream.of(
                Arguments.of(
                        "SingleConstructorClass.java",
                        "de.floydkretschmar.fixturize.mocks.SingleConstructorClassFixture",
                        """
                            package de.floydkretschmar.fixturize.mocks;
                            
                            public class SingleConstructorClassFixture {
                            	public static boolean BOOLEAN_FIELD = false;
                            	public static int INT_FIELD = 0;
                            	public static java.lang.String STRING_FIELD = "STRING_FIELD_VALUE";
                            	public static java.util.UUID UUID_FIELD = java.util.UUID.fromString("%s");
                            
                            	public SingleConstructorClass createSingleConstructorClassFixtureWithStringFieldAndIntFieldAndBooleanFieldAndUuidField() {
                            		return new SingleConstructorClass(STRING_FIELD,INT_FIELD,BOOLEAN_FIELD,UUID_FIELD);
                            	}
                            }
                            """.formatted(RANDOM_UUID)),
                Arguments.of(
                        "MultiConstructorClass.java",
                        "de.floydkretschmar.fixturize.mocks.MultiConstructorClassFixture",
                        """
                            package de.floydkretschmar.fixturize.mocks;
                            
                            public class MultiConstructorClassFixture {
                            	public static boolean BOOLEAN_FIELD = false;
                            	public static int INT_FIELD = 0;
                            	public static java.lang.String STRING_FIELD = "STRING_FIELD_VALUE";
                            	public static java.util.UUID UUID_FIELD = java.util.UUID.fromString("%s");
                            
                            	public MultiConstructorClass createMultiConstructorClassFixtureWithStringFieldAndIntFieldAndBooleanFieldAndUuidField() {
                            		return new MultiConstructorClass(STRING_FIELD,INT_FIELD,BOOLEAN_FIELD,UUID_FIELD);
                            	}
                            
                            	public MultiConstructorClass createMultiConstructorClassFixtureWithStringFieldAndBooleanFieldAndUuidField() {
                            		return new MultiConstructorClass(STRING_FIELD,BOOLEAN_FIELD,UUID_FIELD);
                            	}
                            }
                            """.formatted(RANDOM_UUID)),
                Arguments.of(
                        "CustomConstantDefinitionsClass.java",
                        "de.floydkretschmar.fixturize.mocks.CustomConstantDefinitionsClassFixture",
                        """
                            package de.floydkretschmar.fixturize.mocks;
                            
                            public class CustomConstantDefinitionsClassFixture {
                            	public static boolean CUSTOM_BOOLEAN_FIELD_NAME = false;
                            	public static java.lang.String CUSTOM_STRING_FIELD_NAME = "CUSTOM_CONSTANT_VALUE";
                            	public static int INT_FIELD = 0;
                            	public static java.util.UUID UUID_FIELD = java.util.UUID.fromString("%s");
                            
                            	public CustomConstantDefinitionsClass createCustomConstantDefinitionsClassFixtureWithStringFieldAndIntFieldAndBooleanFieldAndUuidField() {
                            		return new CustomConstantDefinitionsClass(CUSTOM_STRING_FIELD_NAME,INT_FIELD,CUSTOM_BOOLEAN_FIELD_NAME,UUID_FIELD);
                            	}
                            
                            	public CustomConstantDefinitionsClass createCustomConstantDefinitionsClassFixtureWithStringFieldAndBooleanFieldAndUuidField() {
                            		return new CustomConstantDefinitionsClass(CUSTOM_STRING_FIELD_NAME,CUSTOM_BOOLEAN_FIELD_NAME,UUID_FIELD);
                            	}
                            }
                            """.formatted(RANDOM_UUID))
        );
    }


    @ParameterizedTest(name = "{0}")
    @MethodSource("process_getParameters")
    void process_whenCalled_generateFixtureClass(String className, String expectedFixtureClassName, String expectedFixtureClass) {
        final var uuid = UUID.fromString(RANDOM_UUID);
        try (MockedStatic<UUID> uuidStatic = Mockito.mockStatic(UUID.class)) {
            uuidStatic.when(UUID::randomUUID).thenReturn(uuid);
            Compilation compilation = javac()
                    .withProcessors(new FixtureProcessor())
                    .compile(JavaFileObjects.forResource(className));
            assertThat(compilation).succeeded();
            assertThat(compilation)
                    .generatedSourceFile(expectedFixtureClassName)
                    .contentsAsString(StandardCharsets.UTF_8).isEqualTo(expectedFixtureClass);
        }
    }
}