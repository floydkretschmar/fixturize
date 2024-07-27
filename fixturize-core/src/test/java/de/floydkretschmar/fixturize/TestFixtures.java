package de.floydkretschmar.fixturize;

import de.floydkretschmar.fixturize.annotations.FixtureBuilder;
import de.floydkretschmar.fixturize.annotations.FixtureBuilderSetter;
import de.floydkretschmar.fixturize.annotations.FixtureConstant;
import de.floydkretschmar.fixturize.annotations.FixtureConstructor;
import de.floydkretschmar.fixturize.domain.Constant;
import de.floydkretschmar.fixturize.domain.TypeMetadata;
import de.floydkretschmar.fixturize.stategies.constants.ConstantMap;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static javax.lang.model.element.ElementKind.FIELD;
import static javax.lang.model.type.TypeKind.DECLARED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestFixtures {
    public static final String RANDOM_UUID = "6b21f215-bf9e-445a-9dd2-5808a3a98d52";

    public static final String STRING_FIELD_NAME = "stringField";
    public static final String INT_FIELD_NAME = "intField";
    public static final String BOOLEAN_FIELD_NAME = "booleanField";
    public static final String UUID_FIELD_NAME = "uuidField";

    public static final Constant STRING_FIELD_DEFINITION = createConstantFixture(STRING_FIELD_NAME);
    public static final Constant INT_FIELD_DEFINITION = createConstantFixture(INT_FIELD_NAME);
    public static final Constant BOOLEAN_FIELD_DEFINITION = createConstantFixture(BOOLEAN_FIELD_NAME);
    public static final Constant UUID_FIELD_DEFINITION = createConstantFixture(UUID_FIELD_NAME);

    public static TypeMetadata createMetadataFixture() {
        return createMetadataFixture("Class");
    }

    public static TypeMetadata createMetadataFixture(String className) {
        return createMetadataFixtureBuilder(className, "").build();
    }

    public static Stream<Arguments> getMetadataParameters(String expectedOutcome, String expectedGenericOutcome) {
        final var genericField = createVariableElementFixture("secondField", true, true, FIELD);
        final var genericFieldType = createDeclaredTypeFixture();
        final var genericMetadata = TestFixtures.createMetadataFixtureBuilder("Class", "<String>")
                .genericTypeMap(Map.of(genericField.asType(), genericFieldType)).build();

        final var nonGenericField = createVariableElementFixture("secondField", true, true, FIELD);

        return Stream.of(
                Arguments.of(
                        TestFixtures.createMetadataFixture(),
                        nonGenericField,
                        nonGenericField,
                        expectedOutcome),
                Arguments.of(
                        genericMetadata,
                        genericField,
                        genericFieldType.asElement(),
                        expectedGenericOutcome)
        );
    }

    public static TypeMetadata.TypeMetadataBuilder createMetadataFixtureBuilder(String className, String genericPart) {
        return TypeMetadata.builder()
                .packageName("some.test")
                .qualifiedClassName("some.test.%s%s".formatted(className, genericPart))
                .simpleClassName("%s%s".formatted(className, genericPart))
                .genericPart(genericPart)
                .genericTypeMap(Map.of())
                .qualifiedFixtureClassName("some.test.%sFixture".formatted(className))
                .simpleClassNameWithoutGeneric(className)
                .qualifiedClassNameWithoutGeneric("some.test.%s".formatted(className));
    }

    public static Constant createConstantFixture(String fieldName) {
        return createConstantFixture(fieldName, "%sName".formatted(fieldName));
    }

    public static Constant createConstantFixture(String fieldName, String constantName) {
        return createConstantFixture(fieldName, constantName, "%sValue".formatted(fieldName));
    }

    public static Constant createConstantFixture(String fieldName, String constantName, String value) {
        return Constant.builder()
                .originalFieldName(fieldName)
                .name(constantName)
                .type("%sType".formatted(fieldName))
                .value(value).build();
    }

    public static FixtureConstant createFixtureConstantFixture(String name, String value) {
        final var annotation = mock(FixtureConstant.class);
        when(annotation.name()).thenReturn(name);
        when(annotation.value()).thenReturn(value);
        return annotation;
    }

    public static FixtureConstructor createFixtureConstructorFixture(String methodName, String... parameterNames) {
        final var annotation = mock(FixtureConstructor.class);
        if (Objects.nonNull(methodName))
            when(annotation.methodName()).thenReturn(methodName);
        if (parameterNames.length > 0)
            when(annotation.constructorParameters()).thenReturn(parameterNames);
        return annotation;
    }

    public static FixtureBuilderSetter createFixtureBuilderSetterFixture(String setterName, String value) {
        final var setter = mock(FixtureBuilderSetter.class);
        when(setter.setterName()).thenReturn(setterName);
        when(setter.value()).thenReturn(value);
        return setter;
    }

    public static FixtureBuilder createFixtureBuilderFixture(String methodName, String builderMethod, FixtureBuilderSetter... usedSetters) {
        final var builder = mock(FixtureBuilder.class);
        if (Objects.nonNull(methodName))
            when(builder.methodName()).thenReturn(methodName);
        if (Objects.nonNull(builderMethod))
            when(builder.builderMethod()).thenReturn(builderMethod);

        when(builder.usedSetters()).thenReturn(usedSetters);
        return builder;
    }

    public static TypeMirror createTypeMirrorFixture(TypeKind typeKind, String name) {
        final var typeMirror = mock(TypeMirror.class);
        when(typeMirror.getKind()).thenReturn(typeKind);
        when(typeMirror.toString()).thenReturn(name);
        return typeMirror;
    }

    public static TypeMirror createTypeMirrorFixture(String name) {
        final var typeMirror = mock(TypeMirror.class);
        when(typeMirror.toString()).thenReturn(name);
        return typeMirror;
    }

    public static TypeMirror createTypeMirrorFixture(TypeKind typeKind) {
        final var typeMirror = mock(TypeMirror.class);
        when(typeMirror.getKind()).thenReturn(typeKind);
        return typeMirror;
    }

    public static VariableElement createVariableElementFixture(String name, TypeMirror type) {
        return createVariableElementFixture(name, type, false, null, null);
    }

    public static <T extends Annotation> VariableElement createVariableElementFixture(String name, TypeMirror type, ElementKind elementKind, T... annotations) {
        return createVariableElementFixture(name, type, true, elementKind, annotations);
    }

    public static VariableElement createVariableElementFixture(String name, boolean mockVariableName, boolean mockVariableType, ElementKind elementKind) {
        return createVariableElementFixture(name, mockVariableType ? mock(TypeMirror.class) : null, mockVariableName, elementKind, null);
    }

    public static <T extends Annotation> VariableElement createVariableElementFixture(String name, T... annotations) {
        return createVariableElementFixture(name, createTypeMirrorFixture(DECLARED, "%sType".formatted(name)), true, FIELD, annotations);
    }

    private static <T extends Annotation> VariableElement createVariableElementFixture(String name, TypeMirror variableType, boolean mockVariableName, ElementKind elementKind, T[] annotations) {
        final var variableElement = mock(VariableElement.class);

        if (mockVariableName) {
            final var variableName = mock(Name.class);
            when(variableName.toString()).thenReturn(name);
            when(variableElement.getSimpleName()).thenReturn(variableName);
        }

        if (Objects.nonNull(variableType))
            when(variableElement.asType()).thenReturn(variableType);

        if (Objects.nonNull(elementKind))
            when(variableElement.getKind()).thenReturn(elementKind);

        if (Objects.nonNull(annotations))
            when(variableElement.getAnnotationsByType(any())).thenReturn(annotations);

        return variableElement;
    }

    public static <T extends Annotation> TypeElement createTypeElementFixture(String name, T... annotations) {
        final var typeElement = mock(TypeElement.class);
//        final var typeName = mock(Name.class);

//        when(typeName.toString()).thenReturn(name);

        when(typeElement.getAnnotationsByType(any())).thenReturn(annotations);
//        when(typeElement.getSimpleName()).thenReturn(typeName);
//        when(typeElement.getEnclosedElements()).thenReturn(List.of());

        return typeElement;
    }

    public static DeclaredType createDeclaredTypeFixture(String name, ElementKind elementKind, Element... enclosedElements) {
        return createDeclaredTypeFixtureCore(name, elementKind, enclosedElements);
    }

    public static DeclaredType createDeclaredTypeFixture(String name, ElementKind elementKind) {
        return createDeclaredTypeFixtureCore(name, elementKind, null);
    }

    public static DeclaredType createDeclaredTypeFixture(ElementKind elementKind) {
        return createDeclaredTypeFixtureCore(null, elementKind, null);
    }

    public static DeclaredType createDeclaredTypeFixture() {
        return createDeclaredTypeFixtureCore(null, null, null);
    }

    public static DeclaredType createDeclaredTypeFixture(String name) {
        return createDeclaredTypeFixtureCore(name, null, null);
    }

    public static DeclaredType createDeclaredTypeFixture(Element... enclosedElements) {
        return createDeclaredTypeFixture(null, null, enclosedElements);
    }

    public static DeclaredType createDeclaredTypeFixture(String name, Element... enclosedElements) {
        return createDeclaredTypeFixture(name, null, enclosedElements);
    }

    private static DeclaredType createDeclaredTypeFixtureCore(String name, ElementKind elementKind, Element[] enclosedElements) {
        final var declaredType = mock(DeclaredType.class);
        final var declaredElement = mock(TypeElement.class);

        when(declaredType.asElement()).thenReturn(declaredElement);

        if (Objects.nonNull(name))
            when(declaredType.toString()).thenReturn(name);

        if (Objects.nonNull(elementKind)) {
            when(declaredType.getKind()).thenReturn(DECLARED);
            when(declaredElement.getKind()).thenReturn(elementKind);
        }

        if (Objects.nonNull(enclosedElements))
            when(declaredElement.getEnclosedElements()).thenReturn((List) List.of(enclosedElements));

        return declaredType;
    }

    public static ExecutableElement createExecutableElementFixture(String name, ElementKind elementKind, DeclaredType returnType, Modifier... modifiers) {
        final var executableElement = createExecutableElementFixture(name, elementKind, returnType);
        when(executableElement.getModifiers()).thenReturn(Set.of(modifiers));
        return executableElement;
    }

    public static ExecutableElement createExecutableElementFixture(String name, ElementKind elementKind, DeclaredType returnType) {
        final var executableElement = createExecutableElementFixtureCore(elementKind, null);
        final var executableElementName = mock(Name.class);
        when(executableElementName.toString()).thenReturn(name);
        when(executableElement.getReturnType()).thenReturn(returnType);
        when(executableElement.getSimpleName()).thenReturn(executableElementName);
        return executableElement;
    }

    public static ExecutableElement createExecutableElementFixture(ElementKind elementKind, Modifier... modifiers) {
        return createExecutableElementFixtureCore(elementKind, modifiers);
    }

    private static ExecutableElement createExecutableElementFixtureCore(ElementKind elementKind, Modifier[] modifiers) {
        final var executableElement = mock(ExecutableElement.class);
        when(executableElement.getKind()).thenReturn(elementKind);
        if (Objects.nonNull(modifiers))
            when(executableElement.getModifiers()).thenReturn(Set.of(modifiers));
        return executableElement;
    }

    public static ConstantMap createConstantDefinitionMapMock() {
        final var constantMap = mock(ConstantMap.class);
        when(constantMap.getMatchingConstants(anyCollection())).thenAnswer(call -> {
            final var argument = call.<Collection<String>>getArgument(0);
            return argument.stream().map(name -> Map.entry(name, Optional.of(TestFixtures.createConstantFixture(name)))).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        });
        return constantMap;
    }
}
