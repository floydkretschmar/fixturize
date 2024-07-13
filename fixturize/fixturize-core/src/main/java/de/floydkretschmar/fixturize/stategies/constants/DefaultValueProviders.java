package de.floydkretschmar.fixturize.stategies.constants;

import com.google.common.base.CaseFormat;
import com.google.common.base.Function;

import javax.lang.model.element.VariableElement;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DefaultValueProviders extends HashMap<String, Function<VariableElement, String>> {
    public DefaultValueProviders(Map<? extends String, ? extends Function<VariableElement, String>> m) {
        super(m);
        this.putIfAbsent(String.class.getName(), field -> "\"%s_VALUE\"".formatted(CaseFormat.LOWER_CAMEL.to(
                CaseFormat.UPPER_UNDERSCORE, field.getSimpleName().toString())));
        this.putIfAbsent(boolean.class.getName(), field -> "false");
        this.putIfAbsent(char.class.getName(), field -> "'\u0000'");
        this.putIfAbsent(byte.class.getName(), field -> "0");
        this.putIfAbsent(int.class.getName(), field -> "0");
        this.putIfAbsent(short.class.getName(), field -> "Short.valueOf((short)0)");
        this.putIfAbsent(long.class.getName(), field -> "0L");
        this.putIfAbsent(float.class.getName(), field -> "0.0F");
        this.putIfAbsent(double.class.getName(), field -> "0.0");

        this.putIfAbsent(UUID.class.getName(), field -> "java.util.UUID.randomUUID()");
    }
}
