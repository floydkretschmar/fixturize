package de.floydkretschmar.fixturize.domain;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class FixtureConstantDefinition {
    String type;
    String name;
    String value;
}
