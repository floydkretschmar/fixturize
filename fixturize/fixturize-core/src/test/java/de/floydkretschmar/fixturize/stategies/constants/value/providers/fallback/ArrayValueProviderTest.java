package de.floydkretschmar.fixturize.stategies.constants.value.providers.fallback;

import de.floydkretschmar.fixturize.TestFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.lang.model.element.VariableElement;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ArrayValueProviderTest {

    private ArrayValueProvider valueProvider;

    @Mock
    private VariableElement field;

    @BeforeEach
    void setup() {
        valueProvider = new ArrayValueProvider();
    }

    @Test
    void getValueFor_whenCalledForArray_returnCorrespondingValueString() {
        final var metadata = TestFixtures.createMetadataFixture("Class[]");
        final var result = valueProvider.provideValueAsString(field, metadata);

        assertThat(result).isEqualTo("new %s {}".formatted(metadata.getQualifiedClassNameWithoutGeneric()));
    }

}