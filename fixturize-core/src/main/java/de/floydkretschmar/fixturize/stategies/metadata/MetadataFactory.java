package de.floydkretschmar.fixturize.stategies.metadata;

import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.util.List;

/**
 * Defines the methods used to create metadata for types.
 *
 * @author Floyd Kretschmar
 */
public interface MetadataFactory {

    /**
     * Creates type metadata for a type. If the type is a declared generic, the metadata will contain generic information
     * according to {@link DeclaredType#getTypeArguments()}.
     *
     * @param type - for which metadata will be generated
     * @return the type metadata
     */
    TypeMetadata createMetadataFrom(TypeMirror type);

    /**
     * Creates type metadata for a type using the specified list of class names to use for the generic information.
     *
     * @param type                       - for which metadata will be generated
     * @param genericTypeImplementations - list of class names defining the concrete implementations for a generic
     * @return the type metadata
     */
    TypeMetadata createMetadataFrom(TypeMirror type, List<String> genericTypeImplementations);
}
