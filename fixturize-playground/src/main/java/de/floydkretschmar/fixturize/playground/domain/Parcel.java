package de.floydkretschmar.fixturize.playground.domain;

import de.floydkretschmar.fixturize.annotations.*;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Value
@Builder
@Fixture
@FixtureBuilder(methodName = "createParcel", usedSetters = {
        @FixtureBuilderSetter(setterName = "shipmentDate"),
        @FixtureBuilderSetter(setterName = "trackingNumber"),
        @FixtureBuilderSetter(setterName = "orderNumber", value = "ORDER_NUMBER"),
        @FixtureBuilderSetter(setterName = "itemsInParcel"),
})
@FixtureValueProvider(targetType = "java.util.List<de.floydkretschmar.fixturize.playground.domain.Item>", valueProviderCallback = "(field, metadata) => `java.util.List.of(ItemFixture.createItem1().build(), ItemFixture.createItem2().build())`")
public class Parcel {
    Instant shipmentDate;
    String trackingNumber;
    @FixtureConstant(name = "ORDER_NUMBER", value = "OrderFixture.ORDER_NUMBER")
    UUID orderNumber;
    List<Item> itemsInParcel;
}
