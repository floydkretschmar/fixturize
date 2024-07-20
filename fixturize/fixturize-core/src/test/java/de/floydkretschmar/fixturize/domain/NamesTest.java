package de.floydkretschmar.fixturize.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class NamesTest {
    @Test
    void from_whenCalled_extractNames() {
        final var result = Names.from("de.test.package.Class");

        assertThat(result.getPackageName()).isEqualTo("de.test.package");
        assertThat(result.getSimpleClassName()).isEqualTo("Class");
        assertThat(result.getQualifiedClassName()).isEqualTo("de.test.package.Class");
        assertThat(result.getQualifiedFixtureClassName()).isEqualTo("de.test.package.ClassFixture");
    }

    @Test
    void from_whenCalledForClassWithoutPackage_extractNames() {
        final var result = Names.from("Class");

        assertThat(result.getPackageName()).isEqualTo("");
        assertThat(result.getSimpleClassName()).isEqualTo("Class");
        assertThat(result.getQualifiedClassName()).isEqualTo("Class");
        assertThat(result.getQualifiedFixtureClassName()).isEqualTo("ClassFixture");
    }

    @Test
    void from_whenCalledForGeneric_extractNames() {
        final var result = Names.from("de.test.package.Class<some.package.GenericType>");

        assertThat(result.getPackageName()).isEqualTo("de.test.package");
        assertThat(result.getSimpleClassName()).isEqualTo("Class<some.package.GenericType>");
        assertThat(result.getQualifiedClassName()).isEqualTo("de.test.package.Class<some.package.GenericType>");
        assertThat(result.getQualifiedFixtureClassName()).isEqualTo("de.test.package.ClassFixture");
    }

    @ParameterizedTest
    @CsvSource({
            "de.test.package.Class, true",
            "Class, false"
    })
    void hasPackage_whenCalled_returnsExpectedResult(String className, boolean expectedResult) {
        final var result = Names.from(className);

        assertThat(result.hasPackageName()).isEqualTo(expectedResult);
    }
}