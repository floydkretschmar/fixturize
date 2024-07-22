package de.floydkretschmar.fixturize.stategies.constants.metadata;

import de.floydkretschmar.fixturize.exceptions.FixtureCreationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConstantMetadataFactoryTest {
    @Mock
    private Elements elementUtils;

    private ConstantMetadataFactory factory;

    @BeforeEach
    void setup() {
        factory = new ConstantMetadataFactory(elementUtils);
    }

    @Test
    void createMetadataFrom_whenCalledForNonGenericField_shouldCreateMetadata() {
        final var element = mock(VariableElement.class);
        final var type = mock(DeclaredType.class);
        when(element.asType()).thenReturn(type);
        when(type.getKind()).thenReturn(TypeKind.DECLARED);
        when(type.getTypeArguments()).thenReturn(List.of());
        when(type.toString()).thenReturn("some.test.Class");

        final var result = factory.createMetadataFrom(element);

        assertThat(result.getQualifiedClassName()).isEqualTo("some.test.Class");
        assertThat(result.getSimpleClassName()).isEqualTo("Class");
        assertThat(result.getPackageName()).isEqualTo("some.test");
        assertThat(result.getQualifiedFixtureClassName()).isEqualTo("some.test.ClassFixture");
        assertThat(result.getSimpleClassNameWithoutGeneric()).isEqualTo("Class");
        assertThat(result.getQualifiedClassNameWithoutGeneric()).isEqualTo("some.test.Class");
        assertThat(result.getGenericPart()).isEqualTo("");
        assertThat(result.getGenericTypeMap()).isEqualTo(Map.of());
    }

    @Test
    void createMetadataFrom_whenCalledForGenericField_shouldCreateMetadata() {
        final var field = mock(VariableElement.class);
        final var fieldType = mock(DeclaredType.class);
        final var concreteGenericType = mock(DeclaredType.class);
        final var fieldTypeWithoutConcreteGenericsElement = mock(TypeElement.class);
        final var fieldTypeWithoutConcreteGenerics = mock(DeclaredType.class);
        final var genericType = mock(TypeMirror.class);

        when(fieldTypeWithoutConcreteGenericsElement.asType()).thenReturn(fieldTypeWithoutConcreteGenerics);
        when(fieldTypeWithoutConcreteGenerics.getTypeArguments()).thenReturn((List) List.of(genericType));
        when(elementUtils.getTypeElement(any())).thenReturn(fieldTypeWithoutConcreteGenericsElement);

        when(field.asType()).thenReturn(fieldType);
        when(fieldType.getKind()).thenReturn(TypeKind.DECLARED);
        when(fieldType.getTypeArguments()).thenReturn((List) List.of(concreteGenericType));
        when(fieldType.toString()).thenReturn("some.test.Class<java.lang.String>");
        when(concreteGenericType.toString()).thenReturn("java.lang.String");

        final var result = factory.createMetadataFrom(field);

        assertThat(result.getQualifiedClassName()).isEqualTo("some.test.Class<java.lang.String>");
        assertThat(result.getSimpleClassName()).isEqualTo("Class<java.lang.String>");
        assertThat(result.getPackageName()).isEqualTo("some.test");
        assertThat(result.getQualifiedFixtureClassName()).isEqualTo("some.test.ClassFixture");
        assertThat(result.getSimpleClassNameWithoutGeneric()).isEqualTo("Class");
        assertThat(result.getQualifiedClassNameWithoutGeneric()).isEqualTo("some.test.Class");
        assertThat(result.getGenericPart()).isEqualTo("<java.lang.String>");
        assertThat(result.getGenericTypeMap()).isEqualTo(Map.of(genericType, concreteGenericType));
    }

    @Test
    void createMetadataFrom_whenCalledForGenericTypeWithListOfConcreteImplementations_shouldCreateMetadata() {
        final var field = mock(VariableElement.class);
        final var fieldType = mock(DeclaredType.class);
        final var concreteGenericTypeElement = mock(TypeElement.class);
        final var concreteGenericType = mock(DeclaredType.class);
        final var fieldTypeWithoutConcreteGenericsElement = mock(TypeElement.class);
        final var fieldTypeWithoutConcreteGenerics = mock(DeclaredType.class);
        final var genericType = mock(TypeMirror.class);

        when(elementUtils.getTypeElement(any())).thenAnswer(params -> {
            final var arg = (String) params.getArguments()[0];
            if ("java.lang.String".equals(arg))
                return concreteGenericTypeElement;

            return fieldTypeWithoutConcreteGenericsElement;
        });

        when(concreteGenericTypeElement.asType()).thenReturn(concreteGenericType);
        when(concreteGenericType.toString()).thenReturn("java.lang.String");

        when(fieldTypeWithoutConcreteGenericsElement.asType()).thenReturn(fieldTypeWithoutConcreteGenerics);
        when(fieldTypeWithoutConcreteGenerics.getTypeArguments()).thenReturn((List) List.of(genericType));

        when(field.asType()).thenReturn(fieldType);
        when(fieldType.toString()).thenReturn("some.test.Class<T>");

        final var result = factory.createMetadataFrom(field, List.of("java.lang.String"));

        assertThat(result.getQualifiedClassName()).isEqualTo("some.test.Class<java.lang.String>");
        assertThat(result.getSimpleClassName()).isEqualTo("Class<java.lang.String>");
        assertThat(result.getPackageName()).isEqualTo("some.test");
        assertThat(result.getQualifiedFixtureClassName()).isEqualTo("some.test.ClassFixture");
        assertThat(result.getSimpleClassNameWithoutGeneric()).isEqualTo("Class");
        assertThat(result.getQualifiedClassNameWithoutGeneric()).isEqualTo("some.test.Class");
        assertThat(result.getGenericPart()).isEqualTo("<java.lang.String>");
        assertThat(result.getGenericTypeMap()).isEqualTo(Map.of(genericType, concreteGenericType));
    }

    @Test
    void createMetadataFrom_whenCalledForGenericTypeWithListOfWrongLength_shouldThrowFixtureCreationException() {
        final var field = mock(VariableElement.class);
        final var fieldType = mock(DeclaredType.class);
        final var concreteGenericTypeElement = mock(TypeElement.class);
        final var concreteGenericType = mock(DeclaredType.class);
        final var fieldTypeWithoutConcreteGenericsElement = mock(TypeElement.class);
        final var fieldTypeWithoutConcreteGenerics = mock(DeclaredType.class);
        final var genericType = mock(TypeMirror.class);

        when(elementUtils.getTypeElement(any())).thenAnswer(params -> {
            final var arg = (String) params.getArguments()[0];
            if ("java.lang.String".equals(arg))
                return concreteGenericTypeElement;

            return fieldTypeWithoutConcreteGenericsElement;
        });

        when(concreteGenericTypeElement.asType()).thenReturn(concreteGenericType);
        when(concreteGenericType.toString()).thenReturn("java.lang.String");

        when(fieldTypeWithoutConcreteGenericsElement.asType()).thenReturn(fieldTypeWithoutConcreteGenerics);
        when(fieldTypeWithoutConcreteGenerics.getTypeArguments()).thenReturn((List) List.of(genericType));

        when(field.asType()).thenReturn(fieldType);
        when(fieldType.toString()).thenReturn("some.test.Class<T>");

        assertThrows(
                FixtureCreationException.class,
                () -> factory.createMetadataFrom(field, List.of("java.lang.String", "java.lang.String")));
    }

    @Test
    void createMetadataFrom_whenCalledForGenericTypeWithListThatContainsInvalidType_shouldThrowFixtureCreationException() {
        final var field = mock(VariableElement.class);
        final var fieldType = mock(DeclaredType.class);
        when(elementUtils.getTypeElement(any())).thenReturn(null);
        when(field.asType()).thenReturn(fieldType);
        when(fieldType.toString()).thenReturn("some.test.Class<T>");

        assertThrows(
                FixtureCreationException.class,
                () -> factory.createMetadataFrom(field, List.of("invalid.Type")));
    }
}