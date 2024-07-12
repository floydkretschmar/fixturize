package domain;

import lombok.Builder;
import lombok.Getter;

import java.lang.reflect.Field;

@Getter
@Builder
public class FixtureConstant {
    private Field field;
    private String name;
    private Object value;

    @Override
    public String toString() {
        return "\tpublic static %s %s = %s;".formatted(field.getType().getSimpleName(), name, value);
    }
}
