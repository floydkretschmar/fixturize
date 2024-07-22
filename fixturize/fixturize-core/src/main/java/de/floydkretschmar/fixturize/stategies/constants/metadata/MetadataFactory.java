package de.floydkretschmar.fixturize.stategies.constants.metadata;

import de.floydkretschmar.fixturize.domain.Metadata;

import javax.lang.model.element.Element;
import java.util.List;

public interface MetadataFactory {
    Metadata createMetadataFrom(Element element);

    Metadata createMetadataFrom(Element element, List<String> genericTypeImplementations);
}
