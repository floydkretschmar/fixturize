package de.floydkretschmar.fixturize;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.util.ElementFilter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ReflectionUtils {

    public static ExecutableElement findMethodWithModifiersByReturnType(Element declaredType, String returnTypeName, Modifier... modifiers) {
        return ElementFilter.methodsIn(declaredType.getEnclosedElements()).stream()
                .filter(method -> Arrays.stream(modifiers).allMatch(modifier -> method.getModifiers().contains(modifier))
                        && method.getReturnType().toString().equals(returnTypeName))
                .findFirst()
                .orElse(null);
    }

    public static <T> Stream<Map.Entry<T, Optional<ExecutableElement>>> findSetterForFields(Element declaredType, List<T> fields, Modifier... modifiers) {
        return fields.stream()
                .map(field -> Map.entry(field,
                        ElementFilter.methodsIn(declaredType.getEnclosedElements()).stream()
                                .filter(method -> Arrays.stream(modifiers).allMatch(modifier -> method.getModifiers().contains(modifier))
                                        && method.getSimpleName().toString().toLowerCase().endsWith(field.toString().toLowerCase()))
                                .findFirst()));
    }

    public static <T, K, U> Collector<T, ?, Map<K, U>> toLinkedMap(
            Function<? super T, ? extends K> keyMapper,
            Function<? super T, ? extends U> valueMapper) {
        return Collectors.toMap(
                keyMapper,
                valueMapper,
                (u, v) -> {
                    throw new IllegalStateException(String.format("Duplicate key %s", u));
                },
                LinkedHashMap::new
        );
    }
}
