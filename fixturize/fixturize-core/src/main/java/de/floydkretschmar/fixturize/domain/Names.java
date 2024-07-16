package de.floydkretschmar.fixturize.domain;

import lombok.Builder;
import lombok.Value;

/***
 * Contains all names related to the generation of a fixture class.
 */
@Value
@Builder
public class Names {
    /***
     * The fully qualified name of the class for which the fixture is being generated
     */
    String qualifiedClassName;

    /***
     * The simple class name of the class for which the fixture is being generated
     */
    String simpleClassName;

    /***
     * The name of the package which both the original class and the generated fixture will be part of
     */
    String packageName;

    /***
     * The fully qualified name of the generated fixture class.
     */
    String qualifiedFixtureClassName;

    /***
     * Returns a boolean indicating if a package name exists, or if it is empty.
     *
     * @return true if the package name exists, false if it is an empty string
     */
    public boolean hasPackageName() {
        return !this.getPackageName().isEmpty();
    }
}
