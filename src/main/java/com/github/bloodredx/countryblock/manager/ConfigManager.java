package com.github.bloodredx.countryblock.manager;

import com.github.bloodredx.countryblock.CountryBlock;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.util.*;

public class ConfigManager {
    private final CountryBlock plugin;
    private File configFile;
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
            if (!plugin.getDataFolder().exists()) {
                plugin.getDataFolder().mkdirs();
            }

            configFile = new File(plugin.getDataFolder(), CONFIG_FILE);
            
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
        webhookUsername = config.getString("discord.username", "CountryBlock");
        webhookAvatarUrl = config.getString("discord.avatar-url", "https://cdn.modrinth.com/data/d4ZMML0z/8c558c50d7a69f4d2e9cc5e43bfd6e1d18000086_96.webp");
        webhookFooterText = config.getString("discord.footer-text", "CountryBlock Plugin");
        webhookFooterIconUrl = config.getString("discord.footer-icon-url", "");
    }

    public void saveConfig() {
        try {
            config.set("anti-vpn.enable", enableVpnCheck);
            config.set("mode.type", modeType);
            config.set("countries.list", countryList);
            config.set("discord.webhook-url", webhookUrl);
            config.set("discord.enable-notifications", enableNotifications);
            config.set("updates.check-enabled", updateCheckEnabled);
            config.set("updates.ignore-alpha", ignoreAlpha);
            config.set("updates.ignore-beta", ignoreBeta);
            
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save config: " + e.getMessage());
        }
    }

    public String getWebhookUsername() {
        return webhookUsername;
    }
    
    public String getWebhookAvatarUrl() {
        return webhookAvatarUrl;
    }
    
    public String getWebhookFooterText() {
        return webhookFooterText;
    }
    
    public String getWebhookFooterIconUrl() {
        return webhookFooterIconUrl;
    }

    public boolean isEnableVpnCheck() {
        return enableVpnCheck;
    }

    public String getModeType() {
        return modeType;
    }

    public List<String> getCountryList() {
        return new ArrayList<>(countryList);
    }

    public void setCountryList(List<String> newList) {
        this.countryList = new ArrayList<>(newList);
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

    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
        loadConfigValues();
    }
}
