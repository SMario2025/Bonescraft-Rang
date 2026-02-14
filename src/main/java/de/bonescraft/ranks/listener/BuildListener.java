package de.bonescraft.ranks.listener;

import de.bonescraft.ranks.BonescraftRanksPlugin;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.HashSet;
import java.util.List;

public final class BuildListener implements Listener {

    private final BonescraftRanksPlugin plugin;

    public BuildListener(BonescraftRanksPlugin plugin) {
        this.plugin = plugin;
    }

    private boolean isEnabledWorld(World world) {
        List<String> worlds = plugin.getConfig().getStringList("enabled-worlds");
        return new HashSet<>(worlds).contains(world.getName());
    }

    private String msgNoBuild() {
        return plugin.getConfig().getString("messages.no-build", "§cNo permission.");
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        if (!isEnabledWorld(p.getWorld())) return;
        if (!p.hasPermission("bonescraft.build")) {
            e.setCancelled(true);
            p.sendMessage(msgNoBuild());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        if (!isEnabledWorld(p.getWorld())) return;
        if (!p.hasPermission("bonescraft.build")) {
            e.setCancelled(true);
            p.sendMessage(msgNoBuild());
        }
    }
}
