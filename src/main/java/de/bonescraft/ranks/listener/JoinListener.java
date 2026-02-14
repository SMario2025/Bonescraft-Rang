package de.bonescraft.ranks.listener;

import de.bonescraft.ranks.BonescraftRanksPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public final class JoinListener implements Listener {

    private final BonescraftRanksPlugin plugin;

    public JoinListener(BonescraftRanksPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        plugin.ranks().apply(e.getPlayer());
    }
}
