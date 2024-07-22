package de.floydkretschmar.fixturize.stategies.constants.metadata;

import de.floydkretschmar.fixturize.ReflectionUtils;
import de.floydkretschmar.fixturize.domain.Metadata;
import de.floydkretschmar.fixturize.exceptions.FixtureCreationException;
import lombok.RequiredArgsConstructor;

import javax.lang.model.element.Element;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static javax.lang.model.type.TypeKind.DECLARED;

@RequiredArgsConstructor
public class ConstantMetadataFactory implements MetadataFactory {

    private final Elements elementUtils;

    @Override
    public Metadata createMetadataFrom(Element element) {
        final var type = element.asType();
        List<? extends TypeMirror> concreteTypesSetForGenerics = List.of();
        if (type.getKind() == DECLARED && !((DeclaredType) type).getTypeArguments().isEmpty()) {
            concreteTypesSetForGenerics = ((DeclaredType) type).getTypeArguments();
        }
        return createMetadata(type.toString(), concreteTypesSetForGenerics, elementUtils);
    }

    @Override
    public Metadata createMetadataFrom(Element element, List<String> genericTypeImplementations) {
        final var elementType = element.asType();
        final var concreteTypesSetForGenerics = genericTypeImplementations.stream().map(typeString -> {
            var type = elementUtils.getTypeElement(typeString);
            if (Objects.isNull(type))
                throw new FixtureCreationException("The type %s defined for class %s does not exist".formatted(typeString, elementType.toString()));
            return type.asType();
        }).toList();
        return createMetadata(elementType.toString(), concreteTypesSetForGenerics, elementUtils);
    }

    private static Map<? extends TypeMirror, ? extends DeclaredType> createGenericMap(
            List<? extends TypeMirror> concreteTypesSetForGenerics, String qualifiedClassNameWithoutGeneric, Elements elementUtils) {
        final var fieldElementWithoutGenericsSet = elementUtils.getTypeElement(qualifiedClassNameWithoutGeneric);
        final var genericsDefinedOnType = ((DeclaredType) fieldElementWithoutGenericsSet.asType()).getTypeArguments();

        if (genericsDefinedOnType.size() != concreteTypesSetForGenerics.size())
            throw new FixtureCreationException(("There is a mismatch in the number of defined generics and actual concrete types " +
                    "defined for these generics for class %s").formatted(qualifiedClassNameWithoutGeneric));

        return genericsDefinedOnType.stream().collect(ReflectionUtils.toLinkedMap(
                Function.identity(),
                value -> (DeclaredType) concreteTypesSetForGenerics.get(genericsDefinedOnType.indexOf(value))));
    }

    private static Metadata createMetadata(String qualifiedClassName, List<? extends TypeMirror> concreteTypesForGenerics, Elements elementUtils) {
        final var genericStartIndex = qualifiedClassName.indexOf('<');
        final var qualifiedClassNameWithoutGeneric = genericStartIndex > 0 ?
                qualifiedClassName.substring(0, genericStartIndex) : qualifiedClassName;
        final var lastDot = qualifiedClassNameWithoutGeneric.lastIndexOf('.');

        var packageName = "";
        if (lastDot > 0)
            packageName = qualifiedClassNameWithoutGeneric.substring(0, lastDot);

        final var simpleClassNameWithoutGeneric = qualifiedClassNameWithoutGeneric.substring(lastDot + 1);
        final var qualifiedFixtureClassName = qualifiedClassNameWithoutGeneric + "Fixture";

        final var builder = Metadata.builder()
                .packageName(packageName)
                .qualifiedClassNameWithoutGeneric(qualifiedClassNameWithoutGeneric)
                .simpleClassNameWithoutGeneric(simpleClassNameWithoutGeneric)
                .qualifiedFixtureClassName(qualifiedFixtureClassName);

        if (genericStartIndex > 0) {
            final var genericPart = "<%s>".formatted(concreteTypesForGenerics.stream()
                    .map(TypeMirror::toString)
                    .collect(Collectors.joining(", ")));
            builder.genericPart(genericPart)
                    .simpleClassName("%s%s".formatted(simpleClassNameWithoutGeneric, genericPart))
                    .qualifiedClassName("%s%s".formatted(qualifiedClassNameWithoutGeneric, genericPart))
                    .genericTypeMap(createGenericMap(
                            concreteTypesForGenerics,
                            qualifiedClassNameWithoutGeneric,
                            elementUtils));
        } else {
            builder.genericPart("")
                    .simpleClassName(simpleClassNameWithoutGeneric)
                    .qualifiedClassName(qualifiedClassNameWithoutGeneric)
                    .genericTypeMap(Map.of());
        }
        return builder.build();
    }
}
