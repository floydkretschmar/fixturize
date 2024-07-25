package de.floydkretschmar.fixturize.stategies.constants.value;

import de.floydkretschmar.fixturize.TestFixtures;
import de.floydkretschmar.fixturize.domain.TypeMetadata;
import de.floydkretschmar.fixturize.stategies.constants.metadata.MetadataFactory;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.ValueProvider;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.ValueProviderFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.Map;

import static de.floydkretschmar.fixturize.TestFixtures.createDeclaredTypeFixture;
import static de.floydkretschmar.fixturize.TestFixtures.createTypeMirrorFixture;
import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.ElementKind.ENUM;
import static javax.lang.model.type.TypeKind.ARRAY;
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
    private ValueProvider classValueProvider;

    @Mock
    private ValueProvider enumValueProvider;

    @Mock
    private ValueProvider arrayValueProvider;

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
        when(valueProviderFactory.createValueProviders(anyMap(), any(), any())).thenReturn(valueProviderMap);
        when(valueProviderFactory.createClassValueProvider(any())).thenReturn(classValueProvider);
        when(valueProviderFactory.createArrayValueProvider()).thenReturn(arrayValueProvider);
        when(valueProviderFactory.createEnumValueProvider()).thenReturn(enumValueProvider);
        service = new ConstantValueProviderService(Map.of(), valueProviderFactory, elementUtils, typeUtils, metadataFactory);
    }

    @Test
    void getValueFor_whenCalledForDefinedType_returnCorrespondingValueString() {
        when(metadataFactory.createMetadataFrom(any())).thenAnswer(params ->
                TestFixtures.createMetadataFixture(params.getArgument(0).toString()));

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
        verifyNoInteractions(classValueProvider, enumValueProvider, arrayValueProvider);
    }

    @Test
    void getValueFor_whenCalledForDefinedTypeWithoutGenerics_returnCorrespondingValueString() {
        when(metadataFactory.createMetadataFrom(any())).thenReturn(
                TestFixtures.createMetadataFixtureBuilder("ClassName", "<T>").build());
        final var type = mock(TypeMirror.class);

        when(field.asType()).thenReturn(type);

        when(valueProviderMap.containsKey(eq("some.test.ClassName<T>"))).thenReturn(false);
        when(valueProviderMap.containsKey(eq("some.test.ClassName"))).thenReturn(true);
        when(valueProviderMap.get(any(String.class))).thenReturn((f, n) -> "value");

        final var result = service.getValueFor(field);

        assertThat(result).isEqualTo("value");
        verify(valueProviderMap, times(1)).containsKey("some.test.ClassName<T>");
        verify(valueProviderMap, times(1)).containsKey("some.test.ClassName");
        verify(valueProviderMap, times(1)).get("some.test.ClassName");
        verify(metadataFactory, times(1)).createMetadataFrom(type);
        verifyNoMoreInteractions(valueProviderMap, metadataFactory);
        verifyNoInteractions(classValueProvider, enumValueProvider, arrayValueProvider);
    }

    @Test
    void getValueFor_whenCalledForArrayType_returnArrayValue() {
        when(metadataFactory.createMetadataFrom(any())).thenAnswer(params ->
                TestFixtures.createMetadataFixture(params.getArgument(0).toString()));
        final var type = createTypeMirrorFixture(ARRAY, "ArrayType[]");
        when(field.asType()).thenReturn(type);

        when(valueProviderMap.containsKey(anyString())).thenReturn(false);
        when(arrayValueProvider.provideValueAsString(any(), any())).thenReturn("arrayValue");

        final var result = service.getValueFor(field);

        assertThat(result).isEqualTo("arrayValue");
        verify(valueProviderMap, times(2)).containsKey("some.test.ArrayType[]");
        verify(arrayValueProvider, times(1)).provideValueAsString(eq(field), any(TypeMetadata.class));
        verify(metadataFactory, times(1)).createMetadataFrom(type);
        verifyNoMoreInteractions(valueProviderMap, metadataFactory, arrayValueProvider);
        verifyNoInteractions(classValueProvider, enumValueProvider);
    }

    @Test
    void getValueFor_whenCalledForEnum_returnEnumValueProviderValueString() {
        when(metadataFactory.createMetadataFrom(any())).thenAnswer(params ->
                TestFixtures.createMetadataFixture(params.getArgument(0).toString()));
        final var type = createDeclaredTypeFixture("EnumType", ENUM);
        when(field.asType()).thenReturn(type);

        when(valueProviderMap.containsKey(anyString())).thenReturn(false);
        when(enumValueProvider.provideValueAsString(any(), any())).thenReturn("enumValueProviderValue");

        final var result = service.getValueFor(field);

        assertThat(result).isEqualTo("enumValueProviderValue");
        verify(valueProviderMap, times(2)).containsKey("some.test.EnumType");
        verify(enumValueProvider, times(1)).provideValueAsString(eq(field), any(TypeMetadata.class));
        verifyNoMoreInteractions(valueProviderMap, metadataFactory, enumValueProvider);
        verifyNoInteractions(classValueProvider, arrayValueProvider);
    }

    @Test
    void getValueFor_whenCalledForAnyOtherDeclaredClass_returnClassValueProviderValueString() {
        when(metadataFactory.createMetadataFrom(any())).thenAnswer(params ->
                TestFixtures.createMetadataFixture(params.getArgument(0).toString()));
        final var type = createDeclaredTypeFixture("ClassType", CLASS);
        when(field.asType()).thenReturn(type);

        when(valueProviderMap.containsKey(anyString())).thenReturn(false);
        when(classValueProvider.provideValueAsString(any(), any())).thenReturn("classValue");

        final var result = service.getValueFor(field);

        assertThat(result).isEqualTo("classValue");
        verify(valueProviderMap, times(2)).containsKey("some.test.ClassType");
        verify(classValueProvider, times(1)).provideValueAsString(eq(field), any(TypeMetadata.class));
        verifyNoMoreInteractions(valueProviderMap, metadataFactory, classValueProvider);
        verifyNoInteractions(enumValueProvider, arrayValueProvider);
    }


    @Test
    void resolveValuesForDefaultPlaceholders_whenCalled_shouldResolveWildcards() {
        when(metadataFactory.createMetadataFrom(any())).thenAnswer(params ->
                TestFixtures.createMetadataFixture(params.getArgument(0).toString()));
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
        verifyNoInteractions(classValueProvider, enumValueProvider, arrayValueProvider);
    }
}