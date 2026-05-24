package dev.kwlew.beaconsplus;

import dev.kwlew.beaconsplus.kernel.Bootstrap;
import org.bukkit.plugin.java.JavaPlugin;

public final class BeaconsPlus extends JavaPlugin {

    private Bootstrap bootstrap;
    private long start;

    @Override
    public void onEnable() {
        start = System.currentTimeMillis();
        saveDefaultConfig();

        bootstrap = new Bootstrap(this);
        bootstrap.init();

        logStartupTime();
    }

    @Override
    public void onDisable() {
        if (bootstrap != null) {
            bootstrap.shutdown();
        }
    }

    private void logStartupTime() {
        long time = System.currentTimeMillis() - start;
        getLogger().info("Beacons+ enabled! (Took " + time + "ms)");
    }

}
