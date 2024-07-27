package de.floydkretschmar.fixturize.stategies.value;

import de.floydkretschmar.fixturize.annotations.FixtureConstant;
import de.floydkretschmar.fixturize.stategies.constants.ConstantMap;
import lombok.RequiredArgsConstructor;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import java.util.Arrays;

/**
 * Decides which value during creation method creation should be used for a given {@link VariableElement}.
 *
 * @author Floyd Kretschmar
 */
@RequiredArgsConstructor
public class CreationMethodValueProviderService implements ValueProviderService {

    /**
     * The value provider service that resolved strings with placeholders
     */
    private final ValueProviderService defaultValueService;

    /**
     * The map of constants which will be used as references when getting values for fields
     */
    private final ConstantMap constantMap;

    /**
     * Returns the corresponding constant name registered in the constant map for the provided element.
     * @param field - for which the value is being retrieved
     * @return the name of the corresponding constant as the field value
     */
    @Override
    public String getValueFor(Element field) {
        final var constantKey = Arrays.stream(field.getAnnotationsByType(FixtureConstant.class))
                .findFirst()
                .map(FixtureConstant::name)
                .orElse(field.toString());
        if (constantMap.containsKey(constantKey))
            return constantMap.get(constantKey).getName();

        return defaultValueService.getValueFor(field);
    }

    /**
     * Returns the value with all default value placeholders resolved by the provided <b>valueResolverService</b>.
     * @param valueStringWithPlaceholders - that contains 0 to N default value wildcards
     * @return the resolved value
     */
    @Override
    public String resolveValuesForDefaultPlaceholders(String valueStringWithPlaceholders) {
        return defaultValueService.resolveValuesForDefaultPlaceholders(valueStringWithPlaceholders);
    }
}
