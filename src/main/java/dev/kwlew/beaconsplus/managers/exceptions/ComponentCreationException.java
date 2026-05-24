package dev.kwlew.beaconsplus.managers.exceptions;

public class ComponentCreationException extends RuntimeException {
    public ComponentCreationException(Class<?> type, Throwable cause) {
        super("Failed to create component: " + type.getName(), cause);
    }
}
