package de.floydkretschmar.fixturize.domain;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class FixtureConstant {
    String type;
    String name;
    Object value;
}
