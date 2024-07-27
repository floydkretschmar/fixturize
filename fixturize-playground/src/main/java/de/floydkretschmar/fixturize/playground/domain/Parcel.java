package de.floydkretschmar.fixturize.playground.domain;

import de.floydkretschmar.fixturize.annotations.Fixture;
import de.floydkretschmar.fixturize.annotations.FixtureBuilder;
import de.floydkretschmar.fixturize.annotations.FixtureConstant;
import de.floydkretschmar.fixturize.annotations.FixtureValueProvider;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
@Fixture
@FixtureBuilder(methodName = "createParcelBuilder", asBuilder = true)
@FixtureBuilder(methodName = "createParcel")
@FixtureValueProvider(targetType = "java.util.List<de.floydkretschmar.fixturize.playground.domain.Item>", valueProviderCallback = "(field, metadata) => `java.util.List.of(ItemFixture.createItem1(), ItemFixture.createItem2())`")
public class Parcel {
    Instant shipmentDate;
    String trackingNumber;
    @FixtureConstant(name = "ORDER_NUMBER", value = "OrderFixture.ORDER_NUMBER")
    UUID orderNumber;
    List<Item> itemsInParcel;
}
