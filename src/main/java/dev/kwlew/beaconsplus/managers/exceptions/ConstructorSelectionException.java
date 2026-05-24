package dev.kwlew.beaconsplus.managers.exceptions;

public class ConstructorSelectionException extends RuntimeException {
    public ConstructorSelectionException(Class<?> type, String reason) {
        super("Invalid constructor configuration for " + type.getName() + ": " + reason);
    }
}
