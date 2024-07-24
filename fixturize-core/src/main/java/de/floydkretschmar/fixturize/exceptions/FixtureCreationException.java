package de.floydkretschmar.fixturize.exceptions;

/**
 * Thrown to indicate that an error occurred when generating a fixtures for all annotated classes.
 */
public class FixtureCreationException extends RuntimeException {

    /**
     * Constructs a {@link FixtureCreationException} with the specified detail message.
     * @param message - the detail message
     */
    public FixtureCreationException(String message) {
        super(message);
    }
}
