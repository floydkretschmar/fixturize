package de.floydkretschmar.fixturize.stategies.constants.value.providers.fallback;

import de.floydkretschmar.fixturize.domain.TypeMetadata;
import de.floydkretschmar.fixturize.domain.VariableElementMetadata;
import de.floydkretschmar.fixturize.stategies.constants.value.ValueProviderService;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.ValueProvider;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.util.ElementFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;
import static javax.lang.model.element.Modifier.PUBLIC;

@RequiredArgsConstructor
public class ConstructorValueProvider implements ValueProvider {

    private final ValueProviderService valueProviderService;
    @Override
    public String provideValueAsString(Element element, TypeMetadata metadata) {
        final var elementTypeElement = ((DeclaredType) element.asType()).asElement();
        final var fields = metadata.createVariableElementMetadata(ElementFilter.fieldsIn(elementTypeElement.getEnclosedElements()));
        final var constructorName = getConstructorName(metadata);

        if (Objects.nonNull(elementTypeElement.getAnnotation(AllArgsConstructor.class))) {
            return createConstructorValue(
                    constructorName,
                    fields.stream().map(VariableElementMetadata::getTypedElement).toList(),
                    valueProviderService);
        } else if (Objects.nonNull(elementTypeElement.getAnnotation(RequiredArgsConstructor.class))) {
            final var requiredFields = fields.stream()
                    .filter(field -> field.getModifiers().contains(Modifier.FINAL) && Objects.isNull(field.getConstantValue()))
                    .map(VariableElementMetadata::getTypedElement)
                    .toList();
            return createConstructorValue(
                    constructorName,
                    requiredFields,
                    valueProviderService);
        } else if (Objects.nonNull(elementTypeElement.getAnnotation(NoArgsConstructor.class))) {
            return createConstructorValue(
                    constructorName,
                    new ArrayList<>(),
                    valueProviderService);
        }

        return providePublicConstructorAsValue(elementTypeElement, metadata, valueProviderService);
    }

    private static String providePublicConstructorAsValue(Element declaredElement, TypeMetadata metadata, ValueProviderService valueProviderService) {
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

    private static String createConstructorValue(String className, List<? extends Element> parameterValues, ValueProviderService valueProviderService) {
        final var recursiveParameterString = parameterValues.stream()
                .map(valueProviderService::getValueFor)
                .collect(Collectors.joining(", "));
        return "new %s(%s)".formatted(className, recursiveParameterString);
    }

    private static String getConstructorName(TypeMetadata metadata) {
        return "%s%s".formatted(metadata.getQualifiedClassNameWithoutGeneric(), metadata.isGeneric() ? "<>" : "");
    }
}
