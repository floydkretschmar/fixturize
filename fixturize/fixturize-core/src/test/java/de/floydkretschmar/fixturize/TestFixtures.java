package de.floydkretschmar.fixturize;

import de.floydkretschmar.fixturize.annotations.FixtureBuilder;
import de.floydkretschmar.fixturize.annotations.FixtureConstant;
import de.floydkretschmar.fixturize.annotations.FixtureConstructor;
import de.floydkretschmar.fixturize.domain.Constant;
import de.floydkretschmar.fixturize.stategies.constants.ConstantDefinitionMap;
import de.floydkretschmar.fixturize.stategies.constants.ConstantsNamingStrategy;
import de.floydkretschmar.fixturize.stategies.constants.value.ValueProviderService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static javax.lang.model.element.ElementKind.FIELD;
import static javax.lang.model.type.TypeKind.DECLARED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyString;
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

    public static FixtureBuilder createFixtureBuilderFixture(String methodName, String builderMethod, String... usedSetters) {
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

    public static TypeMirror createTypeMirrorFixture(TypeKind typeKind) {
        final var typeMirror = mock(TypeMirror.class);
        when(typeMirror.getKind()).thenReturn(typeKind);
        return typeMirror;
    }

    public static VariableElement createVariableElementFixtureForValueProviderServiceTest(String name, TypeKind typeKind, boolean mockVariableName, ElementKind elementKind) {
        final var type = typeKind == DECLARED ? createTypeMirrorFixture(DECLARED, "%sType".formatted(name)) : createTypeMirrorFixture(typeKind);
        return createVariableElementFixture(name, type, mockVariableName, elementKind, null);
    }

    public static <T extends Annotation> VariableElement createVariableElementFixtureForConstantGenerationStrategyTest(String name, T... annotations) {
        return createVariableElementFixture(name, createTypeMirrorFixture(DECLARED, "%sType".formatted(name)), true, FIELD, annotations);
    }

    private static <T extends Annotation> VariableElement createVariableElementFixture(String name, TypeMirror variableType, boolean mockVariableName, ElementKind elementKind, T[] annotations) {
        final var variableElement = mock(VariableElement.class);

        if (mockVariableName) {
            final var variableName = mock(Name.class);
            when(variableName.toString()).thenReturn(name);
            when(variableElement.getSimpleName()).thenReturn(variableName);
        }

        when(variableElement.asType()).thenReturn(variableType);

        if (Objects.nonNull(elementKind))
            when(variableElement.getKind()).thenReturn(elementKind);

        if (Objects.nonNull(annotations))
            when(variableElement.getAnnotationsByType(any())).thenReturn(annotations);

        return variableElement;
    }

    public static <T extends Annotation> TypeElement createTypeElementFixture(String name, T... annotations) {
        final var typeElement = mock(TypeElement.class);
        final var typeName = mock(Name.class);

        when(typeName.toString()).thenReturn(name);

        when(typeElement.getAnnotationsByType(any())).thenReturn(annotations);
        when(typeElement.getSimpleName()).thenReturn(typeName);
        when(typeElement.getEnclosedElements()).thenReturn(List.of());

        return typeElement;
    }

    public static DeclaredType createDeclaredTypeFixture(String name, ElementKind elementKind, Element... enclosedElements) {
        return createDeclaredTypeFixture(name, elementKind, false, true, enclosedElements);
    }

    public static DeclaredType createDeclaredTypeFixtureForValueProviderServiceTest(String name, ElementKind elementKind) {
        return createDeclaredTypeFixtureForValueProviderServiceTest(name, elementKind, true, true);
    }

    public static DeclaredType createDeclaredTypeFixtureForValueProviderServiceTest(String name, ElementKind elementKind, boolean mockQualifiedName, boolean mockKinds) {
        return createDeclaredTypeFixture(name, elementKind, mockQualifiedName, mockKinds, null);
    }

    public static DeclaredType createDeclaredTypeFixtureForValueProviderServiceTest(String name, ElementKind elementKind, Element... enclosedElements) {
        return createDeclaredTypeFixture(name, elementKind, true, true, enclosedElements);
    }

    private static DeclaredType createDeclaredTypeFixture(String name, ElementKind elementKind, boolean mockQualifiedName, boolean mockKinds, Element[] enclosedElements) {
        final var declaredType = mock(DeclaredType.class);
        final var declaredElement = mock(TypeElement.class);

        when(declaredType.asElement()).thenReturn(declaredElement);
        when(declaredType.toString()).thenReturn(name);

        if (mockKinds) {
            when(declaredType.getKind()).thenReturn(DECLARED);
            when(declaredElement.getKind()).thenReturn(elementKind);
        }

        if (mockQualifiedName) {
            final var declaredElementName = mock(Name.class);
            when(declaredElementName.toString()).thenReturn(name);
            when(declaredElement.getQualifiedName()).thenReturn(declaredElementName);
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
        final var executableElement = createExecutableElementFixture(elementKind, null);
        final var executableElementName = mock(Name.class);
        when(executableElementName.toString()).thenReturn(name);
        when(executableElement.getReturnType()).thenReturn(returnType);
        when(executableElement.getSimpleName()).thenReturn(executableElementName);
        return executableElement;
    }

    public static ExecutableElement createExecutableElementFixture(ElementKind elementKind, Modifier... modifiers) {
        final var executableElement = mock(ExecutableElement.class);
        when(executableElement.getKind()).thenReturn(elementKind);
        if (Objects.nonNull(modifiers))
            when(executableElement.getModifiers()).thenReturn(Set.of(modifiers));
        return executableElement;
    }

    public static ConstantsNamingStrategy createNamingStrategyMock() {
        final var namingStrategy = mock(ConstantsNamingStrategy.class);
        when(namingStrategy.createConstantName(anyString())).thenAnswer(param -> "%sName".formatted(param.getArguments()[0]));
        return namingStrategy;
    }

    public static ValueProviderService createValueProviderServiceMock() {
        final var valueService = mock(ValueProviderService.class);
        when(valueService.getValueFor(any())).thenAnswer(param -> {
            final var field = (VariableElement) param.getArguments()[0];
            return "%sValue".formatted(field.getSimpleName().toString());
        });
        return valueService;
    }

    public static ConstantDefinitionMap createConstantDefinitionMapMock() {
        final var constantMap = mock(ConstantDefinitionMap.class);
        when(constantMap.getMatchingConstants(anyCollection())).thenAnswer(call -> {
            final var argument = call.<Collection<String>>getArgument(0);
            return argument.stream().map(TestFixtures::createConstantFixture).toList();
        });
        return constantMap;
    }
}
