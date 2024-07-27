package de.floydkretschmar.fixturize;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Defines utility methods that are used to work with {@link Element} instances.
 */
public class ElementUtils {

    /**
     * Returns the first method on a {@link Element} whose return type matches the specified return type name and which have
     * the specified modifiers. Returns null if no matching method could be found.
     *
     * @param element        - from which the methods will be returned
     * @param returnTypeName - of the method
     * @param modifiers      - of the method
     * @return the matching method
     */
    public static ExecutableElement findMethodWithModifiersByReturnType(Element element, String returnTypeName, String methodName, Modifier... modifiers) {
        return ElementFilter.methodsIn(element.getEnclosedElements()).stream()
                .filter(method -> Arrays.stream(modifiers).allMatch(modifier -> method.getModifiers().contains(modifier))
                        && method.getReturnType().toString().equals(returnTypeName)
                        && method.getSimpleName().toString().equals(methodName))
                .findFirst()
                .orElse(null);
    }


    /**
     * Returns all methods on the provided element that can be setter for the provided list of fields. A method is considered a match if
     * <ul>
     *     <li>it contains the specified modifiers</li>
     *     <li>its name matches the pattern "[set]*{fieldName}" case insensitvely</li>
     *     <li>it has exactly one parameter</li>
     *     <li>the return type of the method matches the specified return type</li>
     * </ul>
     *
     * @param element    - from which the methods will be returned
     * @param fields     - for which corresponding setters should be retrieved
     * @param returnType - of the setters
     * @param modifiers  - of the setters
     * @param <T>        the type of the fields whose toString methods will be used to determine {fieldName}
     * @return a map of field to setter pairs
     */
    public static <T> Stream<Map.Entry<T, Optional<ExecutableElement>>> findSetterForFields(Element element, List<T> fields, TypeMirror returnType, Modifier... modifiers) {
        return fields.stream()
                .map(field -> Map.entry(field,
                        ElementFilter.methodsIn(element.getEnclosedElements()).stream()
                                .filter(method -> Arrays.stream(modifiers).allMatch(modifier -> method.getModifiers().contains(modifier))
                                        && method.getSimpleName().toString().toLowerCase().matches("[set]*" + field.toString().toLowerCase())
                                        && method.getReturnType().equals(returnType)
                                        && method.getParameters().size() == 1)
                                .findFirst()));
    }

    /**
     * Returns a collector for a {@link LinkedHashMap} for the specified key and value mappers.
     * @param keyMapper - for collecting keys
     * @param valueMapper - for collecting values
     * @return the collector for linked has maps
     */
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
