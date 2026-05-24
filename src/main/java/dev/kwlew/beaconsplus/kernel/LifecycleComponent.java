package dev.kwlew.beaconsplus.kernel;

public interface LifecycleComponent {

    default void init() {}
    default void start() {}
    default void shutdown() {}
}