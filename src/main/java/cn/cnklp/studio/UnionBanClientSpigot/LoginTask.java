package cn.cnklp.studio.UnionBanClientSpigot;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;

class LoginStatus {
    public String username;
    public String api_key;
}

class Expire {
    public int expire;
}

class UserInfo {
    public String account;
    public String password;
}

public class LoginTask extends BukkitRunnable {
    private final ClientPlugin plugin;
    private final CommandSender sender;

    public LoginTask(ClientPlugin plugin, CommandSender sender) {
        this.plugin = plugin;
        this.sender = sender;
    }

    private static int LoginViaKey(String ServerAddress, String api_key) throws Exception {
        LoginStatus key = new LoginStatus();
        key.api_key = api_key;

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(key);

        String Address = ServerAddress + "/session/";
        URL url = new URL(Address);
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
        con.setReadTimeout(5000);
        con.setConnectTimeout(5000);
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestMethod("PUT");
        con.setDoOutput(true);

        con.connect();

        DataOutputStream stream = new DataOutputStream(con.getOutputStream());
        stream.writeBytes(json);
        stream.flush();
        stream.close();

        int responseCode = con.getResponseCode();

        if (responseCode != 200) {
            con.disconnect();
            throw new Exception("Login failed!");
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder builder = new StringBuilder();
        String inputLine;
        while ((inputLine = reader.readLine()) != null) {
            builder.append(inputLine);
        }
        reader.close();
        con.disconnect();

        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Expire expire = mapper.readValue(builder.toString(), Expire.class);

        return expire.expire;
    }

    private static int LoginViaUsername(String ServerAddress, String username, String password) throws Exception {
        UserInfo info = new UserInfo();
        info.account = username;
        info.password = password;

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(info);

        String Address = ServerAddress + "/session/";
        URL url = new URL(Address);
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
        con.setReadTimeout(5000);
        con.setConnectTimeout(5000);
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestMethod("POST");
        con.setDoOutput(true);

        con.connect();

        DataOutputStream stream = new DataOutputStream(con.getOutputStream());
        stream.writeBytes(json);
        stream.flush();
        stream.close();

        int responseCode = con.getResponseCode();

        if (responseCode != 200) {
            con.disconnect();
            throw new Exception("Login failed!");
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder builder = new StringBuilder();
        String inputLine;
        while ((inputLine = reader.readLine()) != null) {
            builder.append(inputLine);
        }
        reader.close();
        con.disconnect();

        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Expire expire = mapper.readValue(builder.toString(), Expire.class);

        return expire.expire;
    }

    public static LoginStatus GetLoginStatus(String ServerAddress) throws Exception {
        URL url = new URL(ServerAddress + "/user/");
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
            throw new Exception("Not logged in!");
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder builder = new StringBuilder();
        String inputLine;
        while ((inputLine = reader.readLine()) != null) {
            builder.append(inputLine);
        }
        reader.close();

        con.disconnect();

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return mapper.readValue(builder.toString(), LoginStatus.class);
    }

    @Override
    public void run() {
        FileConfiguration config = plugin.getConfig();
        String username = config.getString("username");
        String password = config.getString("password");
        String api_key = config.getString("api_key");

        sender.sendMessage("Logging in via api_key...");

        if (api_key != null && !api_key.isEmpty()) {
            try {
                int expire = LoginViaKey(plugin.ServerAddress, api_key);
                sender.sendMessage("Login success. The session will expires after " + expire + " seconds.");
                plugin.login = new LoginTask(plugin, Bukkit.getConsoleSender()).runTaskLaterAsynchronously(plugin, expire * 20);
                sender.sendMessage("Automated login scheduled.");
                return;
            } catch (Exception e) {
                if (!e.getMessage().equals("Login failed!")) {
                    sender.sendMessage("Failed to connect to UnionBan server.");
                    sender.sendMessage("Failed to login!");
                    return;
                }
            }
        }

        sender.sendMessage("Cannot login via api_key. Trying username and password...");

        if (username != null && username.isEmpty()) {
            sender.sendMessage("Username is empty.");
            sender.sendMessage("Failed to login!");
            return;
        }

        try {
            int expire = LoginViaUsername(plugin.ServerAddress, username, password);
            sender.sendMessage("Login success. Updating api_key...");
            api_key = GetLoginStatus(plugin.ServerAddress).api_key;
            sender.sendMessage("Got new api_key: " + api_key + ". Saving to file...");
            config.set("api_key", api_key);
            plugin.saveConfig();
            sender.sendMessage("The session will expires after " + expire + " seconds.");
            plugin.login = new LoginTask(plugin, Bukkit.getConsoleSender()).runTaskLaterAsynchronously(plugin, expire * 20);
            sender.sendMessage("Automated login scheduled.");
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().equals("Login failed!")) {
                sender.sendMessage("Cannot login via username and password!");
            } else {
                sender.sendMessage("Failed to connect to UnionBan server.");
            }
            sender.sendMessage("Failed to login!");
        }
    }
}