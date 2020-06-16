package cn.cnklp.studio.UnionBanClientSpigot;

import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import javax.net.ssl.HttpsURLConnection;
import java.net.URL;

public class LogoutTask extends BukkitRunnable {
    private final ClientPlugin plugin;
    private final CommandSender sender;

    public LogoutTask(ClientPlugin plugin, CommandSender sender) {
        this.plugin = plugin;
        this.sender = sender;
    }

    private static void Logout(String ServerAddress) throws Exception {
        String Address = ServerAddress + "/session/";
        URL url = new URL(Address);
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
        con.setReadTimeout(5000);
        con.setConnectTimeout(5000);
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestMethod("DELETE");
        con.setDoOutput(true);

        con.connect();

        int responseCode = con.getResponseCode();

        if (responseCode != 204) {
            con.disconnect();
            throw new Exception("You have not logged in!");
        }

        con.disconnect();
    }

    @Override
    public void run() {
        try {
            Logout(plugin.ServerAddress);
            sender.sendMessage("Logged out successfully.");
        } catch (Exception e) {
            if (e.getMessage().equals("You have not logged in!")) {
                sender.sendMessage("You have not logged in!");
            } else {
                sender.sendMessage("Cannot connect to UnionBan server!");
            }
        }
    }
}
