package de.floydkretschmar.fixturize.stategies.constants.value;

import javax.lang.model.type.TypeKind;
import java.util.HashMap;
import java.util.Map;

import static javax.lang.model.type.TypeKind.BOOLEAN;
import static javax.lang.model.type.TypeKind.BYTE;
import static javax.lang.model.type.TypeKind.CHAR;
import static javax.lang.model.type.TypeKind.DOUBLE;
import static javax.lang.model.type.TypeKind.FLOAT;
import static javax.lang.model.type.TypeKind.INT;
import static javax.lang.model.type.TypeKind.LONG;
import static javax.lang.model.type.TypeKind.SHORT;

public class TypeKindValueProviderMap extends HashMap<TypeKind, ValueProvider> {
    public TypeKindValueProviderMap(Map<? extends TypeKind, ? extends ValueProvider> map) {
        super(map);
        this.putIfAbsent(BOOLEAN, field -> "false");
        this.putIfAbsent(CHAR, field -> "\u0000");
        this.putIfAbsent(BYTE, field -> "0");
        this.putIfAbsent(INT, field -> "0");
        this.putIfAbsent(SHORT, field -> "Short.valueOf((short)0)");
        this.putIfAbsent(LONG, field -> "0L");
        this.putIfAbsent(FLOAT, field -> "0.0F");
        this.putIfAbsent(DOUBLE, field -> "0.0");
    }
}
