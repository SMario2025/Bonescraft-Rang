package de.bonescraft.ranks;

import de.bonescraft.ranks.command.RankCommand;
import de.bonescraft.ranks.listener.BuildListener;
import de.bonescraft.ranks.listener.FeatureListener;
import de.bonescraft.ranks.listener.JoinListener;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class BonescraftRanksPlugin extends JavaPlugin {

    private RankManager rankManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.rankManager = new RankManager(this);
        this.rankManager.load();

        getServer().getPluginManager().registerEvents(new JoinListener(this), this);
        getServer().getPluginManager().registerEvents(new BuildListener(this), this);
        getServer().getPluginManager().registerEvents(new FeatureListener(this), this);

        PluginCommand cmd = getCommand("bcrank");
        if (cmd != null) {
            RankCommand rc = new RankCommand(this);
            cmd.setExecutor(rc);
            cmd.setTabCompleter(rc);
        } else {
            getLogger().severe("Command '/bcrank' missing from plugin.yml");
        }

        getLogger().info("BonescraftRanks v" + getDescription().getVersion() + " enabled.");
    }

    @Override
    public void onDisable() {
        if (rankManager != null) rankManager.clearAllAttachments();
    }

    public RankManager ranks() {
        return rankManager;
    }

    public void reloadAll() {
        reloadConfig();
        rankManager.load();
    }
}
