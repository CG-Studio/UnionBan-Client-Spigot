package cn.cnklp.studio.UnionBanClientSpigot;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

public class WhitelistListener implements Listener {
    private final ClientPlugin plugin;

    public WhitelistListener(ClientPlugin plugin) {
        this.plugin = plugin;
    }

    void onWhitelist(String prefix, String command) {
        if (command.startsWith(prefix) && command.length() > prefix.length() + 2 && command.charAt(prefix.length() + 1) != ' ') {
            new WhitelistTask(plugin, command.substring(prefix.length() + 1)).runTask(plugin);
        }
    }

    @EventHandler
    public void PlayerAction(PlayerCommandPreprocessEvent event) {
        String prefix = "/whitelist add";
        onWhitelist(prefix, event.getMessage());
    }

    @EventHandler
    public void ServerAction(ServerCommandEvent event) {
        String prefix = "whitelist add";
        onWhitelist(prefix, event.getCommand());
    }
}
