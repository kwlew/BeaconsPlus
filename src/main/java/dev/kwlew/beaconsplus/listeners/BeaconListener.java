package dev.kwlew.beaconsplus.listeners;

import com.destroystokyo.paper.event.block.BeaconEffectEvent;
import dev.kwlew.beaconsplus.kernel.Inject;
import dev.kwlew.beaconsplus.managers.config.ConfigManager;
import dev.kwlew.beaconsplus.managers.utils.BeaconManager;
import io.papermc.paper.event.block.BeaconActivatedEvent;
import org.bukkit.block.Beacon;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class BeaconListener implements ListenerComponent {

    private final JavaPlugin plugin;
    private final BeaconManager beaconManager;
    private final ConfigManager config;

    @Inject
    public BeaconListener(JavaPlugin plugin, BeaconManager beaconManager, ConfigManager config) {
        this.plugin = plugin;
        this.beaconManager = beaconManager;
        this.config = config;
    }

    @Override
    public void start() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onBeaconActivation(BeaconActivatedEvent event) {

        Beacon beacon = event.getBeacon();

        beaconManager.setBeaconRange(beacon, config.beaconRange());

        plugin.getLogger().info("Increased beacon range. " + beacon.getEntitiesInRange());

    }

    @EventHandler
    public void onBeaconEffect(BeaconEffectEvent event) {
        Player player = event.getPlayer();

        beaconManager.testRegeneration(player);
        plugin.getLogger().info("Applied regeneration to " + player.getName());
    }

}
