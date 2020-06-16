package cn.cnklp.studio.UnionBanClientSpigot;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public class KickPlayerTask extends BukkitRunnable {
    private final String username;
    private final ClientPlugin plugin;

    public KickPlayerTask(ClientPlugin plugin, String username) {
        this.username = username;
        this.plugin = plugin;
    }

    public void run() {
        Objects.requireNonNull(Bukkit.getPlayer(username)).kickPlayer("You are in the UnionBan list!");
    }
}
