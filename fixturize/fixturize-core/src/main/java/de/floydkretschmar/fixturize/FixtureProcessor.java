package de.floydkretschmar.fixturize;

import com.google.auto.service.AutoService;
import com.google.common.base.Function;
import de.floydkretschmar.fixturize.exceptions.FixtureCreationException;
import de.floydkretschmar.fixturize.stategies.constants.CamelCaseToScreamingSnakeCaseNamingStrategy;
import de.floydkretschmar.fixturize.stategies.constants.ConstantsGenerationStrategy;
import de.floydkretschmar.fixturize.stategies.constants.ConstantsNamingStrategy;
import de.floydkretschmar.fixturize.stategies.constants.DefaultConstantGenerationStrategy;
import de.floydkretschmar.fixturize.stategies.creation.CreationMethodGenerationStrategy;
import de.floydkretschmar.fixturize.stategies.creation.FixtureConstructorCreationMethodGenerationStrategy;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@SupportedAnnotationTypes(
        "de.floydkretschmar.fixturize.annotations.Fixture")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
@AutoService(Processor.class)
public class FixtureProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        annotations.forEach(annnotation -> roundEnv.getElementsAnnotatedWith(annnotation).forEach(element -> this.processAnnotatedElement((TypeElement)element)));
        return true;
    }

    private void processAnnotatedElement(TypeElement element) {
        final var constantsNamingStrategy = new CamelCaseToScreamingSnakeCaseNamingStrategy();
        final var creationMethodStrategies = new ArrayList<CreationMethodGenerationStrategy>();
        creationMethodStrategies.add(new FixtureConstructorCreationMethodGenerationStrategy(constantsNamingStrategy));
        final var constantsGenerationStrategy = new DefaultConstantGenerationStrategy(constantsNamingStrategy, Map.of());

        final var qualifiedClassName = element.getQualifiedName().toString();
        String packageName = "";
        final int lastDot = qualifiedClassName.lastIndexOf('.');
        if (lastDot > 0) {
            packageName = qualifiedClassName.substring(0, lastDot);
        }

        final String simpleClassName = qualifiedClassName.substring(lastDot + 1);
        final String qualifiedFixtureClassName = qualifiedClassName + "Fixture";

        try {
            final JavaFileObject fixtureFile = processingEnv.getFiler()
                    .createSourceFile(qualifiedFixtureClassName);

            try (PrintWriter out = new PrintWriter(fixtureFile.openWriter())) {
                final String fixtureClassTemplate = """
                %spublic class %sFixture {
                %s
                
                %s
                }
                """;

                final String packageString = packageName.isEmpty() ? "" : "package %s;\n\n".formatted(packageName);
                final String constantsString = String.join("\n", constantsGenerationStrategy.generateConstants(element));
                final String creationMethodsString = creationMethodStrategies.stream()
                        .flatMap(stategy -> stategy.generateCreationMethods(element).stream())
                        .collect(Collectors.joining("\n\n"));
                final var fixtureClassString = String.format(fixtureClassTemplate, packageString, simpleClassName, constantsString, creationMethodsString);
                out.print(fixtureClassString);
            }
        } catch (IOException e) {
            throw new FixtureCreationException("Failed to create source file %s for fixture.".formatted(qualifiedClassName));
        }
    }
}
