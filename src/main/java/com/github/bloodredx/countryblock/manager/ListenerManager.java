package com.github.bloodredx.countryblock.manager;

import com.github.bloodredx.countryblock.CountryBlock;
import com.github.bloodredx.countryblock.listener.PlayerJoinListener;
import org.bukkit.plugin.PluginManager;

public class ListenerManager {
    private final CountryBlock plugin;

    public ListenerManager(CountryBlock plugin) {
        this.plugin = plugin;
        registerListeners();
    }

    private void registerListeners() {
        PluginManager pm = plugin.getServer().getPluginManager();
        pm.registerEvents(new PlayerJoinListener(plugin), plugin);
    }
}
