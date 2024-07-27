package de.floydkretschmar.fixturize.stategies.value.providers.custom;

import de.floydkretschmar.fixturize.stategies.metadata.TypeMetadata;
import de.floydkretschmar.fixturize.stategies.value.ValueProviderService;
import de.floydkretschmar.fixturize.stategies.value.providers.ValueProvider;
import lombok.RequiredArgsConstructor;

import javax.lang.model.element.Element;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.util.Types;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ContainerValueProvider implements ValueProvider {
    /**
     * The service providing values for {@link Element} instances.
     */
    private final ValueProviderService valueProviderService;

    /**
     * The utility object used to process {@link javax.lang.model.type.TypeMirror} instances
     */
    private final Types typeUtils;

    /**
     * The string that defines value creation for this container value provider including a wild card for parameterization.
     */
    private final String wildcardValueString;

    @Override
    public String provideValueAsString(Element field, TypeMetadata metadata) {
        final var fieldType = field.asType();
        final var declaredType = ((DeclaredType) fieldType);
        final var parameterString = declaredType.getTypeArguments().stream()
                .map(typeUtils::asElement)
                .map(valueProviderService::getValueFor)
                .collect(Collectors.joining(", "));
        return wildcardValueString.formatted(parameterString);
    }
}
