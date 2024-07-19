package de.floydkretschmar.fixturize.stategies.constants.value.providers;

import de.floydkretschmar.fixturize.ReflectionUtils;
import de.floydkretschmar.fixturize.annotations.FixtureBuilder;
import de.floydkretschmar.fixturize.annotations.FixtureConstructor;
import de.floydkretschmar.fixturize.domain.Names;
import de.floydkretschmar.fixturize.stategies.constants.value.ValueProviderService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.util.ElementFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static de.floydkretschmar.fixturize.ReflectionUtils.findMethodWithModifiersByReturnType;
import static de.floydkretschmar.fixturize.ReflectionUtils.findSetterForFields;
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
 *     <li>{@link Builder}: create an inline builder for all fields</li>
 *     <li>{@link AllArgsConstructor}: create an inline new call to the constructor using all fields</li>
 *     <li>{@link RequiredArgsConstructor}: create an inline new call to the constructor using all fields that are final
 *     and don't have a constant value</li>
 *     <li>{@link AllArgsConstructor}: create an inline new call to the constructor without arguments</li>
 * </ul>
 * 3. If any manually generated constructors are present, use the one with the most arguments as the value
 * 4. If any manually written builder methods are present, create an inline builder call for all setter that match the
 * fields of the constant type. Matching in this case means that the setter method name must end on the name of the field.
 */
@RequiredArgsConstructor
public class FallbackValueProvider implements RecursiveValueProvider {
    /**
     * The default constant value if all other strategies for generation fail.
     */
    public static final String DEFAULT_VALUE = "null";

    @Override
    public String recursivelyProvideValue(VariableElement field, Names names, ValueProviderService valueProviderService) {
        final var fieldType = field.asType();
        final var declaredElement = ((DeclaredType) fieldType).asElement();

        return provideDefaultFallbackValue(declaredElement, names, valueProviderService);
    }

    private String provideDefaultFallbackValue(Element declaredElement, Names names, ValueProviderService valueProviderService) {
        var returnValue = provideBuilderCreationMethodAsValue(declaredElement, names);
        if (!returnValue.equals(DEFAULT_VALUE)) return returnValue;

        returnValue = provideConstructorCreationMethodAsValue(declaredElement, names);
        if (!returnValue.equals(DEFAULT_VALUE)) return returnValue;

        returnValue = provideLombokCreationMethodAsValue(declaredElement, names, valueProviderService);
        if (!returnValue.equals(DEFAULT_VALUE)) return returnValue;

        returnValue = providePublicConstructorAsValue(declaredElement, names, valueProviderService);
        if (!returnValue.equals(DEFAULT_VALUE)) return returnValue;

        returnValue = provideBuildMethodAsValue(declaredElement, names, valueProviderService);
        if (!returnValue.equals(DEFAULT_VALUE)) return returnValue;

        return returnValue;
    }

    private String provideLombokCreationMethodAsValue(Element declaredElement, Names names, ValueProviderService valueProviderService) {
        final var fields = ElementFilter.fieldsIn(declaredElement.getEnclosedElements());

        if (Objects.nonNull(declaredElement.getAnnotation(Builder.class))) {
            return createBuilderValue(
                    fields.stream().collect(ReflectionUtils.toLinkedMap(field -> field.getSimpleName().toString(), valueProviderService::getValueFor)),
                    names.getQualifiedClassName(),
                    "builder",
                    "build");
        } else if (Objects.nonNull(declaredElement.getAnnotation(AllArgsConstructor.class))) {
            return createConstructorValue(names.getQualifiedClassName(), fields, valueProviderService);
        } else if (Objects.nonNull(declaredElement.getAnnotation(RequiredArgsConstructor.class))) {
            final var requiredFields = fields.stream()
                    .filter(field -> field.getModifiers().contains(Modifier.FINAL) && Objects.isNull(field.getConstantValue()))
                    .toList();
            return createConstructorValue(names.getQualifiedClassName(), requiredFields, valueProviderService);
        } else if (Objects.nonNull(declaredElement.getAnnotation(NoArgsConstructor.class))) {
            return createConstructorValue(names.getQualifiedClassName(), new ArrayList<>(), valueProviderService);
        }

        return DEFAULT_VALUE;
    }

