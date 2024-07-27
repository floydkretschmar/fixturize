package de.floydkretschmar.fixturize.stategies.metadata;

import de.floydkretschmar.fixturize.ElementUtils;
import de.floydkretschmar.fixturize.exceptions.FixtureCreationException;
import lombok.RequiredArgsConstructor;

import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static javax.lang.model.type.TypeKind.DECLARED;

/**
 * Creates metadata for {@link TypeMirror}s.
 *
 * @author Floyd Kretschmar
 */
@RequiredArgsConstructor
public class TypeMetadataFactory implements MetadataFactory {

    private final Elements elementUtils;

    @Override
    public TypeMetadata createMetadataFrom(TypeMirror type) {
        List<? extends TypeMirror> concreteTypesSetForGenerics = List.of();
        if (type.getKind() == DECLARED && !((DeclaredType) type).getTypeArguments().isEmpty()) {
            concreteTypesSetForGenerics = ((DeclaredType) type).getTypeArguments();
        }
        return createMetadata(type.toString(), concreteTypesSetForGenerics, elementUtils);
    }

    @Override
    public TypeMetadata createMetadataFrom(TypeMirror type, List<String> genericTypeImplementations) {
        final var concreteTypesSetForGenerics = genericTypeImplementations.stream().map(typeString -> {
            var typeElement = elementUtils.getTypeElement(typeString);
            if (Objects.isNull(typeElement))
                throw new FixtureCreationException("The type %s defined for class %s does not exist".formatted(typeString, type.toString()));
            return typeElement.asType();
        }).toList();
        return createMetadata(type.toString(), concreteTypesSetForGenerics, elementUtils);
    }

    private static Map<? extends TypeMirror, ? extends DeclaredType> createGenericMap(
            List<? extends TypeMirror> concreteTypesSetForGenerics, String qualifiedClassNameWithoutGeneric, Elements elementUtils) {
        final var fieldElementWithoutGenericsSet = elementUtils.getTypeElement(qualifiedClassNameWithoutGeneric);
        final var genericsDefinedOnType = ((DeclaredType) fieldElementWithoutGenericsSet.asType()).getTypeArguments();

        if (genericsDefinedOnType.size() != concreteTypesSetForGenerics.size())
            throw new FixtureCreationException(("There is a mismatch in the number of defined generics and actual concrete types " +
                    "defined for these generics for class %s").formatted(qualifiedClassNameWithoutGeneric));

        return genericsDefinedOnType.stream().collect(ElementUtils.toLinkedMap(
                Function.identity(),
                value -> (DeclaredType) concreteTypesSetForGenerics.get(genericsDefinedOnType.indexOf(value))));
    }

    private static TypeMetadata createMetadata(String qualifiedClassName, List<? extends TypeMirror> concreteTypesForGenerics, Elements elementUtils) {
        final var genericStartIndex = qualifiedClassName.indexOf('<');
        final var qualifiedClassNameWithoutGeneric = genericStartIndex > 0 ?
                qualifiedClassName.substring(0, genericStartIndex) : qualifiedClassName;
        final var lastDot = qualifiedClassNameWithoutGeneric.lastIndexOf('.');

        var packageName = "";
        if (lastDot > 0)
            packageName = qualifiedClassNameWithoutGeneric.substring(0, lastDot);

        final var simpleClassNameWithoutGeneric = qualifiedClassNameWithoutGeneric.substring(lastDot + 1);
        final var qualifiedFixtureClassName = qualifiedClassNameWithoutGeneric + "Fixture";

        var builder = TypeMetadata.builder()
                .packageName(packageName)
                .qualifiedClassNameWithoutGeneric(qualifiedClassNameWithoutGeneric)
                .simpleClassNameWithoutGeneric(simpleClassNameWithoutGeneric)
                .qualifiedFixtureClassName(qualifiedFixtureClassName);

        setGenericPart(
                builder,
                concreteTypesForGenerics,
                elementUtils,
                genericStartIndex,
                simpleClassNameWithoutGeneric,
                qualifiedClassNameWithoutGeneric);

        return builder.build();
    }

    private static void setGenericPart(
            TypeMetadata.TypeMetadataBuilder builder,
            List<? extends TypeMirror> concreteTypesForGenerics,
            Elements elementUtils,
            int genericStartIndex,
            String simpleClassNameWithoutGeneric,
            String qualifiedClassNameWithoutGeneric) {
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
    }
}
