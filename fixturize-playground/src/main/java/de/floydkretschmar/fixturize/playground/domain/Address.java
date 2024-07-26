package de.floydkretschmar.fixturize.playground.domain;

import de.floydkretschmar.fixturize.annotations.Fixture;
import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class Address {
    String city;
    String zipCode;
    String street;
}
