package de.floydkretschmar.fixturize.playground.domain;

import de.floydkretschmar.fixturize.annotations.Fixture;
import de.floydkretschmar.fixturize.annotations.FixtureBuilder;
import de.floydkretschmar.fixturize.annotations.FixtureConstant;
import de.floydkretschmar.fixturize.annotations.FixtureValueProvider;
import lombok.Builder;
import lombok.Data;
import lombok.Value;

import javax.money.MonetaryAmount;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Builder
@Data
@Fixture
@FixtureValueProvider(targetType = "javax.money.MonetaryAmount", valueProviderCallback = "(field, metadata) => `org.javamoney.moneta.Money.of(java.math.BigDecimal.ZERO, \"EUR\")`")
@FixtureValueProvider(targetType = "java.util.List<de.floydkretschmar.fixturize.playground.domain.Item>", valueProviderCallback = "(field, metadata) => `java.util.List.of(ItemFixture.createItem1().build(), ItemFixture.createItem2().build())`")
@FixtureBuilder(methodName = "createOrder")
public class Order {
    UUID orderNumber;
    Instant creationDate;
    @FixtureConstant(name = "SHIPMENT_DATE", value = "null")
    Instant shipmentDate;
    List<Item> items;
    Customer customer;
    MonetaryAmount total;
    List<Parcel> parcels;
}
