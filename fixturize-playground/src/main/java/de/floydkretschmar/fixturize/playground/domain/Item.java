package de.floydkretschmar.fixturize.playground.domain;

import de.floydkretschmar.fixturize.annotations.Fixture;
import de.floydkretschmar.fixturize.annotations.FixtureBuilder;
import de.floydkretschmar.fixturize.annotations.FixtureConstant;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Fixture
@Value
@Builder
@FixtureBuilder(methodName = "createItem1", usedSetters = {"ITEM_ID_1", "name", "sku"})
@FixtureBuilder(methodName = "createItem2", usedSetters = {"ITEM_ID_2", "name", "sku"})
public class Item {
    @FixtureConstant(name = "ITEM_ID_1")
    @FixtureConstant(name = "ITEM_ID_2")
    UUID itemId;
    String name;
    String sku;
}
