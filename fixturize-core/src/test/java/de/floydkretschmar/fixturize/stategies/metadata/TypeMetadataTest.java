package de.floydkretschmar.fixturize.stategies.metadata;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TypeMetadataTest {

    @ParameterizedTest
    @CsvSource(value = {
            "de.test.package, true",
            "'', false"})
    void hasPackage_whenCalled_returnsExpectedResult(String packageName, boolean expectedResult) {
        final var result = TypeMetadata.builder().packageName(packageName).build();
        assertThat(result.hasPackageName()).isEqualTo(expectedResult);
    }

    @Test
    void createElementMetadata_whenCalled_createVariableElementMetadata() {
        final var field1Type = mock(TypeMirror.class);
        final var field2Type = mock(TypeMirror.class);

        final var field1 = mock(VariableElement.class);
        final var field2 = mock(VariableElement.class);

        final var field2DeclaredType = mock(DeclaredType.class);
        final var field2DeclaredTypeElement = mock(TypeElement.class);

        when(field1.asType()).thenReturn(field1Type);
        when(field2.asType()).thenReturn(field2Type);
        when(field2DeclaredType.asElement()).thenReturn(field2DeclaredTypeElement);

        final var metadata = TypeMetadata.builder()
                .genericTypeMap(Map.of(field2Type, field2DeclaredType))
                .build();

        final var elementMetadata = metadata.createVariableElementMetadata(List.of(field1, field2));

        assertThat(elementMetadata).hasSize(2);
        assertThat(elementMetadata.get(0).getTypedElement()).isEqualTo(field1);
        assertThat(elementMetadata.get(1).getTypedElement()).isEqualTo(field2DeclaredTypeElement);
    }
}