package dev.kwlew.beaconsplus.kernel;

import dev.kwlew.beaconsplus.managers.messages.MessageManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.function.Consumer;

public class Bootstrap {

    private final Registry registry = new Registry();

    public Bootstrap(JavaPlugin plugin) {
        registry.register(JavaPlugin.class, plugin);
        registry.register(Registry.class, registry);

        initAll();
    }

    public void init() {
        lifecycle(LifecycleComponent::init);
        lifecycle(LifecycleComponent::start);
    }

    public void shutdown() {
        lifecycle(LifecycleComponent::shutdown);
    }

    private void initManagers() {
        registry.resolve(MessageManager.class);
    }

    private void initAll() {
        initManagers();
    }

    private void lifecycle(Consumer<LifecycleComponent> action) {
        for (Object obj : new ArrayList<>(registry.getAll())) {
            if (obj instanceof LifecycleComponent component) {
                action.accept(component);
            }
        }
    }
}