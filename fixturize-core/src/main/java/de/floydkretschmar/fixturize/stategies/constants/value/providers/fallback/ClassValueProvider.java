package de.floydkretschmar.fixturize.stategies.constants.value.providers.fallback;

import de.floydkretschmar.fixturize.ElementUtils;
import de.floydkretschmar.fixturize.annotations.FixtureBuilder;
import de.floydkretschmar.fixturize.annotations.FixtureConstructor;
import de.floydkretschmar.fixturize.domain.TypeMetadata;
import de.floydkretschmar.fixturize.domain.VariableElementMetadata;
import de.floydkretschmar.fixturize.stategies.constants.value.ValueProviderService;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.ValueProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.util.ElementFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static de.floydkretschmar.fixturize.ElementUtils.findMethodWithModifiersByReturnType;
import static de.floydkretschmar.fixturize.ElementUtils.findSetterForFields;
import static java.util.Comparator.comparing;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;


/**
 * Defines the default fallback strategy for getting values where no value provider is defined:
 * 1. If type is annotated with {@link FixtureConstructor} or {@link FixtureBuilder}: use the annotation with the most
 * {@link FixtureConstructor#constructorParameters()} or {@link FixtureBuilder#usedSetters()} and use the creation method
 * defined by that annotation
 * 2. If any lombok annotations are present, try to generate to correct value according to the annotation:
 * <ul>
 *     <li>{@link lombok.Builder}: create an inline builder for all fields</li>
 *     <li>{@link lombok.AllArgsConstructor}: create an inline new call to the constructor using all fields</li>
 *     <li>{@link lombok.RequiredArgsConstructor}: create an inline new call to the constructor using all fields that are final
 *     and don't have a constant value</li>
 *     <li>{@link lombok.AllArgsConstructor}: create an inline new call to the constructor without arguments</li>
 * </ul>
 * 3. If any manually generated constructors are present, use the one with the most arguments as the value
 * 4. If any manually written builder methods are present, create an inline builder call for all setter that match the
 * fields of the constant type. Matching in this case means that the setter method name must end on the name of the field.
 */
@RequiredArgsConstructor
public class ClassValueProvider implements ValueProvider {
    private final ValueProviderService valueProviderService;

    @Override
    public String provideValueAsString(Element field, TypeMetadata metadata) {
        final var fieldType = ((DeclaredType) field.asType());
        return provideDefaultFallbackValue(fieldType.asElement(), metadata, valueProviderService);
    }

    private String provideDefaultFallbackValue(Element declaredElement, TypeMetadata metadata, ValueProviderService valueProviderService) {
        var returnValue = provideBuilderCreationMethodAsValue(declaredElement, metadata);
        if (!returnValue.equals(DEFAULT_VALUE)) return returnValue;

        returnValue = provideConstructorCreationMethodAsValue(declaredElement, metadata);
        if (!returnValue.equals(DEFAULT_VALUE)) return returnValue;

        returnValue = provideLombokCreationMethodAsValue(declaredElement, metadata, valueProviderService);
        if (!returnValue.equals(DEFAULT_VALUE)) return returnValue;

        returnValue = providePublicConstructorAsValue(declaredElement, metadata, valueProviderService);
        if (!returnValue.equals(DEFAULT_VALUE)) return returnValue;

        returnValue = provideBuildMethodAsValue(declaredElement, metadata, valueProviderService);
        if (!returnValue.equals(DEFAULT_VALUE)) return returnValue;

        return returnValue;
    }

    private String provideLombokCreationMethodAsValue(Element declaredElement, TypeMetadata metadata, ValueProviderService valueProviderService) {
        final var fields = metadata.createVariableElementMetadata(ElementFilter.fieldsIn(declaredElement.getEnclosedElements()));
        final var constructorName = getConstructorName(metadata);

        if (Objects.nonNull(declaredElement.getAnnotation(Builder.class))) {
            return createBuilderValue(
                    fields.stream().collect(ElementUtils.toLinkedMap(VariableElementMetadata::getName, data -> valueProviderService.getValueFor(data.getTypedElement()))),
                    metadata.getQualifiedClassNameWithoutGeneric(),
                    getBuilderMethodName("builder", metadata),
                    "build");
        } else if (Objects.nonNull(declaredElement.getAnnotation(AllArgsConstructor.class))) {
            return createConstructorValue(
                    constructorName,
                    fields.stream().map(VariableElementMetadata::getTypedElement).toList(),
                    valueProviderService);
        } else if (Objects.nonNull(declaredElement.getAnnotation(RequiredArgsConstructor.class))) {
            final var requiredFields = fields.stream()
                    .filter(field -> field.getModifiers().contains(Modifier.FINAL) && Objects.isNull(field.getConstantValue()))
                    .map(VariableElementMetadata::getTypedElement)
                    .toList();
            return createConstructorValue(
                    constructorName,
                    requiredFields,
                    valueProviderService);
        } else if (Objects.nonNull(declaredElement.getAnnotation(NoArgsConstructor.class))) {
            return createConstructorValue(
                    constructorName,
                    new ArrayList<>(),
                    valueProviderService);
        }

        return DEFAULT_VALUE;
    }

