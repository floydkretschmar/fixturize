package de.floydkretschmar.fixturize;

import com.google.auto.service.AutoService;
import de.floydkretschmar.fixturize.annotations.Fixture;
import de.floydkretschmar.fixturize.annotations.FixtureValueProvider;
import de.floydkretschmar.fixturize.domain.Constant;
import de.floydkretschmar.fixturize.domain.TypeMetadata;
import de.floydkretschmar.fixturize.exceptions.FixtureCreationException;
import de.floydkretschmar.fixturize.stategies.constants.CamelCaseToScreamingSnakeCaseNamingStrategy;
import de.floydkretschmar.fixturize.stategies.constants.ConstantDefinitionMap;
import de.floydkretschmar.fixturize.stategies.constants.ConstantGenerationStrategy;
import de.floydkretschmar.fixturize.stategies.constants.metadata.MetadataFactory;
import de.floydkretschmar.fixturize.stategies.constants.metadata.TypeMetadataFactory;
import de.floydkretschmar.fixturize.stategies.constants.value.ConstantValueProviderService;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.DefaultValueProviderFactory;
import de.floydkretschmar.fixturize.stategies.creation.BuilderCreationMethodStrategy;
import de.floydkretschmar.fixturize.stategies.creation.ConstructorCreationMethodStrategy;
import de.floydkretschmar.fixturize.stategies.creation.CreationMethodGenerationStrategy;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.floydkretschmar.fixturize.FormattingConstants.WHITESPACE_4;
import static de.floydkretschmar.fixturize.FormattingConstants.WHITESPACE_8;

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
@SupportedSourceVersion(SourceVersion.RELEASE_21)
@AutoService(Processor.class)
public class FixtureProcessor extends AbstractProcessor {

    private Types typeUtils;

    private Elements elementUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.typeUtils = processingEnv.getTypeUtils();
        this.elementUtils = processingEnv.getElementUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
                this.processAnnotatedElement((TypeElement) element);
            }
        }
        return true;
    }

    private void processAnnotatedElement(TypeElement element) {
        final var metadataFactory = new TypeMetadataFactory(elementUtils);
        final var valueProviderService = initializeValueProviderService(element.getAnnotationsByType(FixtureValueProvider.class), metadataFactory);
        final var constantsNamingStrategy = new CamelCaseToScreamingSnakeCaseNamingStrategy();
        final var constantsGenerationStrategy = new ConstantGenerationStrategy(constantsNamingStrategy, valueProviderService);

        final var creationMethodStrategies = new ArrayList<CreationMethodGenerationStrategy>();
        creationMethodStrategies.add(new ConstructorCreationMethodStrategy());
        creationMethodStrategies.add(new BuilderCreationMethodStrategy());

        final var fixtureAnnotation = element.getAnnotation(Fixture.class);
        final var metadata = metadataFactory.createMetadataFrom(element.asType(), Arrays.stream(fixtureAnnotation.genericImplementations()).toList());

        try {
            final var fixtureFile = processingEnv.getFiler()
                    .createSourceFile(metadata.getQualifiedFixtureClassName());

            try (final var out = new PrintWriter(fixtureFile.openWriter())) {
                final var fixtureClassString = getFixtureClassAsString(element, metadata, constantsGenerationStrategy, creationMethodStrategies);
                out.print(fixtureClassString);
            }
        } catch (IOException e) {
            throw new FixtureCreationException("Failed to create source file %s for fixture.".formatted(metadata.getQualifiedClassName()));
        }
    }

    private ConstantValueProviderService initializeValueProviderService(FixtureValueProvider[] customFixtureProviders, MetadataFactory metadataFactory) {
        final var customValueProviders = Arrays.stream(customFixtureProviders)
                .collect(Collectors.toMap(
                        FixtureValueProvider::targetType,
                        annotation -> CustomValueProviderParser.parseValueProvider(annotation.valueProviderCallback())
                ));
        return new ConstantValueProviderService(customValueProviders, new DefaultValueProviderFactory(), elementUtils, typeUtils, metadataFactory);
    }

    private static String getCreationMethodsString(TypeElement element, List<CreationMethodGenerationStrategy> creationMethodStrategies, ConstantDefinitionMap constantMap, TypeMetadata metadata) {
        return creationMethodStrategies.stream()
                .flatMap(stategy -> stategy.generateCreationMethods(element, constantMap, metadata).stream())
                .map(method -> "%spublic static %s %s() {\n%sreturn %s;\n%s}".formatted(WHITESPACE_4, method.getReturnType(), method.getName(), WHITESPACE_8, method.getReturnValue(), WHITESPACE_4))
                .collect(Collectors.joining("\n\n"));
    }

    private static String getConstantsString(Stream<Constant> constants) {
        return constants
                .map(constant -> "%spublic static %s %s = %s;".formatted(WHITESPACE_4, constant.getType(), constant.getName(), constant.getValue()))
                .collect(Collectors.joining("\n"));
    }

    private static String getFixtureClassAsString(TypeElement element, TypeMetadata metadata, ConstantGenerationStrategy constantsGenerationStrategy, ArrayList<CreationMethodGenerationStrategy> creationMethodStrategies) {
        final var fixtureClassTemplate = """
        %spublic class %sFixture {
        %s
        
        %s
        }
        """;

        final var packageString = metadata.hasPackageName() ? "package %s;\n\n".formatted(metadata.getPackageName()) : "";

        final var constantMap = constantsGenerationStrategy.generateConstants(element, metadata);
        final var constantsString = getConstantsString(constantMap.values().stream());
        final var creationMethodsString = getCreationMethodsString(element, creationMethodStrategies, constantMap, metadata);

        return String.format(fixtureClassTemplate, packageString, metadata.getSimpleClassNameWithoutGeneric(), constantsString, creationMethodsString);
    }
}
