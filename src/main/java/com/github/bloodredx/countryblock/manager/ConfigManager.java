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

            String defaultContent = new String(defaultConfigStream.readAllBytes());
            Map<String, List<String>> sectionComments = extractComments(defaultContent);
            List<String> sectionOrder = extractSectionOrder(defaultContent);
            
            Config defaultConfig = ConfigFactory.parseReader(new InputStreamReader(plugin.getResource(DEFAULT_CONFIG)));
            Config currentConfig = ConfigFactory.parseFile(configFile);

            StringBuilder newConfig = new StringBuilder();
            Map<String, Object> unknownSettings = new HashMap<>();

            for (String section : sectionOrder) {
                if (sectionComments.containsKey(section)) {
                    for (String comment : sectionComments.get(section)) {
                        newConfig.append(comment).append("\n");
                    }
                }
                
                newConfig.append(section).append(" {\n");
                processSection(currentConfig, defaultConfig, section, newConfig, unknownSettings, 1);
                newConfig.append("}\n\n");
            }
    
            if (!unknownSettings.isEmpty()) {
                newConfig.append("\n/* Deprecated or unknown configuration options:\n");
                unknownSettings.forEach((path, value) -> {
                    newConfig.append(formatUnknownSetting(path, value));
                });
                newConfig.append("*/\n");
            }
    
            Files.write(configFile.toPath(), newConfig.toString().getBytes());
    
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to update config: " + e.getMessage());
        }
    }

    private Map<String, List<String>> extractComments(String content) {
        Map<String, List<String>> comments = new HashMap<>();
        String[] lines = content.split("\n");
        List<String> currentComments = new ArrayList<>();
        String currentSection = null;
        
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("#")) {
                currentComments.add(line);
            } else if (line.contains("{")) {
                currentSection = line.split("\\{")[0].trim();
                if (!currentComments.isEmpty()) {
                    comments.put(currentSection, new ArrayList<>(currentComments));
                    currentComments.clear();
                }
            } else {
                currentComments.clear();
            }
        }
        return comments;
    }

    private List<String> extractSectionOrder(String content) {
        List<String> order = new ArrayList<>();
        Pattern pattern = Pattern.compile("^(\\w+)\\s*\\{", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            order.add(matcher.group(1));
        }
        return order;
    }

    private void processSection(Config current, Config defaultConfig, String section, StringBuilder output, 
                              Map<String, Object> unknownSettings, int depth) {
        Config sectionConfig = defaultConfig.getConfig(section);
        Config currentSectionConfig = current.hasPath(section) ? current.getConfig(section) : ConfigFactory.empty();
        String indent = "    ".repeat(depth);
        
        Set<String> allKeys = new HashSet<>();
        sectionConfig.entrySet().forEach(entry -> allKeys.add(entry.getKey()));
        if (currentSectionConfig != null) {
            currentSectionConfig.entrySet().forEach(entry -> allKeys.add(entry.getKey()));
        }
        
        Map<String, Config> nestedConfigs = new HashMap<>();
        for (String key : allKeys) {
            if (key.contains(".")) {
                String parentKey = key.substring(0, key.indexOf("."));
                if (!nestedConfigs.containsKey(parentKey)) {
                    nestedConfigs.put(parentKey, ConfigFactory.empty());
                }
            }
        }
        
        for (String key : allKeys) {
            if (!key.contains(".")) {
                if (nestedConfigs.containsKey(key)) {
                    output.append(indent).append(key).append(" {\n");
                    for (Map.Entry<String, Config> nested : nestedConfigs.entrySet()) {
                        if (nested.getKey().equals(key)) {
                            processNestedSection(currentSectionConfig, sectionConfig, key, output, depth + 1);
                        }
                    }
                    output.append(indent).append("}\n");
                } else {
                    Object value = currentSectionConfig.hasPath(key) ? 
                        currentSectionConfig.getValue(key).unwrapped() : 
                        sectionConfig.getValue(key).unwrapped();
                        
                    output.append(indent)
                          .append(key)
                          .append(" = ")
                          .append(formatValue(value))
                          .append("\n");
                }
            }
        }
    }
    
    private String formatValue(Object value) {
        if (value instanceof String) {
            return "\"" + value + "\"";
        } else if (value instanceof List) {
            return formatList((List<?>) value);
        }
        return String.valueOf(value);
    }
    
    private void processNestedSection(Config current, Config defaultConfig, String parentKey, StringBuilder output, int depth) {
        String indent = "    ".repeat(depth);
    
        Set<String> nestedKeys = new HashSet<>();
        defaultConfig.entrySet().forEach(entry -> {
            String key = entry.getKey();
            if (key.startsWith(parentKey + ".")) {
                nestedKeys.add(key.substring(key.indexOf(".") + 1));
            }
        });
        
        for (String key : nestedKeys) {
            String fullKey = parentKey + "." + key;
            Object value = current.hasPath(fullKey) ? 
                current.getValue(fullKey).unwrapped() : 
                defaultConfig.getValue(fullKey).unwrapped();
                
            output.append(indent)
                  .append(key)
                  .append(" = ")
                  .append(formatValue(value))
                  .append("\n");
        }
    }
    
    private String formatList(List<?> list) {
        if (list.isEmpty()) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[\n");
        for (Object item : list) {
            sb.append("        ").append(formatValue(item)).append(",\n");
        }
        sb.setLength(sb.length() - 2);
        sb.append("\n    ]");
        return sb.toString();
    }

    private String formatUnknownSetting(String path, Object value) {
        String[] parts = path.split("\\.");
        StringBuilder sb = new StringBuilder();
        String indent = "    ";
        
        for (int i = 0; i < parts.length - 1; i++) {
            sb.append(indent.repeat(i)).append(parts[i]).append(" {\n");
        }
        sb.append(indent.repeat(parts.length - 1))
          .append(parts[parts.length - 1])
          .append(" = ")
          .append(formatValue(value))
          .append("\n");
        
        for (int i = parts.length - 2; i >= 0; i--) {
            sb.append(indent.repeat(i)).append("}\n");
        }
        return sb.toString();
    }

    private String getIndent(int level) {
        return "    ".repeat(level);
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
