package de.floydkretschmar.fixturize.domain;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class FixtureCreationMethod {
    String returnType;
    String methodName;
    String creationCallString;
}
