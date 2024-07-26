package de.floydkretschmar.fixturize.playground.application;

import de.floydkretschmar.fixturize.playground.domain.ItemFixture;
import de.floydkretschmar.fixturize.playground.domain.OrderFixture;
import de.floydkretschmar.fixturize.playground.domain.ParcelFixture;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class OrderServiceTest {

    @Test
    void ship_WhenCalled_ShouldReturnParcel() {
        final var order = OrderFixture.createOrder().build();
        final var orderService = new OrderService();
        final var shipmentDate = Instant.now();

        var parcel = orderService.ship(order, List.of(ItemFixture.ITEM_ID_1, ItemFixture.ITEM_ID_2), ParcelFixture.TRACKING_NUMBER, shipmentDate);

        assertThat(parcel).isEqualTo(ParcelFixture.createParcel().shipmentDate(shipmentDate).build());
    }
}