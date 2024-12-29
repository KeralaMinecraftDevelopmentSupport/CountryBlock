package com.github.bloodredx.countryblock.manager;

import com.github.bloodredx.countryblock.CountryBlock;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueFactory;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class ConfigManager {
    private final CountryBlock plugin;
    private Config config;
    private static final String CONFIG_FILE = "settings.conf";
    private static final String DEFAULT_CONFIG = "settings.conf";
    
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
            } else {
                updateConfig();
            }

            config = ConfigFactory.parseFile(configFile);
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

    private void updateConfig() {
        try {
            File configFile = new File(plugin.getDataFolder(), CONFIG_FILE);
            InputStream defaultConfigStream = plugin.getResource(DEFAULT_CONFIG);
            
            if (defaultConfigStream == null) {
                plugin.getLogger().warning("Default config not found in jar!");
                return;
            }

            Config defaultConfig = ConfigFactory.parseReader(new InputStreamReader(defaultConfigStream));
            Config currentConfig = ConfigFactory.parseFile(configFile);
            
            Config newConfig = defaultConfig.withFallback(currentConfig);
            
            String renderedConfig = newConfig.root().render();
            Files.write(configFile.toPath(), renderedConfig.getBytes());
            
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to update config: " + e.getMessage());
        }
    }

    public void saveConfig() {
        try {
            File configFile = new File(plugin.getDataFolder(), CONFIG_FILE);
            String renderedConfig = config.root().render();
            Files.write(configFile.toPath(), renderedConfig.getBytes());
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save config: " + e.getMessage());
        }
    }

    public boolean isEnableVpnCheck() {
        return enableVpnCheck;
    }

    public String getModeType() {
        return modeType;
    }

    public List<String> getCountryList() {
        return countryList;
    }

    public void setCountryList(List<String> newList) {
        this.countryList = newList;
        config = config.withValue("countries.list", ConfigValueFactory.fromIterable(newList));
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