    private String provideBuildMethodAsValue(Element declaredElement, TypeMetadata metadata, ValueProviderService valueProviderService) {
        final var builderName = "%s.%sBuilder".formatted(metadata.getQualifiedClassNameWithoutGeneric(), metadata.getSimpleClassNameWithoutGeneric());
        final var builderMethod = findMethodWithModifiersByReturnType(declaredElement, builderName, PUBLIC, STATIC);

        if (Objects.isNull(builderMethod)) return DEFAULT_VALUE;

        final var builderType = (DeclaredType) builderMethod.getReturnType();
        final var builderTypeElement = builderType.asElement();
        final var buildMethod = findMethodWithModifiersByReturnType(builderTypeElement, metadata.getQualifiedClassName(), PUBLIC);

        if (Objects.isNull(buildMethod)) return DEFAULT_VALUE;

        final var fields = metadata.createVariableElementMetadata(ElementFilter.fieldsIn(declaredElement.getEnclosedElements()));
        final var builderSetter = findSetterForFields(builderTypeElement, fields, builderType, PUBLIC)
                .filter(entry -> entry.getValue().isPresent())
                .collect(ElementUtils.toLinkedMap(
                        entry -> entry.getValue().orElseThrow().getSimpleName().toString(),
                        entry -> valueProviderService.getValueFor(entry.getKey().getTypedElement())));

        return createBuilderValue(
                builderSetter,
                metadata.getQualifiedClassNameWithoutGeneric(),
                getBuilderMethodName(builderMethod.getSimpleName().toString(), metadata),
                buildMethod.getSimpleName().toString());
    }

    private String providePublicConstructorAsValue(Element declaredElement, TypeMetadata metadata, ValueProviderService valueProviderService) {
        final var mostParametersConstructor = ElementFilter.constructorsIn(declaredElement.getEnclosedElements()).stream()
                .filter(constructor -> constructor.getModifiers().contains(PUBLIC))
                .max(comparing(constructor -> constructor.getParameters().size()))
                .orElse(null);
        if (Objects.isNull(mostParametersConstructor)) return DEFAULT_VALUE;

        final var parameters = metadata.createVariableElementMetadata(mostParametersConstructor.getParameters()).stream()
                .map(VariableElementMetadata::getTypedElement)
                .toList();

        return createConstructorValue(getConstructorName(metadata), parameters, valueProviderService);
    }

    private String provideBuilderCreationMethodAsValue(Element declaredElement, TypeMetadata metadata) {
        return Arrays.stream(declaredElement.getAnnotationsByType(FixtureBuilder.class))
                .max(comparing(annotation -> annotation.usedSetters().length))
                .map(firstBuilder -> "%sFixture.%s().build()".formatted(metadata.getQualifiedClassNameWithoutGeneric(), firstBuilder.methodName()))
                .orElse(DEFAULT_VALUE);
    }

    private String provideConstructorCreationMethodAsValue(Element declaredElement, TypeMetadata metadata) {
        return Arrays.stream(declaredElement.getAnnotationsByType(FixtureConstructor.class))
                .max(comparing(annotation -> annotation.constructorParameters().length))
                .map(firstBuilder -> "%sFixture.%s()".formatted(metadata.getQualifiedClassNameWithoutGeneric(), firstBuilder.methodName()))
                .orElse(DEFAULT_VALUE);
    }

    private String createBuilderValue(Map<String, String> setterAndValueMap, String className, String builderMethodName, String buildMethodName) {
        final var setterString = setterAndValueMap.entrySet().stream()
                .map(setterAndValue -> ".%s(%s)".formatted(setterAndValue.getKey(), setterAndValue.getValue()))
                .collect(Collectors.joining());
        return "%s.%s()%s.%s()".formatted(className, builderMethodName, setterString, buildMethodName);
    }

    private String createConstructorValue(String className, List<? extends Element> parameterValues, ValueProviderService valueProviderService) {
        final var recursiveParameterString = parameterValues.stream()
                .map(valueProviderService::getValueFor)
                .collect(Collectors.joining(", "));
        return "new %s(%s)".formatted(className, recursiveParameterString);
    }

    private static String getConstructorName(TypeMetadata metadata) {
        return "%s%s".formatted(metadata.getQualifiedClassNameWithoutGeneric(), metadata.isGeneric() ? "<>" : "");
    }

    private static String getBuilderMethodName(String builderMethodName, TypeMetadata metadata) {
        return "%s%s".formatted(metadata.getGenericPart(), builderMethodName);
    }
}
