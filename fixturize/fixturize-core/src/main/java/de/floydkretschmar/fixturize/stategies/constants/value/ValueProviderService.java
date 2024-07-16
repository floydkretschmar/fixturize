package de.floydkretschmar.fixturize.stategies.constants.value;

import javax.lang.model.element.VariableElement;

/***
 * Defines methods to retrieve the corresponding value representation for a given {@link VariableElement}.
 *
 * @author Floyd Kretschmar
 */
public interface ValueProviderService {

    /***
     * Returns the correct corresponding value representation for the specified field.
     *
     * @param field - for which the value is being retrieved
     * @return the correct value representation
     */
    String getValueFor(VariableElement field);
}
