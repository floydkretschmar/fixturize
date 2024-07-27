package de.floydkretschmar.fixturize.stategies.constants.value.providers.fallback;

import de.floydkretschmar.fixturize.ElementUtils;
import de.floydkretschmar.fixturize.domain.TypeMetadata;
import de.floydkretschmar.fixturize.domain.VariableElementMetadata;
import de.floydkretschmar.fixturize.stategies.constants.value.ValueProviderService;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.ValueProvider;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import javax.lang.model.element.Element;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.util.ElementFilter;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static de.floydkretschmar.fixturize.ElementUtils.findMethodWithModifiersByReturnType;
import static de.floydkretschmar.fixturize.ElementUtils.findSetterForFields;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

@RequiredArgsConstructor
public class BuilderValueProvider implements ValueProvider {

    private final ValueProviderService valueProviderService;

    @Override
    public String provideValueAsString(Element field, TypeMetadata metadata) {
        return provideValueAsString(field, metadata, "builder", "build");
    }

    public String provideValueAsString(Element field, TypeMetadata metadata, String builderMethodName, String buildMethodName) {
        final var fieldType = ((DeclaredType) field.asType());
        final var fieldTypeElement = fieldType.asElement();
        if (Objects.nonNull(fieldType.asElement().getAnnotation(Builder.class))) {
            final var fields = metadata.createVariableElementMetadata(ElementFilter.fieldsIn(fieldTypeElement.getEnclosedElements()));
            return createBuilderValue(
                    fields.stream().collect(ElementUtils.toLinkedMap(VariableElementMetadata::getName, data -> valueProviderService.getValueFor(data.getTypedElement()))),
                    metadata.getQualifiedClassNameWithoutGeneric(),
                    getBuilderMethodName(builderMethodName, metadata),
                    buildMethodName);
        }

        return provideBuildMethodAsValue(fieldTypeElement, metadata, valueProviderService, builderMethodName, buildMethodName);
    }

    private static String provideBuildMethodAsValue(Element declaredElement, TypeMetadata metadata, ValueProviderService valueProviderService, String builderMethodName, String buildMethodName) {
        final var builderName = "%s.%sBuilder".formatted(metadata.getQualifiedClassNameWithoutGeneric(), metadata.getSimpleClassNameWithoutGeneric());
        final var builderMethod = findMethodWithModifiersByReturnType(declaredElement, builderName, builderMethodName, PUBLIC, STATIC);

        if (Objects.isNull(builderMethod)) return DEFAULT_VALUE;

        final var builderType = (DeclaredType) builderMethod.getReturnType();
        final var builderTypeElement = builderType.asElement();
        final var buildMethod = findMethodWithModifiersByReturnType(builderTypeElement, metadata.getQualifiedClassName(), buildMethodName, PUBLIC);

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

    private static String createBuilderValue(Map<String, String> setterAndValueMap, String className, String builderMethodName, String buildMethodName) {
        final var setterString = setterAndValueMap.entrySet().stream()
                .map(setterAndValue -> ".%s(%s)".formatted(setterAndValue.getKey(), setterAndValue.getValue()))
                .collect(Collectors.joining());
        return "%s.%s()%s.%s()".formatted(className, builderMethodName, setterString, buildMethodName);
    }

    private static String getBuilderMethodName(String builderMethodName, TypeMetadata metadata) {
        return "%s%s".formatted(metadata.getGenericPart(), builderMethodName);
    }
}
