package de.floydkretschmar.fixturize.stategies.creation;

import de.floydkretschmar.fixturize.ElementUtils;
import de.floydkretschmar.fixturize.annotations.Fixture;
import de.floydkretschmar.fixturize.annotations.FixtureBuilder;
import de.floydkretschmar.fixturize.annotations.FixtureBuilderSetter;
import de.floydkretschmar.fixturize.domain.Constant;
import de.floydkretschmar.fixturize.domain.CreationMethod;
import de.floydkretschmar.fixturize.domain.TypeMetadata;
import de.floydkretschmar.fixturize.exceptions.FixtureCreationException;
import de.floydkretschmar.fixturize.stategies.constants.ConstantMap;
import de.floydkretschmar.fixturize.stategies.constants.value.ValueProviderService;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.fallback.BuilderValueProvider;
import lombok.RequiredArgsConstructor;

import javax.lang.model.element.TypeElement;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import static de.floydkretschmar.fixturize.stategies.constants.value.providers.ValueProvider.DEFAULT_VALUE;

/**
 * The strategy that generates on creation method for each {@link FixtureBuilder} annotation on a class also annotated
 * with {@link Fixture}. If {@link FixtureBuilder#usedSetters()} is empty, all fields will be used.
 */
@RequiredArgsConstructor
public class BuilderCreationMethodStrategy implements CreationMethodGenerationStrategy {
    private final ValueProviderService valueProviderService;

    private final BuilderValueProvider noUsedSettersValueProvider;
    /**
     * Returns a {@link Collection} of all {@link CreationMethod}s that have been generated
     * for the provided element and constants according to the specified {@link FixtureBuilder} strategy.
     *
     * @param element     - for which the creation methods are being generated
     * @param constantMap - which contains the already generated constants for reference
     * @return a {@link Collection} of generated {@link CreationMethod}s
     */
    @Override
    public Collection<CreationMethod> generateCreationMethods(TypeElement element, ConstantMap constantMap, TypeMetadata metadata) {
        return Arrays.stream(element.getAnnotationsByType(FixtureBuilder.class))
                .map(annotation -> {
                    final var builderMethod = "%s%s".formatted(metadata.getGenericPart(), annotation.builderMethod());

                    return CreationMethod.builder()
                            .returnType(metadata.getQualifiedClassName())
                            .returnValue(getValue(element, constantMap, metadata, annotation, builderMethod, annotation.buildMethod()))
                            .name(annotation.methodName())
                            .build();
                }).toList();
    }

    private String getValue(TypeElement element, ConstantMap constantMap, TypeMetadata metadata, FixtureBuilder annotation, String builderMethod, String buildMethod) {
        if (annotation.usedSetters().length == 0) {
            final var value = noUsedSettersValueProvider.provideValueAsString(element, metadata, annotation.builderMethod(), annotation.buildMethod());
            if (value.equals(DEFAULT_VALUE))
                throw new FixtureCreationException("Builder creation method could not be created because either builder-method '%s' or build-method '%s' do not exist on class %s."
                        .formatted(builderMethod, buildMethod, metadata.getQualifiedClassName()));
            return value;
        }

        final var valueToSetterMethod = Arrays.stream(annotation.usedSetters())
                .collect(ElementUtils.toLinkedMap(
                        usedSetter -> !usedSetter.value().isEmpty() ? usedSetter.value() : usedSetter.setterName(),
                        FixtureBuilderSetter::setterName));
        final var valueToConstant = constantMap.getMatchingConstants(valueToSetterMethod.keySet().stream().toList());
        final var setterAndValue = valueToConstant.entrySet().stream().map(valueAndOptionalConstant -> {
            final var value = valueAndOptionalConstant.getValue()
                    .map(Constant::getName)
                    .orElse(valueProviderService.resolveValuesForDefaultPlaceholders(valueAndOptionalConstant.getKey()));
            return Map.entry(valueToSetterMethod.get(valueAndOptionalConstant.getKey()), value);
        }).collect(ElementUtils.toLinkedMap(Map.Entry::getKey, Map.Entry::getValue));

        return createReturnValueString(metadata.getQualifiedClassNameWithoutGeneric(), builderMethod, buildMethod, setterAndValue);
    }

    private static String createReturnValueString(String className, String builderMethod, String buildMethod, Map<String, String> setterAndValue) {
        final var setterString = setterAndValue.entrySet().stream()
                .map(setterNameAndValue -> ".%s(%s)".formatted(
                        setterNameAndValue.getKey(),
                        setterNameAndValue.getValue()))
                .collect(Collectors.joining());
        return "%s.%s()%s.%s()".formatted(className, builderMethod, setterString, buildMethod);
    }
}