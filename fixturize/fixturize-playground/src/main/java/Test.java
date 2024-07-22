import de.floydkretschmar.fixturize.annotations.Fixture;
import lombok.Builder;

@Builder
@Fixture
public class Test<T> {
    private int field;

    private T field2;

    public Test(int field, T field2) {
        this.field = field;
        this.field2 = field2;
    }
}
