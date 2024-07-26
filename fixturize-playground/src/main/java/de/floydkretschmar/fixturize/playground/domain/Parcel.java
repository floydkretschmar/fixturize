package de.floydkretschmar.fixturize.playground.domain;

import de.floydkretschmar.fixturize.annotations.Fixture;
import de.floydkretschmar.fixturize.annotations.FixtureBuilder;
import de.floydkretschmar.fixturize.annotations.FixtureConstant;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Value
@Builder
@Fixture
@FixtureBuilder(methodName = "createParcel")
public class Parcel {
    Instant shipmentDate;
    String trackingNumber;
    @FixtureConstant(name = "ORDER_NUMBER", value = "OrderFixture.ORDER_NUMBER")
    UUID orderNumber;
    List<Item> itemsInParcel;
}
