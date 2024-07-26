package de.floydkretschmar.fixturize.playground.application;

import de.floydkretschmar.fixturize.playground.domain.Order;
import de.floydkretschmar.fixturize.playground.domain.Parcel;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class OrderService {
    public Parcel ship(Order order, List<UUID> itemIds, String trackingNumber, Instant shipmentDate) {
        order.setShipmentDate(shipmentDate);

        final var shippedItems = order.getItems().stream().filter(item -> itemIds.contains(item.getItemId())).toList();

        return Parcel.builder()
                .itemsInParcel(shippedItems)
                .orderNumber(order.getOrderNumber())
                .trackingNumber(trackingNumber)
                .shipmentDate(shipmentDate)
                .build();
    }
}
