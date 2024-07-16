package de.floydkretschmar.fixturize.stategies.creation;

import de.floydkretschmar.fixturize.annotations.Fixture;
import de.floydkretschmar.fixturize.annotations.FixtureBuilder;
import de.floydkretschmar.fixturize.annotations.FixtureConstructor;
import de.floydkretschmar.fixturize.domain.CreationMethod;
import de.floydkretschmar.fixturize.stategies.constants.ConstantDefinitionMap;

import javax.lang.model.element.TypeElement;
import java.util.Collection;

/**
 * Defines a function that generates a number of methods used to create instances of a generated fixture for a specified
 * class and the already generated constants of the fixture. Generally for each method generation annotation used on a
 * class also annotated with {@link Fixture} one generation method with the following
 * structure will be generated:
 * <br><br>
 * public <b>returnType</b> <b>methodName</b>() {
 * return <b>returnValue</b>;
 * }
 * <br><br>
 * In the above given example, each part is generated according to its own strategy:
 * <ul>
 *     <li><b>returnType</b>:
 *         <ul>
 *             <li><b>className</b> in the case of {@link FixtureConstructor}</li>
 *             <li><b>className</b>.<b>className</b>Builder in the case of {@link FixtureBuilder}</li>
 *         </ul>
 *     </li>
 *     <li><b>methodName</b>: based on the {@link CreationMethodNamingStrategy} specified</li>
 *     <li><b>returnValue</b>:
 *         <ul>
 *             <li>new <b>className</b>(<b>constructorParameters</b>) in case of {@link FixtureConstructor}</li>
 *             <li><b>className</b>.<b>builderMethod</b>().<b>setter</b>() in case of
 *             {@link FixtureBuilder} where there is one <b>setter</b> for each
 *             {@link FixtureBuilder#usedSetters()}</li>
 *         </ul>
 *     </li>
 * </ul>
 *
 * @author Floyd Kretschmar
 */
@FunctionalInterface
public interface CreationMethodGenerationStrategy {

    /**
     * Returns a {@link Collection} of all {@link CreationMethod}s that have been generated
     * for the provided element and constants according to all specified strategies.
     * @param element - for which the creation methods are being generated
     * @param constantMap - which contains the already generated constants for reference
     * @return a {@link Collection} of generated {@link CreationMethod}s
     */
    Collection<CreationMethod> generateCreationMethods(TypeElement element, ConstantDefinitionMap constantMap);
}
