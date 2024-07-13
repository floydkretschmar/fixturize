package de.floydkretschmar.fixturize.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FixtureCreationMethod {
    String returnType;
    String name;
    String creationCall;

    @Override
    public String toString() {
        return """
                    \tpublic %s %s() {
                    \t\treturn %s;
                    \t}""".formatted(returnType, name, creationCall);
    }
}
