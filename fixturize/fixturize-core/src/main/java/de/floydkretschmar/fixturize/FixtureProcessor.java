package de.floydkretschmar.fixturize;

import com.google.auto.service.AutoService;
import de.floydkretschmar.fixturize.domain.FixtureConstantDefinition;
import de.floydkretschmar.fixturize.domain.FixtureNames;
import de.floydkretschmar.fixturize.exceptions.FixtureCreationException;
import de.floydkretschmar.fixturize.stategies.constants.CamelCaseToScreamingSnakeCaseNamingStrategy;
import de.floydkretschmar.fixturize.stategies.constants.ConstantGenerationStrategy;
import de.floydkretschmar.fixturize.stategies.creation.CreationMethodGenerationStrategy;
import de.floydkretschmar.fixturize.stategies.creation.FixtureConstructorStrategy;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SupportedAnnotationTypes("de.floydkretschmar.fixturize.annotations.Fixture")
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
        final var constantsGenerationStrategy = new ConstantGenerationStrategy(constantsNamingStrategy, Map.of());

        final var creationMethodStrategies = new ArrayList<CreationMethodGenerationStrategy>();
        creationMethodStrategies.add(new FixtureConstructorStrategy());

        final var names = getNames(element);

        try {
            final JavaFileObject fixtureFile = processingEnv.getFiler()
                    .createSourceFile(names.getQualifiedFixtureClassName());

            try (PrintWriter out = new PrintWriter(fixtureFile.openWriter())) {
                final var fixtureClassString = getFixtureClassAsString(element, names, constantsGenerationStrategy, creationMethodStrategies);
                out.print(fixtureClassString);
            }
        } catch (IOException e) {
            throw new FixtureCreationException("Failed to create source file %s for fixture.".formatted(names.getQualifiedClassName()));
        }
    }

    private FixtureNames getNames(TypeElement element) {
        final var qualifiedClassName = element.getQualifiedName().toString();
        final int lastDot = qualifiedClassName.lastIndexOf('.');
        var packageName = "";
        if (lastDot > 0) {
            packageName = qualifiedClassName.substring(0, lastDot);
        }
        final var simpleClassName = qualifiedClassName.substring(lastDot + 1);
        final var qualifiedFixtureClassName = qualifiedClassName + "Fixture";

        return FixtureNames.builder()
                .qualifiedClassName(qualifiedClassName)
                .simpleClassName(simpleClassName)
                .packageName(packageName)
                .qualifiedFixtureClassName(qualifiedFixtureClassName).build();
    }

    private static String getCreationMethodsString(TypeElement element, ArrayList<CreationMethodGenerationStrategy> creationMethodStrategies, Map<String, FixtureConstantDefinition> constantMap) {
        return creationMethodStrategies.stream()
                .flatMap(stategy -> stategy.generateCreationMethods(element, constantMap).stream())
                .map(method -> """
                    \tpublic %s %s() {
                    \t\treturn %s;
                    \t}""".formatted(method.getReturnType(), method.getName(), method.getReturnValue()))
                .collect(Collectors.joining("\n\n"));
    }

    private static String getConstantsString(Stream<FixtureConstantDefinition> constants) {
        return constants
                .sorted(Comparator.comparing(FixtureConstantDefinition::getName))
                .map(constant -> "\tpublic static %s %s = %s;".formatted(constant.getType(), constant.getName(), constant.getValue()))
                .collect(Collectors.joining("\n"));
    }

    private static String getFixtureClassAsString(TypeElement element, FixtureNames names, ConstantGenerationStrategy constantsGenerationStrategy, ArrayList<CreationMethodGenerationStrategy> creationMethodStrategies) {
        final String fixtureClassTemplate = """
        %spublic class %sFixture {
        %s
        
        %s
        }
        """;

        final String packageString = names.hasPackageName() ? "package %s;\n\n".formatted(names.getPackageName()) : "";

        final Map<String, FixtureConstantDefinition> constantMap = constantsGenerationStrategy.generateConstants(element);
        final String constantsString = getConstantsString(constantMap.values().stream());
        final String creationMethodsString = getCreationMethodsString(element, creationMethodStrategies, constantMap);

        return String.format(fixtureClassTemplate, packageString, names.getSimpleClassName(), constantsString, creationMethodsString);
    }
}
