package de.floydkretschmar.fixturize;

import org.junit.jupiter.api.Test;

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

class ReflectionUtilsTest {
    @Test
    void toLinkedMap_whenCalled_CollectsMapPreservingOrder() {
        final var result = Stream.of(
                Map.entry("key3", "value3"),
                Map.entry("key1", "value1"),
                Map.entry("key2", "value2")
        ).collect(ReflectionUtils.toLinkedMap(Map.Entry::getKey, Map.Entry::getValue));

        assertThat(result).hasSize(3);
        assertThat(result.values()).containsExactly("value3", "value1", "value2");
    }

    @Test
    void toLinkedMap_whenCalledWithDuplicateKey_ThrowsIllegalStateException() {
        final var stream = Stream.of(
                Map.entry("key3", "value3"),
                Map.entry("key3", "value1")
        );

        assertThrows(IllegalStateException.class, () -> stream.collect(ReflectionUtils
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

        final var result = ReflectionUtils.findMethodWithModifiersByReturnType(declaredType.asElement(), "ReturnType", PUBLIC);

        assertThat(result).isEqualTo(matchingMethod);
    }

    @Test
    void findMethodWithModifiersByReturnType_whenCalledWithoutMatchingMethod_ReturnsNull() {
        final var returnType = createDeclaredTypeFixture("ReturnType", CLASS);
        final var wrongReturnType = createDeclaredTypeFixture("WrongReturnType", CLASS);
        final var wrongModifierMethod = createExecutableElementFixture("wrongModifierMethod", METHOD, returnType, PRIVATE);
        final var wrongReturnTypeMethod = createExecutableElementFixture("wrongModifierMethod", METHOD, wrongReturnType, PUBLIC);
        final var declaredType = createDeclaredTypeFixture("Type", CLASS, wrongModifierMethod, wrongReturnTypeMethod);

        final var result = ReflectionUtils.findMethodWithModifiersByReturnType(declaredType.asElement(), "ReturnType", PUBLIC);

        assertThat(result).isEqualTo(null);
    }

    @Test
    void findSetterForFields_whenCalled_ReturnsOptinalMethodForEachProvidedField() {
        final var intFieldSetter = createExecutableElementFixture("setIntField", METHOD, createDeclaredTypeFixture("integer", CLASS), PUBLIC);
        final var booleanFieldSetter = createExecutableElementFixture("setBooleanField", METHOD, createDeclaredTypeFixture("boolean", CLASS), PRIVATE);
        final var uuidFieldSetter = createExecutableElementFixture("setUuidFieldWithNonStandardName", METHOD, createDeclaredTypeFixture("java.util.UUID", CLASS), PUBLIC);
        final var declaredType = createDeclaredTypeFixture("Type", CLASS, intFieldSetter, booleanFieldSetter, uuidFieldSetter);

        final var result = ReflectionUtils.findSetterForFields(declaredType.asElement(), List.of("intField", "booleanField", "uuidField"), PUBLIC).collect(Collectors.toSet());

        assertThat(result).hasSize(3);
        assertThat(result).contains(
                Map.entry("intField", Optional.of(intFieldSetter)),
                Map.entry("booleanField", Optional.empty()),
                Map.entry("uuidField", Optional.empty())
        );
    }
}