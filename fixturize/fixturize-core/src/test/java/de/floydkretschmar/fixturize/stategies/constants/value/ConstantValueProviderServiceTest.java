package de.floydkretschmar.fixturize.stategies.constants.value;

import de.floydkretschmar.fixturize.TestFixtures;
import de.floydkretschmar.fixturize.domain.Metadata;
import de.floydkretschmar.fixturize.stategies.constants.metadata.MetadataFactory;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.ValueProvider;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.ValueProviderFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.Map;

import static de.floydkretschmar.fixturize.TestFixtures.createTypeMirrorFixture;
import static de.floydkretschmar.fixturize.stategies.constants.value.providers.ValueProvider.DEFAULT_VALUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConstantValueProviderServiceTest {

    @Mock
    private ValueProviderMap valueProviderMap;

    @Mock
    private VariableElement field;

    @Mock
    private ValueProvider declaredTypeValueProvider;

    @Mock
    private ValueProvider containerValueProvider;

    @Mock
    private Elements elementUtils;

    @Mock
    private Types typeUtils;

    @Mock
    private MetadataFactory metadataFactory;

    private ConstantValueProviderService service;

    @BeforeEach
    void setup() {
        final var valueProviderFactory = mock(ValueProviderFactory.class);
        when(valueProviderFactory.createValueProviders(anyMap())).thenReturn(valueProviderMap);
        when(valueProviderFactory.createDeclaredTypeValueProvider(any())).thenReturn(declaredTypeValueProvider);
        when(valueProviderFactory.createContainerValueProvider(any(), any(), any())).thenReturn(containerValueProvider);
        service = new ConstantValueProviderService(Map.of(), valueProviderFactory, elementUtils, typeUtils, metadataFactory);

        when(metadataFactory.createMetadataFrom(any())).thenAnswer(params -> TestFixtures.createMetadataFixture(field.asType().toString()));
    }

    @Test
    void getValueFor_whenCalledForDefinedType_returnCorrespondingValueString() {
        final var type = createTypeMirrorFixture("ClassName");

        when(field.asType()).thenReturn(type);

        when(valueProviderMap.containsKey(any(String.class))).thenReturn(true);
        when(valueProviderMap.get(any(String.class))).thenReturn((f, n) -> "value");

        final var result = service.getValueFor(field);

        assertThat(result).isEqualTo("value");
        verify(valueProviderMap, times(1)).containsKey("some.test.ClassName");
        verify(valueProviderMap, times(1)).get("some.test.ClassName");
        verifyNoMoreInteractions(valueProviderMap);
        verifyNoInteractions(declaredTypeValueProvider, containerValueProvider);
    }

    @Test
    void getValueFor_whenCalledForDeclaredType_returnDeclaredTypeValueString() {
        final var type = mock(DeclaredType.class);
        when(type.toString()).thenReturn("EnumType");

        when(field.asType()).thenReturn(type);

        when(valueProviderMap.containsKey(anyString())).thenReturn(false);
        when(containerValueProvider.provideValueAsString(any(), any())).thenReturn(DEFAULT_VALUE);
        when(declaredTypeValueProvider.provideValueAsString(any(), any())).thenReturn("declaredTypeProviderValue");

        final var result = service.getValueFor(field);

        assertThat(result).isEqualTo("declaredTypeProviderValue");
        verify(valueProviderMap, times(1)).containsKey("some.test.EnumType");
        verify(containerValueProvider, times(1)).provideValueAsString(eq(field), any(Metadata.class));
        verify(declaredTypeValueProvider, times(1)).provideValueAsString(eq(field), any(Metadata.class));
        verifyNoMoreInteractions(valueProviderMap, declaredTypeValueProvider, containerValueProvider);
    }

    @Test
    void getValueFor_whenCalledForContainerType_returnContainerValueProviderValueString() {
        final var type = createTypeMirrorFixture("ContainerType");
        when(field.asType()).thenReturn(type);

        when(valueProviderMap.containsKey(anyString())).thenReturn(false);
        when(containerValueProvider.provideValueAsString(any(), any())).thenReturn("containerValueProviderValue");

        final var result = service.getValueFor(field);

        assertThat(result).isEqualTo("containerValueProviderValue");
        verify(valueProviderMap, times(1)).containsKey("some.test.ContainerType");
        verify(containerValueProvider, times(1)).provideValueAsString(eq(field), any(Metadata.class));
        verifyNoMoreInteractions(valueProviderMap, containerValueProvider);
        verifyNoInteractions(declaredTypeValueProvider);
    }
}