package de.floydkretschmar.fixturize;

import org.junit.jupiter.api.Test;

import javax.lang.model.element.VariableElement;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.floydkretschmar.fixturize.TestFixtures.createDeclaredTypeFixture;
import static de.floydkretschmar.fixturize.TestFixtures.createExecutableElementFixture;
import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.ElementKind.METHOD;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ElementUtilsTest {
    @Test
    void toLinkedMap_whenCalled_CollectsMapPreservingOrder() {
        final var result = Stream.of(
                Map.entry("key3", "value3"),
                Map.entry("key1", "value1"),
                Map.entry("key2", "value2")
        ).collect(ElementUtils.toLinkedMap(Map.Entry::getKey, Map.Entry::getValue));

        assertThat(result).hasSize(3);
        assertThat(result.values()).containsExactly("value3", "value1", "value2");
    }

    @Test
    void toLinkedMap_whenCalledWithDuplicateKey_ThrowsIllegalStateException() {
        final var stream = Stream.of(
                Map.entry("key3", "value3"),
                Map.entry("key3", "value1")
        );

        assertThrows(IllegalStateException.class, () -> stream.collect(ElementUtils
                .toLinkedMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    @Test
    void findMethodWithModifiersByReturnType_whenCalled_ReturnsAllMethodsWithModifiersThatMatchReturnType() {
        final var returnType = createDeclaredTypeFixture("ReturnType", CLASS);
        final var wrongReturnType = createDeclaredTypeFixture("WrongReturnType", CLASS);
        final var matchingMethod = createExecutableElementFixture("matchingMethod", METHOD, returnType, PUBLIC);
        final var wrongModifierMethod = createExecutableElementFixture("wrongModifierMethod", METHOD, returnType, PRIVATE);
        final var wrongReturnTypeMethod = createExecutableElementFixture("wrongModifierMethod", METHOD, wrongReturnType, PUBLIC);
        final var declaredType = createDeclaredTypeFixture("Type", CLASS, matchingMethod, wrongModifierMethod, wrongReturnTypeMethod);

        final var result = ElementUtils.findMethodWithModifiersByReturnType(declaredType.asElement(), "ReturnType", PUBLIC);

        assertThat(result).isEqualTo(matchingMethod);
    }

    @Test
    void findMethodWithModifiersByReturnType_whenCalledWithoutMatchingMethod_ReturnsNull() {
        final var returnType = createDeclaredTypeFixture("ReturnType", CLASS);
        final var wrongReturnType = createDeclaredTypeFixture("WrongReturnType", CLASS);
        final var wrongModifierMethod = createExecutableElementFixture("wrongModifierMethod", METHOD, returnType, PRIVATE);
        final var wrongReturnTypeMethod = createExecutableElementFixture("wrongModifierMethod", METHOD, wrongReturnType, PUBLIC);
        final var declaredType = createDeclaredTypeFixture("Type", CLASS, wrongModifierMethod, wrongReturnTypeMethod);

        final var result = ElementUtils.findMethodWithModifiersByReturnType(declaredType.asElement(), "ReturnType", PUBLIC);

        assertThat(result).isEqualTo(null);
    }

    @Test
    void findSetterForFields_whenCalled_ReturnsOptinalMethodForEachProvidedField() {
        final var returnType = createDeclaredTypeFixture("TestBuilder", CLASS);
        final var intFieldSetter = createExecutableElementFixture("setIntField", METHOD, returnType, PUBLIC);
        when(intFieldSetter.getParameters()).thenReturn((List) List.of(mock(VariableElement.class)));
        final var booleanFieldSetter = createExecutableElementFixture("setBooleanField", METHOD, returnType, PRIVATE);
        when(booleanFieldSetter.getParameters()).thenReturn((List) List.of(mock(VariableElement.class)));
        final var uuidFieldSetter = createExecutableElementFixture("setUuidFieldWithNonStandardName", METHOD, returnType, PUBLIC);
        when(uuidFieldSetter.getParameters()).thenReturn((List) List.of(mock(VariableElement.class)));
        final var fieldWithoutParameters = createExecutableElementFixture("setField", METHOD, returnType, PUBLIC);

        final var declaredType = createDeclaredTypeFixture("Type", CLASS, intFieldSetter, booleanFieldSetter, uuidFieldSetter, fieldWithoutParameters);

        final var result = ElementUtils.findSetterForFields(declaredType.asElement(), List.of("intField", "booleanField", "uuidField", "field"), returnType, PUBLIC).collect(Collectors.toSet());

        assertThat(result).hasSize(4);
        assertThat(result).contains(
                Map.entry("intField", Optional.of(intFieldSetter)),
                Map.entry("booleanField", Optional.empty()),
                Map.entry("field", Optional.empty()),
                Map.entry("uuidField", Optional.empty())
        );
    }
}