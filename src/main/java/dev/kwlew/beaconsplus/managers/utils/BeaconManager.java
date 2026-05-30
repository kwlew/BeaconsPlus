package dev.kwlew.beaconsplus.managers.utils;

import org.bukkit.block.Beacon;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BeaconManager {

    public void setBeaconRange(Beacon beacon, double range) {
        beacon.setEffectRange(range);
        beacon.update();
    }

    public void testRegeneration(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 220, 0));
    }

}
