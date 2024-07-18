package de.floydkretschmar.fixturize.stategies.creation;

import de.floydkretschmar.fixturize.ReflectionUtils;
import de.floydkretschmar.fixturize.annotations.Fixture;
import de.floydkretschmar.fixturize.annotations.FixtureBuilder;
import de.floydkretschmar.fixturize.domain.Constant;
import de.floydkretschmar.fixturize.domain.CreationMethod;
import de.floydkretschmar.fixturize.exceptions.FixtureCreationException;
import de.floydkretschmar.fixturize.stategies.constants.ConstantDefinitionMap;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.util.ElementFilter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static de.floydkretschmar.fixturize.FormattingUtils.WHITESPACE_16;
import static de.floydkretschmar.fixturize.ReflectionUtils.findSetterForFields;
import static javax.lang.model.element.Modifier.PUBLIC;

/**
 * The strategy that generates on creation method for each {@link FixtureBuilder} annotation on a class also annotated
 * with {@link Fixture}. If {@link FixtureBuilder#usedSetters()} is empty, all fields will be used.
 */
public class BuilderCreationMethodStrategy implements CreationMethodGenerationStrategy {
    /**
     * Returns a {@link Collection} of all {@link CreationMethod}s that have been generated
     * for the provided element and constants according to the specified {@link FixtureBuilder} strategy.
     *
     * @param element     - for which the creation methods are being generated
     * @param constantMap - which contains the already generated constants for reference
     * @return a {@link Collection} of generated {@link CreationMethod}s
     */
    @Override
    public Collection<CreationMethod> generateCreationMethods(TypeElement element, ConstantDefinitionMap constantMap) {
        return Arrays.stream(element.getAnnotationsByType(FixtureBuilder.class))
                .map(annotation -> {
                    Collection<Constant> correspondingConstants;
                    if (annotation.usedSetters().length == 0)
                        correspondingConstants = constantMap.values().stream().toList();
                    else
                        correspondingConstants = constantMap.getMatchingConstants(Arrays.asList(annotation.usedSetters()));

                    final var className = element.getSimpleName().toString();

                    return CreationMethod.builder()
                            .returnType("%s.%sBuilder".formatted(className, className))
                            .returnValue(createReturnValueString(element, className, annotation.builderMethod(), correspondingConstants))
                            .name(annotation.methodName())
                            .build();
                }).toList();
    }

    private static Map<String, String> getConstantToSetterMap(Element element, String buildMethodName, Collection<Constant> correspondingConstants) {
        var fieldNames = correspondingConstants.stream().map(Constant::getOriginalFieldName).toList();
        final var buildMethod = ElementFilter.methodsIn(element.getEnclosedElements()).stream()
                .filter(method -> method.getSimpleName().toString().equals(buildMethodName))
                .findFirst()
                .orElse(null);

        if (Objects.isNull(buildMethod)) return Map.of();

        final var builderType = ((DeclaredType) buildMethod.getReturnType()).asElement();
        return findSetterForFields(builderType, fieldNames, PUBLIC)
                .collect(ReflectionUtils.toLinkedMap(
                        Map.Entry::getKey,
                        entry -> entry
                                .getValue()
                                .orElseThrow(() -> new FixtureCreationException("No valid setter could be found on %s to set %s".formatted(builderType, entry.getKey())))
                                .getSimpleName()
                                .toString()));
    }

    private static String createReturnValueString(Element element, String className, String buildMethod, Collection<Constant> correspondingConstants) {
        final var fieldToSetterMap = getConstantToSetterMap(element, buildMethod, correspondingConstants);
        final var setterString = correspondingConstants.stream()
                .map(constant -> ".%s(%s)".formatted(
                        fieldToSetterMap.containsKey(constant.getOriginalFieldName()) ? fieldToSetterMap.get(constant.getOriginalFieldName()) : constant.getOriginalFieldName(),
                        constant.getName()))
                .collect(Collectors.joining("\n%s".formatted(WHITESPACE_16)));
        return "%s.%s()\n%s%s".formatted(className, buildMethod, WHITESPACE_16, setterString);
    }
}