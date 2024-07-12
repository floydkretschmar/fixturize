package stategies.constants;

import domain.FixtureConstant;
import mocks.TestObject;
import mocks.TestObjectWithUnknownObject;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultConstantGenerationStrategyTest {

    @Test
    void generateConstants_whenCalledWithValidClass_shouldGeneratedConstants() {
        var stategy = new DefaultConstantGenerationStrategy(new CamelCaseToScreamingSnakeCaseNamingStrategy(), Map.of());

        final Collection<String> result = stategy.generateConstants(TestObject.class);

        assertThat(result).containsAll(List.of(
                "\tpublic static boolean BOOLEAN_FIELD = false;",
                "\tpublic static int INT_FIELD = 0;",
                "\tpublic static String STRING_FIELD = \"STRING_FIELD_VALUE\";",
                "\tpublic static UUID UUID_FIELD = UUID.randomUUID();"));
    }

    @Test
    void generateConstants_whenDefaultValueProviderGetsOverwritten_shouldGeneratedConstantsUsingExternalValueProvider() {
        var stategy = new DefaultConstantGenerationStrategy(new CamelCaseToScreamingSnakeCaseNamingStrategy(),
                Map.of(UUID.class, field -> "EXTERNAL_VALUE"));

        final Collection<String> result = stategy.generateConstants(TestObject.class);

        assertThat(result).containsAll(List.of(
                "\tpublic static boolean BOOLEAN_FIELD = false;",
                "\tpublic static int INT_FIELD = 0;",
                "\tpublic static String STRING_FIELD = \"STRING_FIELD_VALUE\";",
                "\tpublic static UUID UUID_FIELD = EXTERNAL_VALUE;"));
    }

    @Test
    void generateConstants_whenCalledWithAttributeWithUnknownValue_shouldSetValueToNull() {
        var stategy = new DefaultConstantGenerationStrategy(new CamelCaseToScreamingSnakeCaseNamingStrategy(),
                Map.of());

        final Collection<String> result = stategy.generateConstants(TestObjectWithUnknownObject.class);

        assertThat(result).containsAll(List.of(
                "\tpublic static boolean BOOLEAN_FIELD = false;",
                "\tpublic static int INT_FIELD = 0;",
                "\tpublic static String STRING_FIELD = \"STRING_FIELD_VALUE\";",
                "\tpublic static BuilderTestObject UNKNOWN_OBJECT = null;",
                "\tpublic static UUID UUID_FIELD = UUID.randomUUID();"));
    }
}