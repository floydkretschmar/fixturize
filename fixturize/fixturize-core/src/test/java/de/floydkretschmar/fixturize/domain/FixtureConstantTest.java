package de.floydkretschmar.fixturize.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FixtureConstantTest {
    @Test
    void toString_whenCalled_shouldCreateConstantString() {
        final var constant = FixtureConstant.builder().type("int").value("10").name("CONSTANT").build();

        assertThat(constant.toString()).isEqualTo("\tpublic static int CONSTANT = 10;");
    }
}