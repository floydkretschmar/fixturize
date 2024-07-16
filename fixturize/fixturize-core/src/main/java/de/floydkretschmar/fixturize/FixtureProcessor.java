package de.floydkretschmar.fixturize;

import com.google.auto.service.AutoService;
import com.google.common.base.Enums;
import de.floydkretschmar.fixturize.annotations.FixtureValueProvider;
import de.floydkretschmar.fixturize.domain.Constant;
import de.floydkretschmar.fixturize.domain.Names;
import de.floydkretschmar.fixturize.exceptions.FixtureCreationException;
import de.floydkretschmar.fixturize.stategies.constants.CamelCaseToScreamingSnakeCaseNamingStrategy;
import de.floydkretschmar.fixturize.stategies.constants.ConstantDefinitionMap;
import de.floydkretschmar.fixturize.stategies.constants.ConstantGenerationStrategy;
import de.floydkretschmar.fixturize.stategies.constants.value.ConstantValueProviderService;
import de.floydkretschmar.fixturize.stategies.constants.value.map.ClassValueProviderMap;
import de.floydkretschmar.fixturize.stategies.constants.value.map.ElementKindValueProviderMap;
import de.floydkretschmar.fixturize.stategies.constants.value.map.TypeKindValueProviderMap;
import de.floydkretschmar.fixturize.stategies.creation.BuilderCreationMethodStrategy;
import de.floydkretschmar.fixturize.stategies.creation.ConstructorCreationMethodStrategy;
import de.floydkretschmar.fixturize.stategies.creation.CreationMethodGenerationStrategy;
import de.floydkretschmar.fixturize.stategies.creation.UpperCamelCaseAndNamingStrategy;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.floydkretschmar.fixturize.FormattingUtils.WHITESPACE_4;
import static de.floydkretschmar.fixturize.FormattingUtils.WHITESPACE_8;

/**
 * Processes all classes annotated with {@link de.floydkretschmar.fixturize.annotations.Fixture} and tries to generate
 * a corresponding fixture class for all of these classes. A fixture class generally has the following format:
 * <br><br>
 * public class <b>className</b>Fixture { <br>
 * <b>constant1</b><br>
 * ...<br>
 * <b>constantM</b><br>
 * <br>
 * <b>creationMethod1</b><br>
 * ...<br>
 * <b>creationMethod2</b><br>
 * }<br>
 * <br>
 * For the exact format of each <b>constant</b> and <b>creationMethod</b> please reference {@link ConstantGenerationStrategy}
 * and {@link CreationMethodGenerationStrategy} respectively.
 */
