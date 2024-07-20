package de.floydkretschmar.fixturize.stategies.constants.value.providers.fallback;

import de.floydkretschmar.fixturize.TestFixtures;
import de.floydkretschmar.fixturize.annotations.FixtureBuilder;
import de.floydkretschmar.fixturize.annotations.FixtureConstructor;
import de.floydkretschmar.fixturize.domain.Names;
import de.floydkretschmar.fixturize.stategies.constants.value.ValueProviderService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static de.floydkretschmar.fixturize.TestFixtures.createDeclaredTypeFixture;
import static de.floydkretschmar.fixturize.TestFixtures.createExecutableElementFixture;
import static de.floydkretschmar.fixturize.TestFixtures.createFixtureBuilderFixture;
import static de.floydkretschmar.fixturize.TestFixtures.createFixtureConstructorFixture;
import static de.floydkretschmar.fixturize.TestFixtures.createVariableElementFixtureForValueProviderServiceTest;
import static de.floydkretschmar.fixturize.stategies.constants.value.ConstantValueProviderService.DEFAULT_VALUE;
import static javax.lang.model.element.ElementKind.CONSTRUCTOR;
import static javax.lang.model.element.ElementKind.FIELD;
import static javax.lang.model.element.ElementKind.METHOD;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static javax.lang.model.type.TypeKind.DECLARED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClassValueProviderTest {

    private ClassValueProvider valueProvider;

    @Mock
    private ValueProviderService valueProviderService;

    private Names names;

    @Mock
    private VariableElement field;

    @BeforeEach
    void setup() {
        valueProvider = new ClassValueProvider(valueProviderService);
        names = Names.from("some.test.Class");
    }

    @Test
    void provideValueAsString_whenFallbackForFixtureBuilder_returnValueStringForFixtureBuilderWithMostSetters() {
        final var fixtureBuilder = createFixtureBuilderFixture("methodName1", null, "param1", "param2");
        final var fixtureBuilder2 = createFixtureBuilderFixture(null, null, "param1");
        final var type = TestFixtures.createDeclaredTypeFixture();
        final var typeAsElement = type.asElement();

        when(typeAsElement.getAnnotationsByType(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(FixtureBuilder.class))))
                .thenReturn(new FixtureBuilder[]{fixtureBuilder, fixtureBuilder2});
        when(field.asType()).thenReturn(type);

        final var result = valueProvider.provideValueAsString(field, names);

        assertThat(result).isEqualTo("some.test.ClassFixture.methodName1().build()");
        verifyNoInteractions(valueProviderService);
    }

    @Test
    void provideValueAsString_whenFallbackForFixtureConstructor_returnValueStringForFixtureConstructorWithMostParameters() {
        final var fixtureConstructor = createFixtureConstructorFixture("methodName1", "param1", "param2");
        final var fixtureConstructor2 = createFixtureConstructorFixture(null, "param1", "param2");
        final var type = TestFixtures.createDeclaredTypeFixture();
        final var typeAsElement = type.asElement();

        when(typeAsElement.getAnnotationsByType(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(FixtureBuilder.class))))
                .thenReturn(new FixtureBuilder[]{});
        when(typeAsElement.getAnnotationsByType(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(FixtureConstructor.class))))
                .thenReturn(new FixtureConstructor[]{fixtureConstructor, fixtureConstructor2});
        when(field.asType()).thenReturn(type);

        final var result = valueProvider.provideValueAsString(field, names);

        assertThat(result).isEqualTo("some.test.ClassFixture.methodName1()");
        verifyNoInteractions(valueProviderService);
    }

    @Test
    void provideValueAsString_whenFallbackForLombokBuilder_returnBuilderValueWithAllFieldsAsSetters() {
        final var field1 = createVariableElementFixtureForValueProviderServiceTest("integerField", DECLARED, true, FIELD);
        final var field2 = createVariableElementFixtureForValueProviderServiceTest("classWithValueProviderField", DECLARED, true, FIELD);
        final var type = TestFixtures.createDeclaredTypeFixture(field1, field2);
        final var typeAsElement = (TypeElement) type.asElement();

        when(typeAsElement.getAnnotationsByType(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(FixtureBuilder.class))))
                .thenReturn(new FixtureBuilder[]{});
        when(typeAsElement.getAnnotationsByType(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(FixtureConstructor.class))))
                .thenReturn(new FixtureConstructor[]{});
        when(typeAsElement.getAnnotation(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(Builder.class))))
                .thenReturn(mock(Builder.class));

        when(field.asType()).thenReturn(type);

        when(valueProviderService.getValueFor(eq(field1))).thenReturn("10");
        when(valueProviderService.getValueFor(eq(field2))).thenReturn("classWithValueProviderFieldTypeValue");

        final var result = valueProvider.provideValueAsString(field, names);

        assertThat(result).isEqualTo("some.test.Class.builder().integerField(10).classWithValueProviderField(classWithValueProviderFieldTypeValue).build()");
        verify(valueProviderService, times(1)).getValueFor(field1);
        verify(valueProviderService, times(1)).getValueFor(field2);
        verifyNoMoreInteractions(valueProviderService);
    }

    @Test
    void provideValueAsString_whenFallbackForLombokAllArgsConstructor_returnConstructorValueWithAllFieldsAsParameters() {
        final var field1 = createVariableElementFixtureForValueProviderServiceTest("integerField", DECLARED, false, FIELD);
        final var field2 = createVariableElementFixtureForValueProviderServiceTest("classWithValueProviderField", DECLARED, false, FIELD);
        final var type = TestFixtures.createDeclaredTypeFixture(field1, field2);
        final var typeAsElement = (TypeElement) type.asElement();

        when(typeAsElement.getAnnotationsByType(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(FixtureBuilder.class))))
                .thenReturn(new FixtureBuilder[]{});
        when(typeAsElement.getAnnotationsByType(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(FixtureConstructor.class))))
                .thenReturn(new FixtureConstructor[]{});
        when(typeAsElement.getAnnotation(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(Builder.class))))
                .thenReturn(null);
        when(typeAsElement.getAnnotation(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(AllArgsConstructor.class))))
                .thenReturn(mock(AllArgsConstructor.class));

        when(field.asType()).thenReturn(type);

        when(valueProviderService.getValueFor(eq(field1))).thenReturn("10");
        when(valueProviderService.getValueFor(eq(field2))).thenReturn("classWithValueProviderFieldTypeValue");

        final var result = valueProvider.provideValueAsString(field, names);

        assertThat(result).isEqualTo("new some.test.Class(10, classWithValueProviderFieldTypeValue)");
        verify(valueProviderService, times(1)).getValueFor(field1);
        verify(valueProviderService, times(1)).getValueFor(field2);
        verifyNoMoreInteractions(valueProviderService);
    }


    @Test
    void provideValueAsString_whenFallbackForLombokRequiredArgsConstructor_returnConstructorValueWithAllFinalFieldsWithoutConstantAsParameters() {
        final var field1 = mock(VariableElement.class);
        when(field1.getKind()).thenReturn(FIELD);
        when(field1.getModifiers()).thenReturn(Set.of(PRIVATE));
        final var field2 = createVariableElementFixtureForValueProviderServiceTest("classWithValueProviderField", DECLARED, false, FIELD);
        when(field2.getModifiers()).thenReturn(Set.of(PRIVATE, FINAL));
        final var field3 = mock(VariableElement.class);
        when(field3.getKind()).thenReturn(FIELD);
        when(field3.getModifiers()).thenReturn(Set.of(PRIVATE, FINAL));
        when(field3.getConstantValue()).thenReturn(true);

        final var type = TestFixtures.createDeclaredTypeFixture(field1, field2, field3);
        final var typeAsElement = (TypeElement) type.asElement();

        when(typeAsElement.getAnnotationsByType(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(FixtureBuilder.class))))
                .thenReturn(new FixtureBuilder[]{});
        when(typeAsElement.getAnnotationsByType(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(FixtureConstructor.class))))
                .thenReturn(new FixtureConstructor[]{});
        when(typeAsElement.getAnnotation(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(Builder.class))))
                .thenReturn(null);
        when(typeAsElement.getAnnotation(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(AllArgsConstructor.class))))
                .thenReturn(null);
        when(typeAsElement.getAnnotation(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(RequiredArgsConstructor.class))))
                .thenReturn(mock(RequiredArgsConstructor.class));

        when(field.asType()).thenReturn(type);

        when(valueProviderService.getValueFor(eq(field2))).thenReturn("classWithValueProviderFieldTypeValue");

        final var result = valueProvider.provideValueAsString(field, names);

        assertThat(result).isEqualTo("new some.test.Class(classWithValueProviderFieldTypeValue)");
        verify(valueProviderService, times(1)).getValueFor(field2);
        verifyNoMoreInteractions(valueProviderService);
    }

    @Test
    void provideValueAsString_whenFallbackForLombokNoArgsConstructor_returnConstructorValueWithNoParameters() {
        final var type = TestFixtures.createDeclaredTypeFixture();
        final var typeAsElement = (TypeElement) type.asElement();

        when(typeAsElement.getAnnotationsByType(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(FixtureBuilder.class))))
                .thenReturn(new FixtureBuilder[]{});
        when(typeAsElement.getAnnotationsByType(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(FixtureConstructor.class))))
                .thenReturn(new FixtureConstructor[]{});
        when(typeAsElement.getAnnotation(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(Builder.class))))
                .thenReturn(null);
        when(typeAsElement.getAnnotation(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(AllArgsConstructor.class))))
                .thenReturn(null);
        when(typeAsElement.getAnnotation(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(RequiredArgsConstructor.class))))
                .thenReturn(null);
        when(typeAsElement.getAnnotation(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(NoArgsConstructor.class))))
                .thenReturn(mock(NoArgsConstructor.class));

        when(field.asType()).thenReturn(type);

        final var result = valueProvider.provideValueAsString(field, names);

        assertThat(result).isEqualTo("new some.test.Class()");
        verifyNoInteractions(valueProviderService);
    }

    @Test
    void provideValueAsString_whenFallbackDefinedConstructor_returnConstructorValueWithParameters() {
        final var parameter1 = createVariableElementFixtureForValueProviderServiceTest("integerParameter", DECLARED, false, null);
        final var parameter2 = createVariableElementFixtureForValueProviderServiceTest("booleanParameter", DECLARED, false, null);
        final var constructor1 = createExecutableElementFixture(CONSTRUCTOR, PUBLIC);
        when(constructor1.getParameters()).thenReturn((List) List.of(parameter1, parameter2));
        final var constructor2 = createExecutableElementFixture(CONSTRUCTOR, PUBLIC);
        when(constructor2.getParameters()).thenReturn((List) List.of(parameter1));

        final var type = TestFixtures.createDeclaredTypeFixture(constructor1, constructor2);
        final var typeAsElement = (TypeElement) type.asElement();

        when(typeAsElement.getAnnotationsByType(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(FixtureBuilder.class))))
                .thenReturn(new FixtureBuilder[]{});
        when(typeAsElement.getAnnotationsByType(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(FixtureConstructor.class))))
                .thenReturn(new FixtureConstructor[]{});
        when(typeAsElement.getAnnotation(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(Builder.class))))
                .thenReturn(null);
        when(typeAsElement.getAnnotation(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(AllArgsConstructor.class))))
                .thenReturn(null);
        when(typeAsElement.getAnnotation(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(RequiredArgsConstructor.class))))
                .thenReturn(null);
        when(typeAsElement.getAnnotation(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(NoArgsConstructor.class))))
                .thenReturn(null);

        when(field.asType()).thenReturn(type);

        when(valueProviderService.getValueFor(eq(parameter1))).thenReturn("10");
        when(valueProviderService.getValueFor(eq(parameter2))).thenReturn("true");

        final var result = valueProvider.provideValueAsString(field, names);

        assertThat(result).isEqualTo("new some.test.Class(10, true)");
        verify(valueProviderService, times(1)).getValueFor(parameter1);
        verify(valueProviderService, times(1)).getValueFor(parameter2);
        verifyNoMoreInteractions(valueProviderService);
    }

    @Test
    void provideValueAsString_whenFallbackDefinedBuilderMethod_returnBuilderValueWithAllAvailableSetters() {
        final var classBuilderType = TestFixtures.createDeclaredTypeFixture("%s.%sBuilder".formatted(names.getQualifiedClassName(), names.getSimpleClassName()));
        final var setter1 = createExecutableElementFixture("setIntegerField", METHOD, classBuilderType, PUBLIC);
        final var setter2 = createExecutableElementFixture("setBooleanField", METHOD, classBuilderType, PUBLIC);

        final var builderMethod = createExecutableElementFixture("builder", METHOD, classBuilderType, PUBLIC, STATIC);
        final var field1 = createVariableElementFixtureForValueProviderServiceTest("integerField", DECLARED, false, FIELD);
        when(field1.toString()).thenReturn("integerField");
        final var field2 = createVariableElementFixtureForValueProviderServiceTest("booleanField", DECLARED, false, FIELD);
        when(field2.toString()).thenReturn("booleanField");
        final var field3 = mock(VariableElement.class);
        when(field3.toString()).thenReturn("fieldWithoutSetter");
        when(field3.getKind()).thenReturn(FIELD);
        final var classType = createDeclaredTypeFixture(names.getQualifiedClassName(), builderMethod, field1, field2, field3);

        final var buildMethod = createExecutableElementFixture("build", METHOD, classType, PUBLIC);
        when(classBuilderType.asElement().getEnclosedElements()).thenReturn((List) List.of(setter1, setter2, buildMethod));

        final var typeAsElement = (TypeElement) classType.asElement();
        when(typeAsElement.getAnnotationsByType(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(FixtureBuilder.class))))
                .thenReturn(new FixtureBuilder[]{});
        when(typeAsElement.getAnnotationsByType(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(FixtureConstructor.class))))
                .thenReturn(new FixtureConstructor[]{});
        when(typeAsElement.getAnnotation(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(Builder.class))))
                .thenReturn(null);
        when(typeAsElement.getAnnotation(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(AllArgsConstructor.class))))
                .thenReturn(null);
        when(typeAsElement.getAnnotation(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(RequiredArgsConstructor.class))))
                .thenReturn(null);
        when(typeAsElement.getAnnotation(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(NoArgsConstructor.class))))
                .thenReturn(null);

        when(field.asType()).thenReturn(classType);

        when(valueProviderService.getValueFor(eq(field1))).thenReturn("10");
        when(valueProviderService.getValueFor(eq(field2))).thenReturn("true");

        final var result = valueProvider.provideValueAsString(field, names);

        assertThat(result).isEqualTo("some.test.Class.builder().setIntegerField(10).setBooleanField(true).build()");
        verify(valueProviderService, times(1)).getValueFor(field1);
        verify(valueProviderService, times(1)).getValueFor(field2);
        verifyNoMoreInteractions(valueProviderService);
    }

    @Test
    void provideValueAsString_whenFallbackDefinedBuilderMethodButNoBuilderFound_returnDefaultValue() {
        final var field1 = mock(VariableElement.class);
        final var field2 = mock(VariableElement.class);
        final var classType = TestFixtures.createDeclaredTypeFixture(field1, field2);

        final var typeAsElement = (TypeElement) classType.asElement();
        when(typeAsElement.getAnnotationsByType(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(FixtureBuilder.class))))
                .thenReturn(new FixtureBuilder[]{});
        when(typeAsElement.getAnnotationsByType(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(FixtureConstructor.class))))
                .thenReturn(new FixtureConstructor[]{});
        when(typeAsElement.getAnnotation(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(Builder.class))))
                .thenReturn(null);
        when(typeAsElement.getAnnotation(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(AllArgsConstructor.class))))
                .thenReturn(null);
        when(typeAsElement.getAnnotation(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(RequiredArgsConstructor.class))))
                .thenReturn(null);
        when(typeAsElement.getAnnotation(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(NoArgsConstructor.class))))
                .thenReturn(null);

        when(field.asType()).thenReturn(classType);

        final var result = valueProvider.provideValueAsString(field, names);

        assertThat(result).isEqualTo(DEFAULT_VALUE);
        verifyNoInteractions(valueProviderService);
    }

    @Test
    void provideValueAsString_whenFallbackDefinedBuilderMethodButNoBuildMethodFound_returnDefaultValue() {
        final var classBuilderType = mock(DeclaredType.class);

        final var builderMethod = createExecutableElementFixture(METHOD, PUBLIC, STATIC);
        when(builderMethod.getReturnType()).thenReturn(classBuilderType);
        final var field1 = mock(VariableElement.class);
        final var field2 = mock(VariableElement.class);
        final var classType = TestFixtures.createDeclaredTypeFixture(builderMethod, field1, field2);

        final var typeAsElement = (TypeElement) classType.asElement();
        when(typeAsElement.getAnnotationsByType(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(FixtureBuilder.class))))
                .thenReturn(new FixtureBuilder[]{});
        when(typeAsElement.getAnnotationsByType(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(FixtureConstructor.class))))
                .thenReturn(new FixtureConstructor[]{});
        when(typeAsElement.getAnnotation(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(Builder.class))))
                .thenReturn(null);
        when(typeAsElement.getAnnotation(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(AllArgsConstructor.class))))
                .thenReturn(null);
        when(typeAsElement.getAnnotation(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(RequiredArgsConstructor.class))))
                .thenReturn(null);
        when(typeAsElement.getAnnotation(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(NoArgsConstructor.class))))
                .thenReturn(null);

        when(field.asType()).thenReturn(classType);

        final var result = valueProvider.provideValueAsString(field, names);

        assertThat(result).isEqualTo(DEFAULT_VALUE);
        verifyNoInteractions(valueProviderService);
    }
}