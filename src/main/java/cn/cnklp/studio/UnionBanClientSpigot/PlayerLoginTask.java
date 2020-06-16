package cn.cnklp.studio.UnionBanClientSpigot;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerLoginTask extends BukkitRunnable {
    private final String username;
    private final ClientPlugin plugin;

    public PlayerLoginTask(ClientPlugin plugin, String username) {
        this.plugin = plugin;
        this.username = username;
    }

    @Override
    public void run() {
        try {
            String uuid = PlayerUUID.FromUsername(username);
            plugin.getLogger().info("UUID of " + username + ": " + uuid);
            boolean isBanned = PlayerUUID.IsBan(plugin.ServerAddress, uuid);
            plugin.getLogger().info("Player " + username + " is banned: " + isBanned);
            if (isBanned) {
                new KickPlayerTask(plugin, username).runTask(plugin);
            }
        } catch (Exception e) {
            Bukkit.getLogger().warning("Failed to get information about player " + username + "!");
        }
    }
}
