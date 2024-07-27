package de.floydkretschmar.fixturize.annotations;


public @interface FixtureBuilderSetter {
    String setterName();

    String value() default "";
}
