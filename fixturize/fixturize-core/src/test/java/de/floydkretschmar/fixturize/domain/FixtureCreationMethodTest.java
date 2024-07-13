package de.floydkretschmar.fixturize.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FixtureCreationMethodTest {
    @Test
    void toString_whenCalled_shouldCreateCreationMethodString() {
        final var creationMethod = FixtureCreationMethod.builder().returnType("int").returnValue("10").name("createInt").build();

        assertThat(creationMethod.toString()).isEqualTo("""
                    \tpublic int createInt() {
                    \t\treturn 10;
                    \t}""");
    }
}