package de.floydkretschmar.fixturize.domain;

import lombok.Builder;
import lombok.Value;

/***
 * Contains all data used to construct a constant in a generated fixture.
 *
 * @author Floyd Kretschmar
 */
@Value
@Builder
public class Constant {
    /***
     * The type of the constant
     */
    String type;

    /***
     * The name of the constant
     */
    String name;

    /***
     * The value of the constant which can be a single value or valid java code.
     */
    String value;

    /***
     * The name of the original field from which the constant was generated.
     */
    String originalFieldName;
}
