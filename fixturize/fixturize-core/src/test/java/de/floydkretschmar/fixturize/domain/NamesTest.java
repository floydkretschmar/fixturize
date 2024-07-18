package de.floydkretschmar.fixturize.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class NamesTest {
    @Test
    void from_whenCalled_extractNames() {
        final var element = mock(TypeElement.class);
        final var name = mock(Name.class);
        when(name.toString()).thenReturn("de.test.package.Class");
        when(element.getQualifiedName()).thenReturn(name);

        final var result = Names.from(element);

        assertThat(result.getPackageName()).isEqualTo("de.test.package");
        assertThat(result.getSimpleClassName()).isEqualTo("Class");
        assertThat(result.getQualifiedClassName()).isEqualTo("de.test.package.Class");
        assertThat(result.getQualifiedFixtureClassName()).isEqualTo("de.test.package.ClassFixture");
    }

    @Test
    void from_whenCalledForClassWithoutPackage_extractNames() {
        final var element = mock(TypeElement.class);
        final var name = mock(Name.class);
        when(name.toString()).thenReturn("Class");
        when(element.getQualifiedName()).thenReturn(name);

        final var result = Names.from(element);

        assertThat(result.getPackageName()).isEqualTo("");
        assertThat(result.getSimpleClassName()).isEqualTo("Class");
        assertThat(result.getQualifiedClassName()).isEqualTo("Class");
        assertThat(result.getQualifiedFixtureClassName()).isEqualTo("ClassFixture");
    }

    @ParameterizedTest
    @CsvSource({
            "de.test.package.Class, true",
            "Class, false"
    })
    void hasPackage_whenCalled_returnsExpectedResult(String className, boolean expectedResult) {
        final var element = mock(TypeElement.class);
        final var name = mock(Name.class);
        when(name.toString()).thenReturn(className);
        when(element.getQualifiedName()).thenReturn(name);

        final var result = Names.from(element);

        assertThat(result.hasPackageName()).isEqualTo(expectedResult);
    }
}