package de.floydkretschmar.fixturize.stategies.constants.value.providers.custom;

import de.floydkretschmar.fixturize.TestFixtures;
import de.floydkretschmar.fixturize.domain.TypeMetadata;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.fallback.EnumValueProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;

import static de.floydkretschmar.fixturize.TestFixtures.createDeclaredTypeFixture;
import static de.floydkretschmar.fixturize.TestFixtures.createMetadataFixture;
import static de.floydkretschmar.fixturize.TestFixtures.createTypeMirrorFixture;
import static de.floydkretschmar.fixturize.TestFixtures.createVariableElementFixture;
import static de.floydkretschmar.fixturize.stategies.constants.value.providers.ValueProvider.DEFAULT_VALUE;
import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.ElementKind.ENUM;
import static javax.lang.model.element.ElementKind.ENUM_CONSTANT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnumValueProviderTest {

    private EnumValueProvider valueProvider;

    private TypeMetadata metadata;

    @Mock
    private VariableElement field;

    @BeforeEach
    void setup() {
        valueProvider = new EnumValueProvider();
        metadata = TestFixtures.createMetadataFixture();
    }

    @Test
    void provideValueAsString_whenCalledForEnum_returnCorrespondingValueString() {
        final var enumConstant = mock(Element.class);
        final var type = TestFixtures.createDeclaredTypeFixture(enumConstant);
        when(enumConstant.getKind()).thenReturn(ENUM_CONSTANT);
        when(enumConstant.toString()).thenReturn("CONSTANT_VALUE");
        when(field.asType()).thenReturn(type);

        final var result = valueProvider.provideValueAsString(field, metadata);

        assertThat(result).isEqualTo("some.test.Class.CONSTANT_VALUE");
    }

    @Test
    void provideValueAsString_whenCalledForEnumWithoutConstants_returnDefaultValue() {
        final var type = TestFixtures.createDeclaredTypeFixture();
        when(field.asType()).thenReturn(type);

        final var result = valueProvider.provideValueAsString(field, metadata);

        assertThat(result).isEqualTo(DEFAULT_VALUE);
    }

    @Test
    void canProvideFallback_whenCalledForDeclaredTypeEnum_returnTrue() {
        final var element = createVariableElementFixture(null, createDeclaredTypeFixture(ENUM));

        final var result = valueProvider.canProvideFallback(element, createMetadataFixture());

        assertThat(result).isTrue();
    }

    @Test
    void canProvideFallback_whenCalledForDeclaredTypeNotEnum_returnFalse() {
        final var element = createVariableElementFixture(null, createDeclaredTypeFixture(CLASS));

        final var result = valueProvider.canProvideFallback(element, createMetadataFixture());

        assertThat(result).isFalse();
    }

    @Test
    void canProvideFallback_whenCalledForOtherThanDeclaredType_returnFalse() {
        final var element = createVariableElementFixture(null, createTypeMirrorFixture(TypeKind.INT));

        final var result = valueProvider.canProvideFallback(element, createMetadataFixture());

        assertThat(result).isFalse();
    }

}