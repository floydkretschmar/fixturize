import de.floydkretschmar.fixturize.annotations.Fixture;
import lombok.Builder;

@Builder
@Fixture(genericImplementations = {"java.lang.Boolean"})
public class Test<T> {
    private int field;

    private T otherField;

    public Test(int field, T otherField) {
        this.field = field;
        this.otherField = otherField;
    }
}