@SupportedAnnotationTypes("de.floydkretschmar.fixturize.annotations.Fixture")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor.class)
public class FixtureProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        annotations.forEach(annnotation -> roundEnv.getElementsAnnotatedWith(annnotation).forEach(element -> this.processAnnotatedElement((TypeElement)element)));
        return true;
    }

    private void processAnnotatedElement(TypeElement element) {
        final var valueProviderService = initializeValueProviderService(element.getAnnotationsByType(FixtureValueProvider.class));
        final var constantsNamingStrategy = new CamelCaseToScreamingSnakeCaseNamingStrategy();
        final var constantsGenerationStrategy = new ConstantGenerationStrategy(constantsNamingStrategy, valueProviderService);

        final var methodNamingStrategy = new UpperCamelCaseAndNamingStrategy();
        final var creationMethodStrategies = new ArrayList<CreationMethodGenerationStrategy>();
        creationMethodStrategies.add(new ConstructorCreationMethodStrategy(methodNamingStrategy));
        creationMethodStrategies.add(new BuilderCreationMethodStrategy(methodNamingStrategy));

        final var names = getNames(element);

        try {
            final var fixtureFile = processingEnv.getFiler()
                    .createSourceFile(names.getQualifiedFixtureClassName());

            try (final var out = new PrintWriter(fixtureFile.openWriter())) {
                final var fixtureClassString = getFixtureClassAsString(element, names, constantsGenerationStrategy, creationMethodStrategies);
                out.print(fixtureClassString);
            }
        } catch (IOException e) {
            throw new FixtureCreationException("Failed to create source file %s for fixture.".formatted(names.getQualifiedClassName()));
        }
    }

    private static ConstantValueProviderService initializeValueProviderService(FixtureValueProvider[] customFixtureProviders) {
        final var customTypeKindValueProviders = Arrays.stream(customFixtureProviders)
                .filter(provider -> Enums.getIfPresent(TypeKind.class, provider.targetType()).isPresent())
                .collect(Collectors.toMap(
                        provider -> TypeKind.valueOf(provider.targetType()),
                        annotation -> CustomValueProviderParser.parseValueProvider(annotation.valueProviderCallback())));
        final var customElementKindValueProviders = Arrays.stream(customFixtureProviders)
                .filter(provider -> Enums.getIfPresent(ElementKind.class, provider.targetType()).isPresent())
                .collect(Collectors.toMap(
                        provider -> ElementKind.valueOf(provider.targetType()),
                        annotation -> CustomValueProviderParser.parseValueProvider(annotation.valueProviderCallback())));
        final var customClassValueProviders = Arrays.stream(customFixtureProviders)
                .filter(provider -> !Enums.getIfPresent(TypeKind.class, provider.targetType()).isPresent() &&
                        !Enums.getIfPresent(ElementKind.class, provider.targetType()).isPresent())
                .collect(Collectors.toMap(
                        FixtureValueProvider::targetType,
                        annotation -> CustomValueProviderParser.parseValueProvider(annotation.valueProviderCallback())
                ));

        return new ConstantValueProviderService(
                new TypeKindValueProviderMap(customTypeKindValueProviders),
                new ElementKindValueProviderMap(customElementKindValueProviders),
                new ClassValueProviderMap(customClassValueProviders));
    }

    private Names getNames(TypeElement element) {
        final var qualifiedClassName = element.getQualifiedName().toString();
        final var lastDot = qualifiedClassName.lastIndexOf('.');
        var packageName = "";
        if (lastDot > 0) {
            packageName = qualifiedClassName.substring(0, lastDot);
        }
        final var simpleClassName = qualifiedClassName.substring(lastDot + 1);
        final var qualifiedFixtureClassName = qualifiedClassName + "Fixture";

        return Names.builder()
                .qualifiedClassName(qualifiedClassName)
                .simpleClassName(simpleClassName)
                .packageName(packageName)
                .qualifiedFixtureClassName(qualifiedFixtureClassName).build();
    }

    private static String getCreationMethodsString(TypeElement element, List<CreationMethodGenerationStrategy> creationMethodStrategies, ConstantDefinitionMap constantMap) {
        return creationMethodStrategies.stream()
                .flatMap(stategy -> stategy.generateCreationMethods(element, constantMap).stream())
                .map(method -> "%spublic static %s %s() {\n%sreturn %s;\n%s}".formatted(WHITESPACE_4, method.getReturnType(), method.getName(), WHITESPACE_8, method.getReturnValue(), WHITESPACE_4))
                .collect(Collectors.joining("\n\n"));
    }

    private static String getConstantsString(Stream<Constant> constants) {
        return constants
                .map(constant -> "%spublic static %s %s = %s;".formatted(WHITESPACE_4, constant.getType(), constant.getName(), constant.getValue()))
                .collect(Collectors.joining("\n"));
    }

    private static String getFixtureClassAsString(TypeElement element, Names names, ConstantGenerationStrategy constantsGenerationStrategy, ArrayList<CreationMethodGenerationStrategy> creationMethodStrategies) {
        final var fixtureClassTemplate = """
        %spublic class %sFixture {
        %s
        
        %s
        }
        """;

        final var packageString = names.hasPackageName() ? "package %s;\n\n".formatted(names.getPackageName()) : "";

        final var constantMap = constantsGenerationStrategy.generateConstants(element);
        final var constantsString = getConstantsString(constantMap.values().stream());
        final var creationMethodsString = getCreationMethodsString(element, creationMethodStrategies, constantMap);

        return String.format(fixtureClassTemplate, packageString, names.getSimpleClassName(), constantsString, creationMethodsString);
    }
}
