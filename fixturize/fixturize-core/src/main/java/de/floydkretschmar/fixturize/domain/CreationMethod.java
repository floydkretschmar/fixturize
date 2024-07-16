package de.floydkretschmar.fixturize.domain;

import lombok.Builder;
import lombok.Value;

/***
 * Contains all data used to construct a method that creates a new instance of the generated fixture.
 *
 * @author Floyd Kretschmar
 */
@Value
@Builder
public class CreationMethod {
    /***
     * The return type of the method
     */
    String returnType;

    /***
     * The name of the method
     */
    String name;

    /***
     * The return value of the method
     */
    String returnValue;
}
