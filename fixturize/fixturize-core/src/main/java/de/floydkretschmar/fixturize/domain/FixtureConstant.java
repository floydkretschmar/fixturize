package de.floydkretschmar.fixturize.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FixtureConstant {
    private String type;
    private String name;
    private Object value;

    @Override
    public String toString() {
        return "\tpublic static %s %s = %s;".formatted(type, name, value);
    }
}
