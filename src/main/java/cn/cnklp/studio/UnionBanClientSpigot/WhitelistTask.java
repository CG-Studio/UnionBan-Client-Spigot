package cn.cnklp.studio.UnionBanClientSpigot;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class WhitelistTask extends BukkitRunnable {
    private final String username;
    private final ClientPlugin plugin;

    public WhitelistTask(ClientPlugin plugin, String username) {
        this.username = username;
        this.plugin = plugin;
    }

    @Override
    public void run() {
        try {
            String uuid = PlayerUUID.FromUsername(username);
            plugin.getLogger().info("UUID of " + username + ": " + uuid);
            boolean isBanned = PlayerUUID.IsBan(plugin.ServerAddress, uuid);
            plugin.getLogger().info("Player " + username + " is banned: " + isBanned);
            if (isBanned) {
                Bukkit.broadcastMessage(ChatColor.RED + "[UnionBan] Player " + username + " is in the UnionBan list!");
            }
        } catch (Exception e) {
            Bukkit.getLogger().warning("Failed to get information about player " + username + "!");
        }
    }
}
