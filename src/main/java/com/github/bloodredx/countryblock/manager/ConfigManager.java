package com.github.bloodredx.countryblock.manager;

import com.github.bloodredx.countryblock.CountryBlock;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.util.*;

public class ConfigManager {
    private final CountryBlock plugin;
    private FileConfiguration config;
    private static final String CONFIG_FILE = "config.yml";
    
    private boolean enableVpnCheck;
    private String modeType;
    private List<String> countryList;
    private String webhookUrl;
    private boolean enableNotifications;
    private boolean updateCheckEnabled;
    private boolean ignoreAlpha;
    private boolean ignoreBeta;

    public ConfigManager(CountryBlock plugin) {
        this.plugin = plugin;
    }

    public void loadConfig() {
        try {
            File configFile = new File(plugin.getDataFolder(), CONFIG_FILE);
            
            if (!plugin.getDataFolder().exists()) {
                plugin.getDataFolder().mkdirs();
            }

            if (!configFile.exists()) {
                plugin.saveResource(CONFIG_FILE, false);
            }

            config = YamlConfiguration.loadConfiguration(configFile);
            loadConfigValues();
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to load config: " + e.getMessage());
        }
    }

    private void loadConfigValues() {
        enableVpnCheck = config.getBoolean("anti-vpn.enable");
        modeType = config.getString("mode.type");
        countryList = config.getStringList("countries.list");
        webhookUrl = config.getString("discord.webhook-url");
        enableNotifications = config.getBoolean("discord.enable-notifications");
        updateCheckEnabled = config.getBoolean("updates.check-enabled");
        ignoreAlpha = config.getBoolean("updates.ignore-alpha");
        ignoreBeta = config.getBoolean("updates.ignore-beta");
    }

    public void saveConfig() {
        try {
            File configFile = new File(plugin.getDataFolder(), CONFIG_FILE);
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save config: " + e.getMessage());
        }
    }

    public void setCountryList(List<String> newList) {
        this.countryList = newList;
        config.set("countries.list", newList);
        saveConfig();
    }

    public String getWebhookUrl() {
        return webhookUrl;
    }

    public boolean isEnableNotifications() {
        return enableNotifications;
    }

    public boolean isUpdateCheckEnabled() {
        return updateCheckEnabled;
    }

    public boolean isIgnoreAlpha() {
        return ignoreAlpha;
    }

    public boolean isIgnoreBeta() {
        return ignoreBeta;
    }
}
