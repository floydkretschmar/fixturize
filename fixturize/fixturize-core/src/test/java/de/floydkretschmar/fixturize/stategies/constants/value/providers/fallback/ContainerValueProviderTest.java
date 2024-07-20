package de.floydkretschmar.fixturize.stategies.constants.value.providers.fallback;

import de.floydkretschmar.fixturize.domain.Names;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.lang.model.element.VariableElement;

import static de.floydkretschmar.fixturize.TestFixtures.createTypeMirrorFixture;
import static javax.lang.model.type.TypeKind.ARRAY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContainerValueProviderTest {

    private ContainerValueProvider valueProvider;

    @Mock
    private VariableElement field;

    @BeforeEach
    void setup() {
        valueProvider = new ContainerValueProvider();
    }

    @Test
    void getValueFor_whenCalledForArray_returnCorrespondingValueString() {
        final var names = Names.from("some.test.Class[]");
        final var type = createTypeMirrorFixture(ARRAY);
        when(field.asType()).thenReturn(type);

        final var result = valueProvider.provideValueAsString(field, names);

        assertThat(result).isEqualTo("new some.test.Class[] {}");
    }

}