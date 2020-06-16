package cn.cnklp.studio.UnionBanClientSpigot;

import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

public class BanTask extends BukkitRunnable {
    private final String username;
    private final CommandSender sender;
    private final ClientPlugin plugin;

    public BanTask(ClientPlugin plugin, CommandSender sender, String username) {
        this.sender = sender;
        this.plugin = plugin;
        this.username = username;
    }

    @Override
    public void run() {
        String uuid;
        sender.sendMessage("Querying UUID of player " + username + " from Mojang server...");
        try {
            uuid = PlayerUUID.FromUsername(username);
            sender.sendMessage("UUID of player " + username + " is " + uuid + ".");
        } catch (Exception e) {
            if (e.getMessage().equals("Player does not exist!")) {
                sender.sendMessage("Player " + username + " does not exist!");
            } else {
                sender.sendMessage("Cannot connect to Mojang server!");
            }
            return;
        }
        try {
            PlayerUUID.Ban(plugin.ServerAddress, uuid);
            sender.sendMessage("Banned player " + username + " successfully!");
        } catch (Exception e) {
            if (e.getMessage().equals("Record exists!")) {
                sender.sendMessage("Player " + username + " is already in the UnionBan list!");
            } else if (e.getMessage().equals("Not logged in!")) {
                sender.sendMessage("You have not logged in!");
            } else {
                e.printStackTrace();
                sender.sendMessage("Failed to ban player " + username + "!");
            }
        }
    }
}
