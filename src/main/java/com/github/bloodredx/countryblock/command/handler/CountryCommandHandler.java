package com.github.bloodredx.countryblock.command.handler;

import com.github.bloodredx.countryblock.CountryBlock;
import com.github.bloodredx.countryblock.utility.*;
import org.bukkit.command.CommandSender;
import java.util.ArrayList;
import java.util.List;

public class CountryCommandHandler {
    private final CountryBlock plugin;

    public CountryCommandHandler(CountryBlock plugin) {
        this.plugin = plugin;
    }

    public void handleAdd(CommandSender sender, String country) {
        if (country == null || country.trim().isEmpty()) {
            sender.sendMessage(MessageUtil.error("Country/continent cannot be empty!"));
            return;
        }

        country = country.trim().toUpperCase();
        List<String> countryList = new ArrayList<>(plugin.getConfigManager().getCountryList());
        
        if (countryList.contains(country)) {
            sender.sendMessage(MessageUtil.info("This country/continent is already in the list!"));
            return;
        }
    
        if ((country.startsWith("C:")) && ContinentUtil.isContinent(country)) {
            countryList.add(country);
            plugin.getConfigManager().setCountryList(countryList);
            sender.sendMessage(MessageUtil.success("Continent added successfully!"));
            return;
        }
    
        if (!CountryUtil.isValidCountryCode(country) && !CountryUtil.isValidCountryName(country)) {
            sender.sendMessage(MessageUtil.error("Invalid country/continent format!"));
            return;
        }
    
        countryList.add(country);
        plugin.getConfigManager().setCountryList(countryList);
        plugin.getLogger().info("Added country/continent: " + country);
        sender.sendMessage(MessageUtil.success("Country added successfully!"));
    }
    
    public void handleRemove(CommandSender sender, String country) {
        if (country == null || country.trim().isEmpty()) {
            sender.sendMessage(MessageUtil.error("Country/continent cannot be empty!"));
            return;
        }

        country = country.trim().toUpperCase();
        List<String> countryList = new ArrayList<>(plugin.getConfigManager().getCountryList());
        
        if (!countryList.contains(country)) {
            sender.sendMessage(MessageUtil.info("This country/continent is not in the list!"));
            return;
        }
    
        countryList.remove(country);
        plugin.getConfigManager().setCountryList(countryList);
        plugin.getLogger().info("Removed country/continent: " + country);
        sender.sendMessage(MessageUtil.success("Country/continent removed successfully!"));
    }
}
