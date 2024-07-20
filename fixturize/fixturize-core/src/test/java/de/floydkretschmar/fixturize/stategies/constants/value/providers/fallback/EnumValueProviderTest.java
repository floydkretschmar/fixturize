package de.floydkretschmar.fixturize.stategies.constants.value.providers.fallback;

import de.floydkretschmar.fixturize.TestFixtures;
import de.floydkretschmar.fixturize.domain.Names;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;

import static de.floydkretschmar.fixturize.stategies.constants.value.providers.ValueProvider.DEFAULT_VALUE;
import static javax.lang.model.element.ElementKind.ENUM_CONSTANT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnumValueProviderTest {

    private EnumValueProvider valueProvider;

    private Names names;

    @Mock
    private VariableElement field;

    @BeforeEach
    void setup() {
        valueProvider = new EnumValueProvider();
        names = Names.from("some.test.Class");
    }


    @Test
    void provideValueAsString_whenCalledForEnum_returnCorrespondingValueString() {
        final var enumConstant = mock(Element.class);
        final var type = TestFixtures.createDeclaredTypeFixture(enumConstant);
        when(enumConstant.getKind()).thenReturn(ENUM_CONSTANT);
        when(enumConstant.toString()).thenReturn("CONSTANT_VALUE");
        when(field.asType()).thenReturn(type);

        final var result = valueProvider.provideValueAsString(field, names);

        assertThat(result).isEqualTo("some.test.Class.CONSTANT_VALUE");
    }

    @Test
    void provideValueAsString_whenCalledForEnumWithoutConstants_returnDefaultValue() {
        final var type = TestFixtures.createDeclaredTypeFixture();
        when(field.asType()).thenReturn(type);

        final var result = valueProvider.provideValueAsString(field, names);

        assertThat(result).isEqualTo(DEFAULT_VALUE);
    }

}