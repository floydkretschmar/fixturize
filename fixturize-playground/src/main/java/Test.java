import de.floydkretschmar.fixturize.annotations.Fixture;
import de.floydkretschmar.fixturize.annotations.FixtureBuilder;
import de.floydkretschmar.fixturize.annotations.FixtureValueProvider;
import lombok.Builder;

@Builder
@Fixture(genericImplementations = {"java.lang.Boolean"})
@FixtureValueProvider(targetType = "java.lang.Boolean", valueProviderCallback = """
function(field, metadata) {
    return `true`;
}""")
@FixtureBuilder(methodName = "createTestFixture")
public class Test<T> {
    private int field;

    private T otherField;

    private Test2 testField;

    public Test(int field, T otherField, Test2 testField) {
        this.field = field;
        this.otherField = otherField;
        this.testField = testField;
    }
}
