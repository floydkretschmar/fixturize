package de.floydkretschmar.fixturize.stategies.constants.value.map;

import de.floydkretschmar.fixturize.stategies.constants.value.provider.BooleanValueProvider;
import de.floydkretschmar.fixturize.stategies.constants.value.provider.ByteValueProvider;
import de.floydkretschmar.fixturize.stategies.constants.value.provider.CharacterValueProvider;
import de.floydkretschmar.fixturize.stategies.constants.value.provider.DoubleValueProvider;
import de.floydkretschmar.fixturize.stategies.constants.value.provider.FloatValueProvider;
import de.floydkretschmar.fixturize.stategies.constants.value.provider.IntegerValueProvider;
import de.floydkretschmar.fixturize.stategies.constants.value.provider.LongValueProvider;
import de.floydkretschmar.fixturize.stategies.constants.value.provider.ShortValueProvider;
import de.floydkretschmar.fixturize.stategies.constants.value.provider.ValueProvider;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import java.util.HashMap;
import java.util.Map;

import static javax.lang.model.type.TypeKind.ARRAY;
import static javax.lang.model.type.TypeKind.BOOLEAN;
import static javax.lang.model.type.TypeKind.BYTE;
import static javax.lang.model.type.TypeKind.CHAR;
import static javax.lang.model.type.TypeKind.DOUBLE;
import static javax.lang.model.type.TypeKind.FLOAT;
import static javax.lang.model.type.TypeKind.INT;
import static javax.lang.model.type.TypeKind.LONG;
import static javax.lang.model.type.TypeKind.SHORT;

public class TypeKindValueProviderMap extends HashMap<TypeKind, ValueProvider<VariableElement>> {
    public TypeKindValueProviderMap(Map<? extends TypeKind, ? extends ValueProvider<VariableElement>> map) {
        super(map);
        this.putIfAbsent(ARRAY, field -> "new %s {}".formatted(field.asType().toString()));
        this.putIfAbsent(BOOLEAN, new BooleanValueProvider());
        this.putIfAbsent(BYTE, new ByteValueProvider());
        this.putIfAbsent(CHAR, new CharacterValueProvider());
        this.putIfAbsent(DOUBLE, new DoubleValueProvider());
        this.putIfAbsent(FLOAT, new FloatValueProvider());
        this.putIfAbsent(INT, new IntegerValueProvider());
        this.putIfAbsent(LONG, new LongValueProvider());
        this.putIfAbsent(SHORT, new ShortValueProvider());
    }
}