    private String provideBuildMethodAsValue(Element declaredElement, Names names, ValueProviderService valueProviderService) {
        final var builderName = "%s.%sBuilder".formatted(names.getQualifiedClassName(), names.getSimpleClassName());
        final var builderMethod = findMethodWithModifiersByReturnType(declaredElement, builderName, PUBLIC, STATIC);

        if (Objects.isNull(builderMethod)) return DEFAULT_VALUE;

        final var builderType = ((DeclaredType) builderMethod.getReturnType()).asElement();
        final var buildMethod = findMethodWithModifiersByReturnType(builderType, names.getQualifiedClassName(), PUBLIC);

        if (Objects.isNull(buildMethod)) return DEFAULT_VALUE;

        final var fields = ElementFilter.fieldsIn(declaredElement.getEnclosedElements());
        final var builderSetter = findSetterForFields(builderType, fields, PUBLIC)
                .filter(entry -> entry.getValue().isPresent())
                .collect(ReflectionUtils.toLinkedMap(entry -> entry.getValue().orElseThrow().getSimpleName().toString(), entry -> valueProviderService.getValueFor(entry.getKey())));

        return createBuilderValue(
                builderSetter,
                names.getQualifiedClassName(),
                builderMethod.getSimpleName().toString(),
                buildMethod.getSimpleName().toString());
    }

    private String providePublicConstructorAsValue(Element declaredElement, Names names, ValueProviderService valueProviderService) {
        final var mostParametersConstructor = ElementFilter.constructorsIn(declaredElement.getEnclosedElements()).stream()
                .filter(constructor -> constructor.getModifiers().contains(PUBLIC))
                .max(comparing(constructor -> constructor.getParameters().size()))
                .orElse(null);
        if (Objects.isNull(mostParametersConstructor)) return DEFAULT_VALUE;

        return createConstructorValue(names.getQualifiedClassName(), mostParametersConstructor.getParameters(), valueProviderService);
    }

    private String provideBuilderCreationMethodAsValue(Element declaredElement, Names names) {
        return Arrays.stream(declaredElement.getAnnotationsByType(FixtureBuilder.class))
                .max(comparing(annotation -> annotation.usedSetters().length))
                .map(firstBuilder -> "%sFixture.%s().build()".formatted(names.getQualifiedClassName(), firstBuilder.methodName()))
                .orElse(DEFAULT_VALUE);
    }

    private String provideConstructorCreationMethodAsValue(Element declaredElement, Names names) {
        return Arrays.stream(declaredElement.getAnnotationsByType(FixtureConstructor.class))
                .max(comparing(annotation -> annotation.constructorParameters().length))
                .map(firstBuilder -> "%sFixture.%s()".formatted(names.getQualifiedClassName(), firstBuilder.methodName()))
                .orElse(DEFAULT_VALUE);
    }

    private String createBuilderValue(Map<String, String> setterAndValueMap, String className, String builderMethodName, String buildMethodName) {
        final var setterString = setterAndValueMap.entrySet().stream()
                .map(setterAndValue -> ".%s(%s)".formatted(setterAndValue.getKey(), setterAndValue.getValue()))
                .collect(Collectors.joining());
        return "%s.%s()%s.%s()".formatted(className, builderMethodName, setterString, buildMethodName);
    }

    private String createConstructorValue(String className, List<? extends VariableElement> parameterValues, ValueProviderService valueProviderService) {
        final var recursiveParameterString = parameterValues.stream()
                .map(valueProviderService::getValueFor)
                .collect(Collectors.joining(", "));
        return "new %s(%s)".formatted(className, recursiveParameterString);
    }
}
