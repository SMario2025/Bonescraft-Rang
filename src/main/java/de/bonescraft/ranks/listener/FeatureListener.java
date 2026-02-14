package de.bonescraft.ranks.listener;

import de.bonescraft.ranks.BonescraftRanksPlugin;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.List;

import static org.bukkit.Material.ENDER_PEARL;

public final class FeatureListener implements Listener {

    private final BonescraftRanksPlugin plugin;

    public FeatureListener(BonescraftRanksPlugin plugin) {
        this.plugin = plugin;
    }

    private boolean isEnabledWorld(World world) {
        List<String> worlds = plugin.getConfig().getStringList("enabled-worlds");
        return new HashSet<>(worlds).contains(world.getName());
    }

    private String msgNoFeature() {
        return plugin.getConfig().getString("messages.no-feature", "§cNo permission.");
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent e) {
        if (!plugin.getConfig().getBoolean("features.enderpearl", true)) return;
        if (e.getHand() != null && e.getHand() != EquipmentSlot.HAND) return;

        Player p = e.getPlayer();
        if (!isEnabledWorld(p.getWorld())) return;

        ItemStack item = e.getItem();
        if (item == null || item.getType() != ENDER_PEARL) return;

        if (!p.hasPermission("bonescraft.feature.enderpearl")) {
            e.setCancelled(true);
            p.sendMessage(msgNoFeature());
        }
    }
}
