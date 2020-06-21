package cn.cnklp.studio.UnionBanClientSpigot;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class KickPlayerTask extends BukkitRunnable {
    private final String username;
    private final ClientPlugin plugin;

    public KickPlayerTask(ClientPlugin plugin, String username) {
        this.username = username;
        this.plugin = plugin;
    }

    public void run() {
        Player player = Bukkit.getPlayer(username);
        if (player == null) {
            plugin.getLogger().info("Player " + username + " is not in the server.");
        } else {
            player.kickPlayer("You are in the UnionBan list!");
        }
    }
}
