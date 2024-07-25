package de.floydkretschmar.fixturize.stategies.constants;

import de.floydkretschmar.fixturize.ElementUtils;
import de.floydkretschmar.fixturize.annotations.Fixture;
import de.floydkretschmar.fixturize.annotations.FixtureConstant;
import de.floydkretschmar.fixturize.annotations.FixtureValueProvider;
import de.floydkretschmar.fixturize.domain.Constant;
import de.floydkretschmar.fixturize.domain.TypeMetadata;
import de.floydkretschmar.fixturize.domain.VariableElementMetadata;
import de.floydkretschmar.fixturize.stategies.constants.value.ValueProviderService;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.ValueProvider;
import lombok.RequiredArgsConstructor;

import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;

/**
 * The strategy used to generate constants for a given fixture. Generally for each <b>field</b> in a given class that is
 * annotated with {@link Fixture} one constant with the following format is generated:
 * <br><br>
 * public static <b>tupe</b> <b>constantName</b> = <b>constantValue</b>;
 * <br><br>
 * In the above given example, each part is generated according to its own strategy:
 * <ul>
 *     <li><b>type</b>: The same type as <b>field</b></li>
 *     <li><b>constantName</b>:
 *         <ul>
 *             <li>the name of <b>field</b> according to the specified {@link ConstantsNamingStrategy}</li>
 *             <li>or the name specified by {@link FixtureConstant}</li>
 *         </ul>
 *     </li>
 *     <li><b>constantValue</b>:
 *         <ul>
 *             <li>one of the default {@link ValueProvider}s specified
 *             for the type of <b>field</b></li>
 *             <li>or a custom value provider specified by {@link FixtureValueProvider}</li>
 *             <li>or the value specified by {@link FixtureConstant}</li>
 *         </ul>
 *     </li>
 * </ul>
 * <p>
 * When <b>field</b> is annotated with one or more {@link FixtureConstant} annotations, then one constant per annotation
 * is generated.
 *
 * @author Floyd Kretschmar
 */
@RequiredArgsConstructor
public class ConstantGenerationStrategy {
    /**
     * The strategy used to name generated constants
     */
    private final ConstantsNamingStrategy constantsNamingStrategy;

    /**
     * The service used to determine the value used for a generated constant
     */
    private final ValueProviderService valueProviderService;

    /**
     * Returns a {@link ConstantDefinitionMap} containing all {@link Constant}s that have been generated
     * for the provided element according to all specified strategies.
     *
     * @param element - for which the constants will be generated
     * @return the {@link ConstantDefinitionMap} containing all constant definitions
     */
    public ConstantDefinitionMap generateConstants(TypeElement element, TypeMetadata metadata) {
        final var fields = metadata.createVariableElementMetadata(ElementFilter.fieldsIn(element.getEnclosedElements()));
        final var linkedHashMap = createConstantsForFields(fields.stream())
                .collect(ElementUtils.toLinkedMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue));
        return new FixtureConstantDefinitionMap(linkedHashMap);
    }

    private Stream<Map.Entry<String, Constant>> createConstantsForFields(Stream<VariableElementMetadata> fields) {
        return fields.flatMap(field -> {
            final var constantsAnnotations = field.getAnnotationsByType(FixtureConstant.class);

            if (constantsAnnotations.length == 0) {
                final var constantDefinition = createConstant(field);
                final var key = field.getName();
                return Stream.of(Map.entry(key, constantDefinition));
            }

            return Arrays.stream(constantsAnnotations)
                    .map(constantAnnotation -> {
                        final var constantDefinition = createConstant(constantAnnotation, field);
                        return Map.entry(constantDefinition.getName(), constantDefinition);
                    });
        });
    }

    private Constant createConstant(FixtureConstant constantAnnotation, VariableElementMetadata field) {
        return Constant.builder()
                .type(field.getTypedElement().asType().toString())
                .name(constantAnnotation.name())
                .value(getValue(constantAnnotation, field))
                .originalFieldName(field.getName())
                .build();
    }

    private String getValue(FixtureConstant constantAnnotation, VariableElementMetadata field) {
        if (!constantAnnotation.value().isEmpty()) {
            return this.valueProviderService.resolveValuesForDefaultPlaceholders(constantAnnotation.value());
        }

        return this.valueProviderService.getValueFor(field.getTypedElement());
    }

    private Constant createConstant(VariableElementMetadata field) {
        final var originalFieldName = field.getName();
        return Constant.builder()
                .type(field.getTypedElement().asType().toString())
                .name(constantsNamingStrategy.createConstantName(originalFieldName))
                .value(this.valueProviderService.getValueFor(field.getTypedElement()))
                .originalFieldName(originalFieldName)
                .build();
    }
}
