package de.floydkretschmar.fixturize;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;

class FixtureProcessorTest {
    @Test
    void process_whenCalled_generateFixtureClass() {
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
                         	public static java.util.UUID UUID_FIELD = java.util.UUID.randomUUID();
                         
                         	public TestObject createTestObjectFixtureWithStringFieldAndIntFieldAndBooleanFieldAndUuidField() {
                         		return new TestObject(STRING_FIELD,INT_FIELD,BOOLEAN_FIELD,UUID_FIELD);
                         	}
                         
                         	public TestObject createTestObjectFixtureWithStringFieldAndBooleanFieldAndUuidField() {
                         		return new TestObject(STRING_FIELD,BOOLEAN_FIELD,UUID_FIELD);
                         	}
                         }
                         """);
    }
}