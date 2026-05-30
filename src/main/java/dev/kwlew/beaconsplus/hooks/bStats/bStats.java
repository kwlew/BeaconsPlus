package dev.kwlew.beaconsplus.hooks.bStats;

import dev.kwlew.beaconsplus.kernel.LifecycleComponent;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

import static dev.kwlew.beaconsplus.managers.config.BuildInfo.BSTATS_ID;

public class bStats implements LifecycleComponent {

    private final JavaPlugin plugin;

    public bStats(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void start() {
        new Metrics(plugin, BSTATS_ID);
        plugin.getLogger().info("bStats connected.");
    }
}
