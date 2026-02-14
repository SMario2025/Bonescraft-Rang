package de.bonescraft.ranks;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.io.File;
import java.io.IOException;
import java.util.*;

public final class RankManager {

    private final BonescraftRanksPlugin plugin;
    private final Map<UUID, String> playerRanks = new HashMap<>();
    private final Map<UUID, PermissionAttachment> attachments = new HashMap<>();

    private File ranksFile;
    private YamlConfiguration ranksYaml;

    public RankManager(BonescraftRanksPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        if (ranksFile == null) ranksFile = new File(plugin.getDataFolder(), "ranks.yml");

        if (!ranksFile.exists()) {
            try {
                plugin.getDataFolder().mkdirs();
                ranksFile.createNewFile();
                YamlConfiguration y = new YamlConfiguration();
                y.set("players", new HashMap<>());
                y.save(ranksFile);
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create ranks.yml: " + e.getMessage());
            }
        }

        ranksYaml = YamlConfiguration.loadConfiguration(ranksFile);

        playerRanks.clear();
        ConfigurationSection playersSec = ranksYaml.getConfigurationSection("players");
        if (playersSec != null) {
            for (String key : playersSec.getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    String rank = playersSec.getString(key, "default");
                    playerRanks.put(uuid, rank);
                } catch (IllegalArgumentException ignored) {}
            }
        }

        for (Player p : Bukkit.getOnlinePlayers()) apply(p);
    }

    public void save() {
        if (ranksYaml == null) return;
        for (Map.Entry<UUID, String> e : playerRanks.entrySet()) {
            ranksYaml.set("players." + e.getKey().toString(), e.getValue());
        }
        try {
            ranksYaml.save(ranksFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save ranks.yml: " + e.getMessage());
        }
    }

    public String getRank(UUID uuid) {
        return playerRanks.getOrDefault(uuid, "default");
    }

    public Set<String> getAvailableRanks() {
        ConfigurationSection sec = plugin.getConfig().getConfigurationSection("ranks");
        if (sec == null) return Set.of("default");
        return sec.getKeys(false);
    }

    public boolean rankExists(String rank) {
        return plugin.getConfig().getConfigurationSection("ranks." + rank) != null;
    }

    public void setRank(UUID uuid, String rank) {
        playerRanks.put(uuid, rank);
        save();
        Player p = Bukkit.getPlayer(uuid);
        if (p != null) apply(p);
    }

    public void apply(Player player) {
        clearAttachment(player.getUniqueId());

        String rank = getRank(player.getUniqueId());
        Set<String> perms = resolvePermissions(rank, new HashSet<>());

        PermissionAttachment att = player.addAttachment(plugin);
        for (String perm : perms) att.setPermission(perm, true);
        attachments.put(player.getUniqueId(), att);
    }

    private Set<String> resolvePermissions(String rank, Set<String> visiting) {
        if (visiting.contains(rank)) return Set.of();
        visiting.add(rank);

        ConfigurationSection sec = plugin.getConfig().getConfigurationSection("ranks." + rank);
        if (sec == null) return Set.of();

        Set<String> out = new HashSet<>();
        for (String parent : sec.getStringList("inherits")) out.addAll(resolvePermissions(parent, visiting));
        out.addAll(sec.getStringList("perms"));
        return out;
    }

    public void clearAttachment(UUID uuid) {
        PermissionAttachment att = attachments.remove(uuid);
        if (att != null) {
            try { att.remove(); } catch (IllegalStateException ignored) {}
        }
    }

    public void clearAllAttachments() {
        for (UUID uuid : new ArrayList<>(attachments.keySet())) clearAttachment(uuid);
    }
}
