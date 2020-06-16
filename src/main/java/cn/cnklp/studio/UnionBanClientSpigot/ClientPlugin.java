package cn.cnklp.studio.UnionBanClientSpigot;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.Objects;

public class ClientPlugin extends JavaPlugin {
    public String ServerAddress = "https://api.unionban.icu";
    public BukkitTask login = null;

    @Override
    public void onEnable() {
        CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
        getServer().getPluginManager().registerEvents(new PlayerLoginListener(this), this);
        getServer().getPluginManager().registerEvents(new WhitelistListener(this), this);
        CommandsExecutor executor = new CommandsExecutor(this);
        Objects.requireNonNull(this.getCommand("unionban")).setExecutor(executor);
        Objects.requireNonNull(this.getCommand("unionban")).setTabCompleter(executor);
        saveDefaultConfig();
        new LoginTask(this, Bukkit.getConsoleSender()).runTaskAsynchronously(this);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }
}
