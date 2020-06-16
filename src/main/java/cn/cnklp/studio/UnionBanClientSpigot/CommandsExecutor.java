package cn.cnklp.studio.UnionBanClientSpigot;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.List;

public class CommandsExecutor implements TabExecutor {
    private final ClientPlugin plugin;
    private final List<String> SubCommands = new ArrayList<>();

    public CommandsExecutor(ClientPlugin plugin) {
        this.plugin = plugin;
        SubCommands.add("help");
        SubCommands.add("status");
        SubCommands.add("login");
        SubCommands.add("logout");
        SubCommands.add("ban");
        SubCommands.add("pardon");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender.hasPermission("unionban.account")) {
            if (args.length == 3 && args[0].equals("login")) {
                if (plugin.login != null) {
                    plugin.login.cancel();
                    plugin.login = null;
                }
                plugin.getConfig().set("username", args[1]);
                plugin.getConfig().set("password", args[2]);
                plugin.getConfig().set("api_key", "");
                plugin.saveConfig();
                new LoginTask(plugin, sender).runTaskAsynchronously(plugin);
                return true;
            }
            if (args.length == 1 && args[0].equals("logout")) {
                sender.sendMessage("Logging out...");
                if (plugin.login != null) {
                    plugin.login.cancel();
                    plugin.login = null;
                }
                plugin.getConfig().set("username", "");
                plugin.getConfig().set("password", "");
                plugin.getConfig().set("api_key", "");
                plugin.saveConfig();
                new LogoutTask(plugin, sender).runTaskAsynchronously(plugin);
                return true;
            }
        }
        if (sender.hasPermission("unionban.ban")) {
            if (args.length == 2 && args[0].equals("ban")) {
                new BanTask(plugin, sender, args[1]).runTaskAsynchronously(plugin);
                return true;
            }
            if (args.length == 2 && args[0].equals("pardon")) {
                new PardonTask(plugin, sender, args[1]).runTaskAsynchronously(plugin);
                return true;
            }
        }
        if (sender.hasPermission("unionban.info")) {
            if (args.length == 0) {
                return false;
            }
            if (args.length == 1 && args[0].equals("help")) {
                return false;
            }
            if (args.length == 1 && args[0].equals("status")) {
                new GetServerStatusTask(plugin, sender).runTaskAsynchronously(plugin);
                return true;
            }
            sender.sendMessage("Unknown command. Type \"/unionban help\" for help.");
            return true;
        }
        sender.sendMessage("You do not have permission to execute this command!");
        StringBuilder builder = new StringBuilder();
        builder.append(cmd);
        for (String arg : args) {
            builder.append(" ");
            builder.append(arg);
        }
        plugin.getLogger().warning(sender.getName() + " tried to execute command: " + builder);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        List<String> result = new ArrayList<>();
        if (args.length == 1) {
            result.addAll(SubCommands);
        } else if (args.length == 2 && (args[0].equals("ban") || args[0].equals("pardon"))) {
            for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
                String name = player.getName();
                if (name != null) {
                    result.add(name);
                }
            }
        }
        return result;
    }
}
