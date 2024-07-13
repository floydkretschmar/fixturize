package de.floydkretschmar.fixturize.domain;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class FixtureCreationMethodDefinition {
    String returnType;
    String name;
    String returnValue;
}
