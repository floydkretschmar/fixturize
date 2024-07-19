package de.floydkretschmar.fixturize.stategies.constants.value;

import de.floydkretschmar.fixturize.ReflectionUtils;
import de.floydkretschmar.fixturize.annotations.FixtureBuilder;
import de.floydkretschmar.fixturize.annotations.FixtureConstructor;
import de.floydkretschmar.fixturize.domain.Names;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.ValueProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
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
import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.ElementKind.ENUM;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static javax.lang.model.type.TypeKind.ARRAY;

/**
 * Decides which value during constant creation should be used for a given {@link VariableElement}. Also defines the default
 * fallback strategy for getting values where no value provider is defined:
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
 *
 * @author Floyd Kretschmar
 */
@RequiredArgsConstructor
public class ConstantValueProviderService implements ValueProviderService {
    /**
     * The default constant value if all other strategies for generation fail.
     */
    public static final String DEFAULT_VALUE = "null";
    /**
     * All default and custom value providers for classes.
     */
    private final ValueProviderMap valueProviders;


    public ConstantValueProviderService(
            Map<String, ValueProvider> valueProviders) {
        this.valueProviders = new ValueProviderMap(valueProviders);
    }

    /**
     * Returns the correct value that should be used for constant generation for the specified field.
     *
     * @param field - for which the value is being retrieved
     * @return the value used for constant construction
     */
    @Override
    public String getValueFor(VariableElement field) {
        final var fieldType = field.asType();
        final var typeKind = fieldType.getKind();

        final var fullQualifiedTypeName = fieldType.toString();
        final var genericStartIndex = fullQualifiedTypeName.indexOf('<');
        final var classKey = genericStartIndex > 0 ? fullQualifiedTypeName.substring(0, genericStartIndex) : fullQualifiedTypeName;
        if (valueProviders.containsKey(classKey))
            return valueProviders.get(classKey).provideValueAsString(field);

        if (typeKind == TypeKind.DECLARED) {
            final var declaredElement = ((DeclaredType) fieldType).asElement();
            final var elementKind = declaredElement.getKind();
            if (elementKind == ENUM) {
                return provideValueForEnum(declaredElement, fullQualifiedTypeName);
            } else if (elementKind == CLASS) {
                return provideDefaultFallbackValue(declaredElement);
            }
        }

        if (typeKind == ARRAY) {
            return "new %s {}".formatted(field.asType().toString());
        }

        return DEFAULT_VALUE;
    }


    private String provideValueForEnum(Element declaredElement, String fullQualifiedTypeName) {
        final var firstEnumElement = declaredElement.getEnclosedElements().stream()
                .filter(element -> element.getKind().equals(ElementKind.ENUM_CONSTANT))
                .map(Object::toString)
                .findFirst();

        if (firstEnumElement.isEmpty())
            return DEFAULT_VALUE;

        return "%s.%s".formatted(fullQualifiedTypeName, firstEnumElement.get());
    }

    private String provideDefaultFallbackValue(Element declaredElement) {
        final var names = Names.from((TypeElement) declaredElement);
        var returnValue = provideBuilderCreationMethodAsValue(declaredElement, names);
        if (!returnValue.equals(DEFAULT_VALUE)) return returnValue;

        returnValue = provideConstructorCreationMethodAsValue(declaredElement, names);
        if (!returnValue.equals(DEFAULT_VALUE)) return returnValue;

        returnValue = provideLombokCreationMethodAsValue(declaredElement, names);
        if (!returnValue.equals(DEFAULT_VALUE)) return returnValue;

        returnValue = providePublicConstructorAsValue(declaredElement, names);
        if (!returnValue.equals(DEFAULT_VALUE)) return returnValue;

        returnValue = provideBuildMethodAsValue(declaredElement, names);
        if (!returnValue.equals(DEFAULT_VALUE)) return returnValue;

        return returnValue;
    }

    private String provideLombokCreationMethodAsValue(Element declaredElement, Names names) {
        final var fields = ElementFilter.fieldsIn(declaredElement.getEnclosedElements());

        if (Objects.nonNull(declaredElement.getAnnotation(Builder.class))) {
            return createBuilderValue(
                    fields.stream().collect(ReflectionUtils.toLinkedMap(field -> field.getSimpleName().toString(), this::getValueFor)),
                    names.getQualifiedClassName(),
                    "builder",
                    "build");
        } else if (Objects.nonNull(declaredElement.getAnnotation(AllArgsConstructor.class))) {
            return createConstructorValue(names.getSimpleClassName(), fields);
        } else if (Objects.nonNull(declaredElement.getAnnotation(RequiredArgsConstructor.class))) {
            final var requiredFields = fields.stream()
                    .filter(field -> field.getModifiers().contains(Modifier.FINAL) && Objects.isNull(field.getConstantValue()))
                    .toList();
            return createConstructorValue(names.getSimpleClassName(), requiredFields);
        } else if (Objects.nonNull(declaredElement.getAnnotation(NoArgsConstructor.class))) {
            return createConstructorValue(names.getSimpleClassName(), new ArrayList<>());
        }

        return DEFAULT_VALUE;
    }

    private String provideBuildMethodAsValue(Element declaredElement, Names names) {
        final var builderName = "%s.%sBuilder".formatted(names.getQualifiedClassName(), names.getSimpleClassName());
        final var builderMethod = findMethodWithModifiersByReturnType(declaredElement, builderName, PUBLIC, STATIC);

        if (Objects.isNull(builderMethod)) return DEFAULT_VALUE;

        final var builderType = ((DeclaredType) builderMethod.getReturnType()).asElement();
        final var buildMethod = findMethodWithModifiersByReturnType(builderType, names.getQualifiedClassName(), PUBLIC);

        if (Objects.isNull(buildMethod)) return DEFAULT_VALUE;

        final var fields = ElementFilter.fieldsIn(declaredElement.getEnclosedElements());
        final var builderSetter = findSetterForFields(builderType, fields, PUBLIC)
                .filter(entry -> entry.getValue().isPresent())
                .collect(ReflectionUtils.toLinkedMap(entry -> entry.getValue().orElseThrow().getSimpleName().toString(), entry -> this.getValueFor(entry.getKey())));

        return createBuilderValue(
                builderSetter,
                names.getQualifiedClassName(),
                builderMethod.getSimpleName().toString(),
                buildMethod.getSimpleName().toString());
    }

    private String providePublicConstructorAsValue(Element declaredElement, Names names) {
        final var mostParametersConstructor = ElementFilter.constructorsIn(declaredElement.getEnclosedElements()).stream()
                .filter(constructor -> constructor.getModifiers().contains(PUBLIC))
                .max(comparing(constructor -> constructor.getParameters().size()))
                .orElse(null);
        if (Objects.isNull(mostParametersConstructor)) return DEFAULT_VALUE;

        return createConstructorValue(names.getQualifiedClassName(), mostParametersConstructor.getParameters());
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

    private String createConstructorValue(String className, List<? extends VariableElement> parameterValues) {
        final var recursiveParameterString = parameterValues.stream()
                .map(this::getValueFor)
                .collect(Collectors.joining(", "));
        return "new %s(%s)".formatted(className, recursiveParameterString);
    }
}
