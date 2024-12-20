package com.github.bloodredx.countryblock.command.handler;

import com.github.bloodredx.countryblock.CountryBlock;
import com.github.bloodredx.countryblock.utility.*;
import org.bukkit.command.CommandSender;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.List;

public class CountryCommandHandler {
    private final CountryBlock plugin;

    public CountryCommandHandler(CountryBlock plugin) {
        this.plugin = plugin;
    }

    public void handleAdd(CommandSender sender, String country) {
        List<String> countryList = plugin.getConfigManager().getCountryList();
        
        if (countryList.contains(country)) {
            sender.sendMessage(MessageUtil.info("This country/continent is already in the list!"));
            return;
        }
    
        if ((country.startsWith("c:") || country.startsWith("C:")) && ContinentUtil.isContinent(country)) {
            countryList.add(country);
            saveCountryList(countryList);
            sender.sendMessage(MessageUtil.success("Continent added successfully!"));
            return;
        }
    
        if (!CountryUtil.isValidCountryCode(country) && !CountryUtil.isValidCountryName(country)) {
            sender.sendMessage(MessageUtil.error("Invalid country/continent format!"));
            return;
        }
    
        countryList.add(country);
        saveCountryList(countryList);
        sender.sendMessage(MessageUtil.success("Country added successfully!"));
    }
    
    public void handleRemove(CommandSender sender, String country) {
        List<String> countryList = plugin.getConfigManager().getCountryList();
        
        if (!countryList.contains(country)) {
            sender.sendMessage(MessageUtil.info("This country/continent is not in the list!"));
            return;
        }
    
        countryList.remove(country);
        saveCountryList(countryList);
        sender.sendMessage(MessageUtil.success("Country/continent removed successfully!"));
    }
    
    private void saveCountryList(List<String> countryList) {
        try {
            File configFile = new File(plugin.getDataFolder(), "settings.conf");
            String content = new String(Files.readAllBytes(configFile.toPath()));
            
            Config config = ConfigFactory.parseString(content);
            
            String updatedContent = content.replaceAll(
                "(?m)^\\s*countries\\.list\\s*=\\s*\\[.*?\\]",
                "countries.list = " + countryList.toString()
            );
            
            Files.write(configFile.toPath(), updatedContent.getBytes());
            
            plugin.getConfigManager().loadConfig();
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save country list: " + e.getMessage());
        }
    }
}