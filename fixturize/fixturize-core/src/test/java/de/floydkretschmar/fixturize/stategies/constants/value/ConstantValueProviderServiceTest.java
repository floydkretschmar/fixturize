package de.floydkretschmar.fixturize.stategies.constants.value;

import de.floydkretschmar.fixturize.TestFixtures;
import de.floydkretschmar.fixturize.annotations.FixtureBuilder;
import de.floydkretschmar.fixturize.annotations.FixtureConstructor;
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

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static de.floydkretschmar.fixturize.TestFixtures.createDeclaredTypeFixture;
import static de.floydkretschmar.fixturize.TestFixtures.createDeclaredTypeFixtureForValueProviderServiceTest;
import static de.floydkretschmar.fixturize.TestFixtures.createExecutableElementFixture;
import static de.floydkretschmar.fixturize.TestFixtures.createFixtureBuilderFixture;
import static de.floydkretschmar.fixturize.TestFixtures.createFixtureConstructorFixture;
import static de.floydkretschmar.fixturize.TestFixtures.createTypeMirrorFixture;
import static de.floydkretschmar.fixturize.TestFixtures.createVariableElementFixtureForValueProviderServiceTest;
import static de.floydkretschmar.fixturize.stategies.constants.value.ConstantValueProviderService.DEFAULT_VALUE;
import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.ElementKind.CONSTRUCTOR;
import static javax.lang.model.element.ElementKind.ENUM;
import static javax.lang.model.element.ElementKind.ENUM_CONSTANT;
import static javax.lang.model.element.ElementKind.FIELD;
import static javax.lang.model.element.ElementKind.METHOD;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static javax.lang.model.type.TypeKind.ARRAY;
import static javax.lang.model.type.TypeKind.DECLARED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConstantValueProviderServiceTest {

    @Mock
    private ValueProviderMap valueProviderMap;

    @Mock
    private VariableElement field;

    private ConstantValueProviderService service;

    @BeforeEach
    void setup() {
        service = new ConstantValueProviderService(valueProviderMap);
    }

    @Test
    void getValueFor_whenCalledForDefinedType_returnCorrespondingValueString() {
        final var type = createTypeMirrorFixture(DECLARED, "ClassName");

        when(field.asType()).thenReturn(type);

        when(valueProviderMap.containsKey(any(String.class))).thenReturn(true);
        when(valueProviderMap.get(any(String.class))).thenReturn(f -> "value");

        final var result = service.getValueFor(field);

        assertThat(result).isEqualTo("value");
        verify(valueProviderMap, times(1)).containsKey("ClassName");
        verify(valueProviderMap, times(1)).get("ClassName");
        verifyNoMoreInteractions(valueProviderMap);
    }

    @Test
    void getValueFor_whenCalledForEnum_returnCorrespondingValueString() {
        final var enumConstant = mock(Element.class);
        final var type = createDeclaredTypeFixture("EnumType", ENUM, enumConstant);
        when(enumConstant.getKind()).thenReturn(ENUM_CONSTANT);
        when(enumConstant.toString()).thenReturn("CONSTANT_VALUE");
        when(field.asType()).thenReturn(type);

        when(valueProviderMap.containsKey(anyString())).thenReturn(false);

        final var result = service.getValueFor(field);

        assertThat(result).isEqualTo("EnumType.CONSTANT_VALUE");
        verify(valueProviderMap, times(1)).containsKey("EnumType");
        verifyNoMoreInteractions(valueProviderMap);
    }

    @Test
    void getValueFor_whenCalledForEnumWithoutConstants_returnDefaultValue() {
        final var type = createDeclaredTypeFixture("EnumType", ENUM);
        when(field.asType()).thenReturn(type);

        when(valueProviderMap.containsKey(anyString())).thenReturn(false);

        final var result = service.getValueFor(field);

        assertThat(result).isEqualTo(DEFAULT_VALUE);
        verify(valueProviderMap, times(1)).containsKey("EnumType");
        verifyNoMoreInteractions(valueProviderMap);
    }

    @Test
    void getValueFor_whenCalledForArray_returnCorrespondingValueString() {
        final var type = createTypeMirrorFixture(ARRAY, "ArrayType[]");
        when(field.asType()).thenReturn(type);

        when(valueProviderMap.containsKey(anyString())).thenReturn(false);

        final var result = service.getValueFor(field);

        assertThat(result).isEqualTo("new ArrayType[] {}");
        verify(valueProviderMap, times(1)).containsKey("ArrayType[]");
        verifyNoMoreInteractions(valueProviderMap);
    }

    @Test
    void getValueFor_whenFallbackForFixtureBuilder_returnValueStringForFixtureBuilderWithMostSetters() {
        final var fixtureBuilder = createFixtureBuilderFixture("methodName1", null, "param1", "param2");
        final var fixtureBuilder2 = createFixtureBuilderFixture(null, null, "param1");
        final var type = TestFixtures.createDeclaredTypeFixtureForValueProviderServiceTest("FixtureBuilderClass", CLASS);
        final var typeAsElement = type.asElement();

        when(typeAsElement.getAnnotationsByType(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(FixtureBuilder.class))))
                .thenReturn(new FixtureBuilder[]{fixtureBuilder, fixtureBuilder2});
        when(field.asType()).thenReturn(type);
        when(valueProviderMap.containsKey(anyString())).thenReturn(false);

        final var result = service.getValueFor(field);

        assertThat(result).isEqualTo("FixtureBuilderClassFixture.methodName1().build()");
        verify(valueProviderMap, times(1)).containsKey("FixtureBuilderClass");
        verifyNoMoreInteractions(valueProviderMap);
    }

    @Test
    void getValueFor_whenFallbackForFixtureConstructor_returnValueStringForFixtureConstructorWithMostParameters() {
        final var fixtureConstructor = createFixtureConstructorFixture("methodName1", "param1", "param2");
        final var fixtureConstructor2 = createFixtureConstructorFixture(null, "param1", "param2");
        final var type = createDeclaredTypeFixtureForValueProviderServiceTest("FixtureConstructorClass", CLASS);
        final var typeAsElement = type.asElement();

        when(typeAsElement.getAnnotationsByType(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(FixtureBuilder.class))))
                .thenReturn(new FixtureBuilder[]{});
        when(typeAsElement.getAnnotationsByType(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(FixtureConstructor.class))))
                .thenReturn(new FixtureConstructor[]{fixtureConstructor, fixtureConstructor2});
        when(field.asType()).thenReturn(type);
        when(valueProviderMap.containsKey(anyString())).thenReturn(false);

        final var result = service.getValueFor(field);

        assertThat(result).isEqualTo("FixtureConstructorClassFixture.methodName1()");
        verify(valueProviderMap, times(1)).containsKey("FixtureConstructorClass");
        verifyNoMoreInteractions(valueProviderMap);
    }

    @Test
    void getValueFor_whenFallbackForLombokBuilder_returnBuilderValueWithAllFieldsAsSetters() {
        final var field1 = createVariableElementFixtureForValueProviderServiceTest("integerField", DECLARED, true, FIELD);
        final var field2 = createVariableElementFixtureForValueProviderServiceTest("classWithValueProviderField", DECLARED, true, FIELD);
        final var type = createDeclaredTypeFixtureForValueProviderServiceTest("LombokClass", CLASS, field1, field2);
        final var typeAsElement = (TypeElement) type.asElement();

        when(typeAsElement.getAnnotationsByType(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(FixtureBuilder.class))))
                .thenReturn(new FixtureBuilder[]{});
        when(typeAsElement.getAnnotationsByType(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(FixtureConstructor.class))))
                .thenReturn(new FixtureConstructor[]{});
        when(typeAsElement.getAnnotation(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(Builder.class))))
                .thenReturn(mock(Builder.class));

        when(field.asType()).thenReturn(type);

        when(valueProviderMap.containsKey(anyString())).thenReturn(false);
        when(valueProviderMap.containsKey(eq("classWithValueProviderFieldType"))).thenReturn(true);
        when(valueProviderMap.containsKey(eq("integerFieldType"))).thenReturn(true);
        when(valueProviderMap.get(eq("classWithValueProviderFieldType"))).thenReturn(f -> "classWithValueProviderFieldTypeValue");
        when(valueProviderMap.get(eq("integerFieldType"))).thenReturn(f -> "10");

        final var result = service.getValueFor(field);

        assertThat(result).isEqualTo("LombokClass.builder().integerField(10).classWithValueProviderField(classWithValueProviderFieldTypeValue).build()");
        verify(valueProviderMap, times(1)).containsKey("LombokClass");
        verify(valueProviderMap, times(1)).containsKey("integerFieldType");
        verify(valueProviderMap, times(1)).containsKey("classWithValueProviderFieldType");
        verify(valueProviderMap, times(1)).get("classWithValueProviderFieldType");
        verify(valueProviderMap, times(1)).get("integerFieldType");
        verifyNoMoreInteractions(valueProviderMap);
    }

    @Test
    void getValueFor_whenFallbackForLombokAllArgsConstructor_returnConstructorValueWithAllFieldsAsParameters() {
        final var field1 = createVariableElementFixtureForValueProviderServiceTest("integerField", DECLARED, false, FIELD);
        final var field2 = createVariableElementFixtureForValueProviderServiceTest("classWithValueProviderField", DECLARED, false, FIELD);
        final var type = createDeclaredTypeFixtureForValueProviderServiceTest("LombokClass", CLASS, field1, field2);
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

        when(valueProviderMap.containsKey(anyString())).thenReturn(false);
        when(valueProviderMap.containsKey(eq("classWithValueProviderFieldType"))).thenReturn(true);
        when(valueProviderMap.get(eq("classWithValueProviderFieldType"))).thenReturn(f -> "classWithValueProviderFieldTypeValue");
        when(valueProviderMap.containsKey(eq("integerFieldType"))).thenReturn(true);
        when(valueProviderMap.get(eq("integerFieldType"))).thenReturn(field -> "10");

        final var result = service.getValueFor(field);

        assertThat(result).isEqualTo("new LombokClass(10, classWithValueProviderFieldTypeValue)");
        verify(valueProviderMap, times(1)).containsKey("integerFieldType");
        verify(valueProviderMap, times(1)).containsKey("LombokClass");
        verify(valueProviderMap, times(1)).containsKey("classWithValueProviderFieldType");
        verify(valueProviderMap, times(1)).get("classWithValueProviderFieldType");
        verify(valueProviderMap, times(1)).get("integerFieldType");
        verifyNoMoreInteractions(valueProviderMap);
    }


    @Test
    void getValueFor_whenFallbackForLombokRequiredArgsConstructor_returnConstructorValueWithAllFinalFieldsWithoutConstantAsParameters() {
        final var field1 = mock(VariableElement.class);
        when(field1.getKind()).thenReturn(FIELD);
        when(field1.getModifiers()).thenReturn(Set.of(PRIVATE));
        final var field2 = createVariableElementFixtureForValueProviderServiceTest("classWithValueProviderField", DECLARED, false, FIELD);
        when(field2.getModifiers()).thenReturn(Set.of(PRIVATE, FINAL));
        final var field3 = mock(VariableElement.class);
        when(field3.getKind()).thenReturn(FIELD);
        when(field3.getModifiers()).thenReturn(Set.of(PRIVATE, FINAL));
        when(field3.getConstantValue()).thenReturn(true);

        final var type = createDeclaredTypeFixtureForValueProviderServiceTest("LombokClass", CLASS, field1, field2, field3);
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

        when(valueProviderMap.containsKey(anyString())).thenReturn(false);
        when(valueProviderMap.containsKey(eq("classWithValueProviderFieldType"))).thenReturn(true);
        when(valueProviderMap.get(eq("classWithValueProviderFieldType"))).thenReturn(f -> "classWithValueProviderFieldTypeValue");

        final var result = service.getValueFor(field);

        assertThat(result).isEqualTo("new LombokClass(classWithValueProviderFieldTypeValue)");
        verify(valueProviderMap, times(1)).containsKey("LombokClass");
        verify(valueProviderMap, times(1)).containsKey("classWithValueProviderFieldType");
        verify(valueProviderMap, times(1)).get("classWithValueProviderFieldType");
        verifyNoMoreInteractions(valueProviderMap);
    }

    @Test
    void getValueFor_whenFallbackForLombokNoArgsConstructor_returnConstructorValueWithNoParameters() {
        final var type = createDeclaredTypeFixtureForValueProviderServiceTest("LombokClass", CLASS);
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
        when(valueProviderMap.containsKey(anyString())).thenReturn(false);

        final var result = service.getValueFor(field);

        assertThat(result).isEqualTo("new LombokClass()");
        verify(valueProviderMap, times(1)).containsKey("LombokClass");
        verifyNoMoreInteractions(valueProviderMap);
    }

    @Test
    void getValueFor_whenFallbackDefinedConstructor_returnConstructorValueWithParameters() {
        final var parameter1 = createVariableElementFixtureForValueProviderServiceTest("integerParameter", DECLARED, false, null);
        final var parameter2 = createVariableElementFixtureForValueProviderServiceTest("booleanParameter", DECLARED, false, null);
        final var constructor1 = createExecutableElementFixture(CONSTRUCTOR, PUBLIC);
        when(constructor1.getParameters()).thenReturn((List) List.of(parameter1, parameter2));
        final var constructor2 = createExecutableElementFixture(CONSTRUCTOR, PUBLIC);
        when(constructor2.getParameters()).thenReturn((List) List.of(parameter1));

        final var type = createDeclaredTypeFixtureForValueProviderServiceTest("LombokClass", CLASS, constructor1, constructor2);
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

        when(valueProviderMap.containsKey(anyString())).thenReturn(false);
        when(valueProviderMap.containsKey(eq("integerParameterType"))).thenReturn(true);
        when(valueProviderMap.containsKey(eq("booleanParameterType"))).thenReturn(true);
        when(valueProviderMap.get(eq("integerParameterType"))).thenReturn(field -> "10");
        when(valueProviderMap.get(eq("booleanParameterType"))).thenReturn(field -> "true");

        final var result = service.getValueFor(field);

        assertThat(result).isEqualTo("new LombokClass(10, true)");
        verify(valueProviderMap, times(1)).containsKey("integerParameterType");
        verify(valueProviderMap, times(1)).containsKey("booleanParameterType");
        verify(valueProviderMap, times(1)).get("integerParameterType");
        verify(valueProviderMap, times(1)).get("booleanParameterType");
        verify(valueProviderMap, times(1)).containsKey("LombokClass");
        verifyNoMoreInteractions(valueProviderMap);
    }

    @Test
    void getValueFor_whenFallbackDefinedBuilderMethod_returnBuilderValueWithAllAvailableSetters() {
        final var classBuilderType = createDeclaredTypeFixtureForValueProviderServiceTest("LombokClass.LombokClassBuilder", CLASS, false);
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
        final var classType = createDeclaredTypeFixtureForValueProviderServiceTest("LombokClass", CLASS, builderMethod, field1, field2, field3);

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

        when(valueProviderMap.containsKey(anyString())).thenReturn(false);
        when(valueProviderMap.containsKey(eq("integerFieldType"))).thenReturn(true);
        when(valueProviderMap.containsKey(eq("booleanFieldType"))).thenReturn(true);
        when(valueProviderMap.get(eq("integerFieldType"))).thenReturn(field -> "10");
        when(valueProviderMap.get(eq("booleanFieldType"))).thenReturn(field -> "true");

        final var result = service.getValueFor(field);

        assertThat(result).isEqualTo("LombokClass.builder().setIntegerField(10).setBooleanField(true).build()");
        verify(valueProviderMap, times(1)).containsKey("integerFieldType");
        verify(valueProviderMap, times(1)).containsKey("booleanFieldType");
        verify(valueProviderMap, times(1)).get("integerFieldType");
        verify(valueProviderMap, times(1)).get("booleanFieldType");
        verify(valueProviderMap, times(1)).containsKey("LombokClass");
        verifyNoMoreInteractions(valueProviderMap);
    }

    @Test
    void getValueFor_whenFallbackDefinedBuilderMethodButNoBuilderFound_returnDefaultValue() {
        final var field1 = mock(VariableElement.class);
        final var field2 = mock(VariableElement.class);
        final var classType = createDeclaredTypeFixtureForValueProviderServiceTest("LombokClass", CLASS, field1, field2);

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

        when(valueProviderMap.containsKey(anyString())).thenReturn(false);

        final var result = service.getValueFor(field);

        assertThat(result).isEqualTo(DEFAULT_VALUE);
        verify(valueProviderMap, times(1)).containsKey("LombokClass");
        verifyNoMoreInteractions(valueProviderMap);
    }

    @Test
    void getValueFor_whenFallbackDefinedBuilderMethodButNoBuildMethodFound_returnDefaultValue() {
        final var classBuilderType = createDeclaredTypeFixtureForValueProviderServiceTest("LombokClass.LombokClassBuilder", CLASS, false);

        final var builderMethod = createExecutableElementFixture(METHOD, PUBLIC, STATIC);
        when(builderMethod.getReturnType()).thenReturn(classBuilderType);
        final var field1 = mock(VariableElement.class);
        final var field2 = mock(VariableElement.class);
        final var classType = createDeclaredTypeFixtureForValueProviderServiceTest("LombokClass", CLASS, builderMethod, field1, field2);

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

        when(valueProviderMap.containsKey(anyString())).thenReturn(false);

        final var result = service.getValueFor(field);

        assertThat(result).isEqualTo(DEFAULT_VALUE);
        verify(valueProviderMap, times(1)).containsKey("LombokClass");
        verifyNoMoreInteractions(valueProviderMap);
    }
}