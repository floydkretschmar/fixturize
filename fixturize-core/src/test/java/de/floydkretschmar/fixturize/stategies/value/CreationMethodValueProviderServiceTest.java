package de.floydkretschmar.fixturize.stategies.value;

import de.floydkretschmar.fixturize.TestFixtures;
import de.floydkretschmar.fixturize.annotations.FixtureConstant;
import de.floydkretschmar.fixturize.stategies.constants.ConstantMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.lang.model.element.Element;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreationMethodValueProviderServiceTest {
    @Mock
    private ValueProviderService defaultValueService;

    @Mock
    private ConstantMap constantMap;

    @Mock
    private Element field;

    private CreationMethodValueProviderService service;

    @BeforeEach
    void setup() {
        service = new CreationMethodValueProviderService(defaultValueService, constantMap);
    }

    @Test
    void resolveValuesForDefaultPlaceholders_whenCalled_returnResolvedValue() {
        when(defaultValueService.resolveValuesForDefaultPlaceholders(any())).thenReturn("resolvedString");

        final var value = service.resolveValuesForDefaultPlaceholders("valueStringWithPlaceholders");

        assertThat(value).isEqualTo("resolvedString");
        verify(defaultValueService, times(1)).resolveValuesForDefaultPlaceholders("valueStringWithPlaceholders");
        verifyNoMoreInteractions(defaultValueService);
        verifyNoInteractions(constantMap);
    }

    @Test
    void getValueFor_whenCalledForFieldWithAnnotation_returnConstantNameForAnnotation() {
        final var annotation = TestFixtures.createFixtureConstantFixture("definedConstantName", null);
        when(field.getAnnotationsByType(any())).thenReturn(new FixtureConstant[] { annotation });
        when(field.toString()).thenReturn("fieldName");
        when(constantMap.get(any(String.class))).thenReturn(TestFixtures.createConstantFixture("constant"));
        when(constantMap.containsKey(any(String.class))).thenReturn(true);

        final var value = service.getValueFor(field);

        assertThat(value).isEqualTo("constantName");
        verify(constantMap, times(1)).get("definedConstantName");
        verify(constantMap, times(1)).containsKey("definedConstantName");
        verifyNoMoreInteractions(constantMap);
        verifyNoInteractions(defaultValueService);
    }

    @Test
    void getValueFor_whenCalledForFieldWithoutAnnotation_returnConstantNameForFieldName() {
        when(field.getAnnotationsByType(any())).thenReturn(new FixtureConstant[] { });
        when(field.toString()).thenReturn("fieldName");
        when(constantMap.get(any(String.class))).thenReturn(TestFixtures.createConstantFixture("constant"));
        when(constantMap.containsKey(any(String.class))).thenReturn(true);

        final var value = service.getValueFor(field);

        assertThat(value).isEqualTo("constantName");
        verify(constantMap, times(1)).get("fieldName");
        verify(constantMap, times(1)).containsKey("fieldName");
        verifyNoMoreInteractions(constantMap);
        verifyNoInteractions(defaultValueService);
    }

    @Test
    void getValueFor_whenCalledForFieldWithNoEntryInMap_returnValueFromDefaultService() {
        final var annotation = TestFixtures.createFixtureConstantFixture("definedConstantName", null);
        when(field.getAnnotationsByType(any())).thenReturn(new FixtureConstant[] { annotation });
        when(field.toString()).thenReturn("fieldName");
        when(constantMap.containsKey(any(String.class))).thenReturn(false);
        when(defaultValueService.getValueFor(any())).thenReturn("valueFromDefaultService");

        final var value = service.getValueFor(field);

        assertThat(value).isEqualTo("valueFromDefaultService");
        verify(constantMap, times(1)).containsKey("definedConstantName");
        verify(defaultValueService, times(1)).getValueFor(field);
        verifyNoMoreInteractions(constantMap, defaultValueService);
    }
}