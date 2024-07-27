package de.floydkretschmar.fixturize.playground.domain;

import de.floydkretschmar.fixturize.annotations.Fixture;
import de.floydkretschmar.fixturize.annotations.FixtureBuilder;
import de.floydkretschmar.fixturize.annotations.FixtureBuilderSetter;
import de.floydkretschmar.fixturize.annotations.FixtureConstant;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Fixture
@Value
@Builder
@FixtureBuilder(methodName = "createItem1", usedSetters = {
        @FixtureBuilderSetter(setterName = "itemId", value = "ITEM_ID_1"),
        @FixtureBuilderSetter(setterName = "name"),
        @FixtureBuilderSetter(setterName = "sku")
})
@FixtureBuilder(methodName = "createItem2", usedSetters = {
        @FixtureBuilderSetter(setterName = "itemId", value = "ITEM_ID_2"),
        @FixtureBuilderSetter(setterName = "name"),
        @FixtureBuilderSetter(setterName = "sku")
})
public class Item {
    @FixtureConstant(name = "ITEM_ID_1")
    @FixtureConstant(name = "ITEM_ID_2")
    UUID itemId;
    String name;
    String sku;
}
