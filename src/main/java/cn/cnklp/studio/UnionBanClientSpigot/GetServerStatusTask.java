package cn.cnklp.studio.UnionBanClientSpigot;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

class ServerStatus {
    public String version;
}

public class GetServerStatusTask extends BukkitRunnable {
    private final ClientPlugin plugin;
    private final CommandSender sender;

    public GetServerStatusTask(ClientPlugin plugin, CommandSender sender) {
        this.plugin = plugin;
        this.sender = sender;
    }

    public static String GetVersion(String ServerAddress) throws Exception {
        URL url = new URL(ServerAddress);
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
        con.setReadTimeout(5000);
        con.setConnectTimeout(5000);
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestMethod("GET");
        con.setDoOutput(true);

        int responseCode = con.getResponseCode();
        if (responseCode != 200) {
            con.disconnect();
            throw new Exception("HTTP Response Code: " + responseCode);
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder builder = new StringBuilder();
        String inputLine;
        while ((inputLine = reader.readLine()) != null) {
            builder.append(inputLine);
        }
        reader.close();

        con.disconnect();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ServerStatus status = objectMapper.readValue(builder.toString(), ServerStatus.class);

        return status.version;
    }

    @Override
    public void run() {
        sender.sendMessage("Querying server status...");
        sender.sendMessage("The UnionBan Server address is " + plugin.ServerAddress);
        try {
            String version = GetVersion(plugin.ServerAddress);
            sender.sendMessage("UnionBan Server is ON! Version: " + version);
            String username = LoginTask.GetLoginStatus(plugin.ServerAddress).username;
            sender.sendMessage("Logged in as " + plugin.getConfig().getString("username") + ".");
        } catch (Exception e) {
            if (e.getMessage().equals("Not logged in!")) {
                sender.sendMessage("You have not logged in!");
            } else {
                sender.sendMessage("Failed to connect to UnionBan server.");
            }
        }
    }
}
