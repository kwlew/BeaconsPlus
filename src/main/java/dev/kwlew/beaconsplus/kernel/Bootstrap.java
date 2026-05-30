package dev.kwlew.beaconsplus.kernel;

import dev.kwlew.beaconsplus.hooks.bStats.bStats;
import dev.kwlew.beaconsplus.listeners.BeaconListener;
import dev.kwlew.beaconsplus.managers.config.ConfigManager;
import dev.kwlew.beaconsplus.managers.messages.MessageManager;
import dev.kwlew.beaconsplus.managers.utils.BeaconManager;
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

    public <T> T resolve(Class<T> type) {
        return registry.resolve(type);
    }

    private void initManagers() {
        registry.resolve(ConfigManager.class);
        registry.resolve(MessageManager.class);
        registry.resolve(BeaconManager.class);
    }

    private void initListeners() {
        registry.resolve(BeaconListener.class);
    }

    private void initHooks() {
        registry.resolve(bStats.class);
    }

    private void initAll() {
        initManagers();

        initHooks();

        initListeners();
    }

    private void lifecycle(Consumer<LifecycleComponent> action) {
        for (Object obj : new ArrayList<>(registry.getAll())) {
            if (obj instanceof LifecycleComponent component) {
                action.accept(component);
            }
        }
    }
}