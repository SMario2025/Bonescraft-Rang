package de.bonescraft.ranks.command;

import de.bonescraft.ranks.BonescraftRanksPlugin;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.*;

public final class RankCommand implements CommandExecutor, TabCompleter {

    private final BonescraftRanksPlugin plugin;

    public RankCommand(BonescraftRanksPlugin plugin) {
        this.plugin = plugin;
    }

    private String msg(String path, String def) {
        return plugin.getConfig().getString(path, def);
    }

    private String fmt(String s, Map<String, String> vars) {
        for (var e : vars.entrySet()) s = s.replace("{" + e.getKey() + "}", e.getValue());
        return s;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("bonescraft.admin")) {
            sender.sendMessage(msg("messages.no-feature", "§cKeine Rechte."));
            return true;
        }
        if (args.length == 0) return false;

        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "set" -> {
                if (args.length != 3) return false;
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                if (target == null || (target.getName() == null && !target.hasPlayedBefore())) {
                    sender.sendMessage(msg("messages.player-not-found", "§cSpieler nicht gefunden."));
                    return true;
                }
                String rank = args[2].toLowerCase(Locale.ROOT);
                if (!plugin.ranks().rankExists(rank)) {
                    sender.sendMessage(fmt(msg("messages.rank-not-found", "§cRang nicht gefunden: §e{rank}"),
                            Map.of("rank", rank)));
                    return true;
                }
                plugin.ranks().setRank(target.getUniqueId(), rank);
                sender.sendMessage(fmt(msg("messages.rank-set", "§aRang gesetzt."),
                        Map.of("player", target.getName() != null ? target.getName() : args[1], "rank", rank)));
                if (target.isOnline()) {
                    Player p = (Player) target;
                    p.sendMessage("§aDein Rang ist jetzt: §b" + rank);
                }
                return true;
            }
            case "get" -> {
                if (args.length != 2) return false;
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                if (target == null || (target.getName() == null && !target.hasPlayedBefore())) {
                    sender.sendMessage(msg("messages.player-not-found", "§cSpieler nicht gefunden."));
                    return true;
                }
                String rank = plugin.ranks().getRank(target.getUniqueId());
                sender.sendMessage(fmt(msg("messages.rank-get", "§e{player} §ahat Rang: §b{rank}"),
                        Map.of("player", target.getName() != null ? target.getName() : args[1], "rank", rank)));
                return true;
            }
            case "list" -> {
                sender.sendMessage("§aRänge: §e" + String.join("§7, §e", plugin.ranks().getAvailableRanks()));
                return true;
            }
            case "reload" -> {
                plugin.reloadAll();
                sender.sendMessage(msg("messages.reloaded", "§aNeu geladen."));
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("bonescraft.admin")) return List.of();

        if (args.length == 1) return Arrays.asList("set", "get", "list", "reload");

        if (args.length == 2 && (args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("get"))) {
            List<String> names = new ArrayList<>();
            for (Player p : Bukkit.getOnlinePlayers()) names.add(p.getName());
            return names;
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("set")) {
            return new ArrayList<>(plugin.ranks().getAvailableRanks());
        }

        return List.of();
    }
}
