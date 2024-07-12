import mocks.TestObject;
import org.junit.jupiter.api.Test;
import stategies.constants.CamelCaseToScreamingSnakeCaseNamingStrategy;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class FixtureCreatorTest {
    @Test
    void createFixtureForClass_whenCalledForFixtureWithFixtureConstructor_shouldReturnFixtureClassAsString() {
        final FixtureCreator<TestObject> fixtureCreator = new FixtureCreator<>(Map.of(), List.of(), new CamelCaseToScreamingSnakeCaseNamingStrategy());

        var result = fixtureCreator.createFixtureForClass(TestObject.class);
        assertThat(result).isEqualTo("""
                public class TestObjectFixture {
                \tpublic static boolean BOOLEAN_FIELD = false;
                \tpublic static int INT_FIELD = 0;
                \tpublic static String STRING_FIELD = "STRING_FIELD_VALUE";
                \tpublic static UUID UUID_FIELD = UUID.randomUUID();
                
                \tpublic TestObject createTestObjectFixtureWithStringFieldAndIntFieldAndBooleanFieldAndUuidField() {
                \t\treturn new TestObject(STRING_FIELD,INT_FIELD,BOOLEAN_FIELD,UUID_FIELD);
                \t}
                }
                """);

    }
}