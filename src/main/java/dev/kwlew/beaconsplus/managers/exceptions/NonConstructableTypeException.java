package dev.kwlew.beaconsplus.managers.exceptions;

public class NonConstructableTypeException extends RuntimeException {
    public NonConstructableTypeException(Class<?> type) {
        super("No registered instance for non-constructable type " + type.getName());
    }
}
