package de.floydkretschmar.fixturize.playground.domain;

import de.floydkretschmar.fixturize.annotations.Fixture;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Customer {
    String lastName;
    String firstName;
    Address billingAddress;
    Address shipmentAddress;
}
