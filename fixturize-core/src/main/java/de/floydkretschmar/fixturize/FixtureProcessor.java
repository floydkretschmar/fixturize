package de.floydkretschmar.fixturize;

import com.google.auto.service.AutoService;
import de.floydkretschmar.fixturize.annotations.Fixture;
import de.floydkretschmar.fixturize.annotations.FixtureValueProvider;
import de.floydkretschmar.fixturize.exceptions.FixtureCreationException;
import de.floydkretschmar.fixturize.stategies.constants.ConstantGenerationStrategy;
import de.floydkretschmar.fixturize.stategies.constants.ConstantMap;
import de.floydkretschmar.fixturize.stategies.constants.metadata.MetadataFactory;
import de.floydkretschmar.fixturize.stategies.constants.metadata.TypeMetadataFactory;
import de.floydkretschmar.fixturize.stategies.constants.naming.ConstantNamingStrategy;
import de.floydkretschmar.fixturize.stategies.constants.value.ConstantValueProviderService;
import de.floydkretschmar.fixturize.stategies.constants.value.CreationMethodValueProviderService;
import de.floydkretschmar.fixturize.stategies.constants.value.ValueProviderService;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.ConstantValueProviderFactory;
import de.floydkretschmar.fixturize.stategies.creation.BuilderCreationMethodStrategy;
import de.floydkretschmar.fixturize.stategies.creation.ConstructorCreationMethodStrategy;
import de.floydkretschmar.fixturize.stategies.creation.CreationMethodGenerationStrategy;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;

import javax.annotation.processing.*;
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
@AutoService(Processor.class)
public class FixtureProcessor extends AbstractProcessor {
    private static final String WHITESPACE_4 = " ".repeat(4);
    private static final String WHITESPACE_8 = " ".repeat(8);

    private Types typeUtils;

    private Elements elementUtils;

    private CustomValueProviderParser valueProviderParser;

    private ConstantValueProviderFactory valueProviderFactory;
    private MetadataFactory metadataFactory;

    @Override
    public SourceVersion getSupportedSourceVersion() {
        if (SourceVersion.RELEASE_17.compareTo(SourceVersion.latest()) > 0)
            return SourceVersion.RELEASE_17;

        return SourceVersion.latest();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        typeUtils = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
        valueProviderParser = new CustomValueProviderParser(Context.newBuilder("js")
           .allowHostAccess(HostAccess.ALL)
           .build());
        valueProviderFactory = new ConstantValueProviderFactory();
        metadataFactory = new TypeMetadataFactory(elementUtils);
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
        final var constantValueProviderService = initializeValueProviderService(element.getAnnotationsByType(FixtureValueProvider.class), metadataFactory);
        final var constantsGenerationStrategy = new ConstantGenerationStrategy(new ConstantNamingStrategy(), constantValueProviderService);

        final var fixtureAnnotation = element.getAnnotation(Fixture.class);
        final var metadata = metadataFactory.createMetadataFrom(element.asType(), Arrays.stream(fixtureAnnotation.genericImplementations()).toList());

        try {
            final var fixtureFile = processingEnv.getFiler()
                    .createSourceFile(metadata.getQualifiedFixtureClassName());

            try (final var fixtureWriter = new PrintWriter(fixtureFile.openWriter())) {
                if (metadata.hasPackageName()) {
                    fixtureWriter.println("package %s;".formatted(metadata.getPackageName()));
                    fixtureWriter.println();
                }
                final var constantMap = constantsGenerationStrategy.generateConstants(element, metadata);
                final var creationMethodStrategies = getCreationMethodGenerationStrategies(constantValueProviderService, constantMap);

                final var creationMethods = creationMethodStrategies.stream()
                        .flatMap(strategy -> strategy.generateCreationMethods(element, constantMap, metadata).stream());

                fixtureWriter.println("public class %sFixture {".formatted(metadata.getSimpleClassNameWithoutGeneric()));
                constantMap.values().forEach(constant -> fixtureWriter.println("%spublic static %s %s = %s;".formatted(WHITESPACE_4, constant.getType(), constant.getName(), constant.getValue())));
                creationMethods.forEach(method -> {
                    fixtureWriter.println();
                    fixtureWriter.println("%spublic static %s %s() {".formatted(WHITESPACE_4, method.getReturnType(), method.getName()));
                    fixtureWriter.println("%sreturn %s;".formatted(WHITESPACE_8, method.getReturnValue()));
                    fixtureWriter.println("%s}".formatted(WHITESPACE_4));
                });
                fixtureWriter.println("}");
            }
        } catch (IOException e) {
            throw new FixtureCreationException("Failed to create source file %s for fixture.".formatted(metadata.getQualifiedClassName()));
        }
    }

    private ConstantValueProviderService initializeValueProviderService(FixtureValueProvider[] customFixtureProviders, MetadataFactory metadataFactory) {
        final var customValueProviders = Arrays.stream(customFixtureProviders)
                .collect(Collectors.toMap(
                        FixtureValueProvider::targetType,
                        annotation -> valueProviderParser.parseValueProvider(annotation.valueProviderCallback())
                ));
        return new ConstantValueProviderService(customValueProviders, valueProviderFactory, elementUtils, typeUtils, metadataFactory);
    }

    private List<CreationMethodGenerationStrategy> getCreationMethodGenerationStrategies(ValueProviderService constantValueProviderService, ConstantMap constantMap) {
        final var creationMethodStrategies = new ArrayList<CreationMethodGenerationStrategy>();
        final var creationMethodValueProviderService = new CreationMethodValueProviderService(constantValueProviderService, constantMap);
        creationMethodStrategies.add(
                new ConstructorCreationMethodStrategy(
                        creationMethodValueProviderService,
                        valueProviderFactory.createConstructorValueProvider(creationMethodValueProviderService)));
        creationMethodStrategies.add(
                new BuilderCreationMethodStrategy(
                        creationMethodValueProviderService,
                        valueProviderFactory.createBuilderValueProvider(creationMethodValueProviderService)));
        return creationMethodStrategies;
    }
}
