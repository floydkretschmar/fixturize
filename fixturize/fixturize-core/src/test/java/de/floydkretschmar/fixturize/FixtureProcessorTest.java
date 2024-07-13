package de.floydkretschmar.fixturize;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;

class FixtureProcessorTest {
    private static final String RANDOM_UUID = "6b21f215-bf9e-445a-9dd2-5808a3a98d52";
    @Test
    void process_whenCalled_generateFixtureClass() {
        final var uuid = UUID.fromString(RANDOM_UUID);
        try (MockedStatic<UUID> uuidStatic = Mockito.mockStatic(UUID.class)) {
            uuidStatic.when(UUID::randomUUID).thenReturn(uuid);
            Compilation compilation = javac()
                    .withOptions("--add-opens=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED")
                    .withProcessors(new FixtureProcessor())
                    .compile(JavaFileObjects.forResource("TestObject.java"));
            assertThat(compilation).succeeded();
            assertThat(compilation)
                    .generatedSourceFile("de.floydkretschmar.fixturize.mocks.TestObjectFixture")
                    .contentsAsString(StandardCharsets.UTF_8).isEqualTo("""
                            package de.floydkretschmar.fixturize.mocks;
                            
                            public class TestObjectFixture {
                            	public static boolean BOOLEAN_FIELD = false;
                            	public static int INT_FIELD = 0;
                            	public static java.lang.String STRING_FIELD = "STRING_FIELD_VALUE";
                            	public static java.util.UUID UUID_FIELD = java.util.UUID.fromString("%s");
                            
                            	public TestObject createTestObjectFixtureWithStringFieldAndIntFieldAndBooleanFieldAndUuidField() {
                            		return new TestObject(STRING_FIELD,INT_FIELD,BOOLEAN_FIELD,UUID_FIELD);
                            	}
                            
                            	public TestObject createTestObjectFixtureWithStringFieldAndBooleanFieldAndUuidField() {
                            		return new TestObject(STRING_FIELD,BOOLEAN_FIELD,UUID_FIELD);
                            	}
                            }
                            """.formatted(RANDOM_UUID));
        }
    }
}