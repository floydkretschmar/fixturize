package de.floydkretschmar.fixturize.stategies.constants.value;

import de.floydkretschmar.fixturize.TestFixtures;
import de.floydkretschmar.fixturize.annotations.FixtureConstant;
import de.floydkretschmar.fixturize.domain.TypeMetadata;
import de.floydkretschmar.fixturize.stategies.constants.metadata.MetadataFactory;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.ValueProvider;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.ValueProviderFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.List;
import java.util.Map;

import static de.floydkretschmar.fixturize.TestFixtures.BOOLEAN_FIELD_DEFINITION;
import static de.floydkretschmar.fixturize.TestFixtures.BOOLEAN_FIELD_NAME;
import static de.floydkretschmar.fixturize.TestFixtures.createConstantFixture;
import static de.floydkretschmar.fixturize.TestFixtures.createFixtureConstantFixture;
import static de.floydkretschmar.fixturize.TestFixtures.createTypeMirrorFixture;
import static de.floydkretschmar.fixturize.TestFixtures.createVariableElementFixture;
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

        when(metadataFactory.createMetadataFrom(any())).thenAnswer(params -> TestFixtures.createMetadataFixture(params.getArgument(0).toString()));
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
        verify(metadataFactory, times(1)).createMetadataFrom(type);
        verifyNoMoreInteractions(valueProviderMap, metadataFactory);
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
        verify(containerValueProvider, times(1)).provideValueAsString(eq(field), any(TypeMetadata.class));
        verify(declaredTypeValueProvider, times(1)).provideValueAsString(eq(field), any(TypeMetadata.class));
        verify(metadataFactory, times(1)).createMetadataFrom(type);
        verifyNoMoreInteractions(valueProviderMap, declaredTypeValueProvider, containerValueProvider, metadataFactory);
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
        verify(containerValueProvider, times(1)).provideValueAsString(eq(field), any(TypeMetadata.class));
        verify(metadataFactory, times(1)).createMetadataFrom(type);
        verifyNoMoreInteractions(valueProviderMap, containerValueProvider, metadataFactory);
        verifyNoInteractions(declaredTypeValueProvider);
    }


    @Test
    void resolveValuesForDefaultPlaceholders_whenCalled_shouldResolveWildcards() {
        final var type = createTypeMirrorFixture("ClassTypeElement");
        final var resolvedElement = mock(TypeElement.class);
        when(resolvedElement.asType()).thenReturn(type);
        when(elementUtils.getTypeElement(any())).thenReturn(resolvedElement);

        when(valueProviderMap.containsKey(any(String.class))).thenReturn(true);
        when(valueProviderMap.get(any(String.class))).thenReturn((f, n) -> "%sValue".formatted(n.getSimpleClassName()));

        final var result = service.resolveValuesForDefaultPlaceholders("Non-dynamic part ${ClassTypeElement}");

        assertThat(result).isEqualTo("Non-dynamic part ClassTypeElementValue");
        verify(metadataFactory, times(1)).createMetadataFrom(type);
        verify(valueProviderMap, times(1)).containsKey("some.test.ClassTypeElement");
        verifyNoMoreInteractions(valueProviderMap, metadataFactory);
        verifyNoInteractions(declaredTypeValueProvider, containerValueProvider);
    }
}