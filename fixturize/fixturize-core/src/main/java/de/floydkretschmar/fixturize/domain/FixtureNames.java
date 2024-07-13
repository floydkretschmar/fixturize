package de.floydkretschmar.fixturize.domain;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class FixtureNames {
    String qualifiedClassName;
    String simpleClassName;
    String packageName;
    String qualifiedFixtureClassName;

    public boolean hasPackageName() {
        return !this.getPackageName().isEmpty();
    }
}
