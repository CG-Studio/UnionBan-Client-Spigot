package cn.cnklp.studio.UnionBanClientSpigot;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

class MojangUserInfo {
    public String id;
}

public class PlayerUUID {
    public static boolean IsBan(String ServerAddress, String uuid) throws Exception {
        String Address = ServerAddress + "/record/uuid/" + uuid;
        URL url = new URL(Address);
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
        con.setReadTimeout(5000);
        con.setConnectTimeout(5000);
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestMethod("GET");
        con.setDoOutput(true);

        int responseCode = con.getResponseCode();
        con.disconnect();

        if (responseCode == 200) {
            return true;
        } else if (responseCode == 404) {
            return false;
        } else {
            throw new Exception("Failed to connect to UnionBan server!");
        }
    }

    public static String FromUsername(String username) throws Exception {
        String Address = "https://api.mojang.com/users/profiles/minecraft/" + username;
        URL url = new URL(Address);
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
        con.setReadTimeout(5000);
        con.setConnectTimeout(5000);
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestMethod("GET");
        con.setDoOutput(true);

        int responseCode = con.getResponseCode();
        if (responseCode == 204) {
            throw new Exception("Player does not exist!");
        } else if (responseCode != 200) {
            con.disconnect();
            throw new Exception("Failed to connect to Mojang API!");
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
        MojangUserInfo mojangUserInfo = objectMapper.readValue(builder.toString(), MojangUserInfo.class);

        return mojangUserInfo.id.substring(0, 8) + "-" + mojangUserInfo.id.substring(8, 12) + "-" + mojangUserInfo.id.substring(12, 16) + "-" + mojangUserInfo.id.substring(16, 20) + "-" + mojangUserInfo.id.substring(20, 32);
    }

    public static void Ban(String ServerAddress, String uuid) throws Exception {
        String Address = ServerAddress + "/record/uuid/" + uuid;
        URL url = new URL(Address);
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
        con.setReadTimeout(5000);
        con.setConnectTimeout(5000);
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestMethod("PUT");
        con.setDoOutput(true);

        con.connect();
        int responseCode = con.getResponseCode();
        con.disconnect();

        if (responseCode == 409) {
            throw new Exception("Record exists!");
        } else if (responseCode == 403) {
            throw new Exception("Not logged in!");
        } else if (responseCode != 200) {
            throw new Exception("Ban failed!");
        }
    }

    public static void Pardon(String ServerAddress, String uuid) throws Exception {
        String Address = ServerAddress + "/record/uuid/" + uuid;
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
        con.disconnect();

        if (responseCode == 404) {
            throw new Exception("Record does not exist!");
        } else if (responseCode == 403) {
            throw new Exception("Not logged in!");
        } else if (responseCode != 204) {
            throw new Exception("Pardon failed!");
        }
    }
}
