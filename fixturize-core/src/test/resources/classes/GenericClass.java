package de.floydkretschmar.fixturize.mocks;

import de.floydkretschmar.fixturize.annotations.Fixture;
import de.floydkretschmar.fixturize.annotations.FixtureBuilder;
import de.floydkretschmar.fixturize.annotations.FixtureBuilderSetter;
import de.floydkretschmar.fixturize.annotations.FixtureConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@AllArgsConstructor
@Builder
@Value
@Fixture(genericImplementations = {"java.lang.String"})
@FixtureConstructor(methodName = "createGenericFixtureConstructor", constructorParameters = {"genericField", "field"})
@FixtureBuilder(methodName = "createGenericFixtureBuilder", usedSetters = {
        @FixtureBuilderSetter(setterName = "genericField"), @FixtureBuilderSetter(setterName = "field")
})
public class GenericClass<T> {
    T genericField;
    int field;
}
