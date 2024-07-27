package de.floydkretschmar.fixturize;

import com.google.auto.service.AutoService;
import de.floydkretschmar.fixturize.annotations.Fixture;
import de.floydkretschmar.fixturize.annotations.FixtureValueProvider;
import de.floydkretschmar.fixturize.domain.TypeMetadata;
import de.floydkretschmar.fixturize.exceptions.FixtureCreationException;
import de.floydkretschmar.fixturize.stategies.constants.naming.ConstantNamingStrategy;
import de.floydkretschmar.fixturize.stategies.constants.ConstantGenerationStrategy;
import de.floydkretschmar.fixturize.stategies.constants.metadata.MetadataFactory;
import de.floydkretschmar.fixturize.stategies.constants.metadata.TypeMetadataFactory;
import de.floydkretschmar.fixturize.stategies.constants.value.ConstantValueProviderService;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.ConstantValueProviderFactory;
import de.floydkretschmar.fixturize.stategies.creation.BuilderCreationMethodStrategy;
import de.floydkretschmar.fixturize.stategies.creation.ConstructorCreationMethodStrategy;
import de.floydkretschmar.fixturize.stategies.creation.CreationMethodGenerationStrategy;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
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

    @Override
    public SourceVersion getSupportedSourceVersion() {
        if (SourceVersion.RELEASE_17.compareTo(SourceVersion.latest()) > 0)
            return SourceVersion.RELEASE_17;

        return SourceVersion.latest();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.typeUtils = processingEnv.getTypeUtils();
        this.elementUtils = processingEnv.getElementUtils();
        this.valueProviderParser = new CustomValueProviderParser(Context.newBuilder("js")
                .allowHostAccess(HostAccess.ALL)
                .build());
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
        final var constantsNamingStrategy = new ConstantNamingStrategy();
        final var constantsGenerationStrategy = new ConstantGenerationStrategy(constantsNamingStrategy, valueProviderService);

        final var creationMethodStrategies = new ArrayList<CreationMethodGenerationStrategy>();
        creationMethodStrategies.add(new ConstructorCreationMethodStrategy(valueProviderService));
        creationMethodStrategies.add(new BuilderCreationMethodStrategy(valueProviderService));

        final var fixtureAnnotation = element.getAnnotation(Fixture.class);
        final var metadata = metadataFactory.createMetadataFrom(element.asType(), Arrays.stream(fixtureAnnotation.genericImplementations()).toList());

        try {
            final var fixtureFile = processingEnv.getFiler()
                    .createSourceFile(metadata.getQualifiedFixtureClassName());

            try (final var out = new PrintWriter(fixtureFile.openWriter())) {
                writeFixture(out, element, metadata, constantsGenerationStrategy, creationMethodStrategies);
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
        return new ConstantValueProviderService(customValueProviders, new ConstantValueProviderFactory(), elementUtils, typeUtils, metadataFactory);
    }

    private static void writeFixture(
            PrintWriter writer,
            TypeElement element,
            TypeMetadata metadata,
            ConstantGenerationStrategy constantsGenerationStrategy,
            ArrayList<CreationMethodGenerationStrategy> creationMethodStrategies) {
        if (metadata.hasPackageName()) {
            writer.println("package %s;".formatted(metadata.getPackageName()));
            writer.println();
        }

        final var constantMap = constantsGenerationStrategy.generateConstants(element, metadata);
        final var creationMethods = creationMethodStrategies.stream()
                .flatMap(strategy -> strategy.generateCreationMethods(element, constantMap, metadata).stream());

        writer.println("public class %sFixture {".formatted(metadata.getSimpleClassNameWithoutGeneric()));
        constantMap.values().forEach(constant -> writer.println("%spublic static %s %s = %s;".formatted(WHITESPACE_4, constant.getType(), constant.getName(), constant.getValue())));
        creationMethods.forEach(method -> {
            writer.println();
            writer.println("%spublic static %s %s() {".formatted(WHITESPACE_4, method.getReturnType(), method.getName()));
            writer.println("%sreturn %s;".formatted(WHITESPACE_8, method.getReturnValue()));
            writer.println("%s}".formatted(WHITESPACE_4));
        });
        writer.println("}");
    }
}
