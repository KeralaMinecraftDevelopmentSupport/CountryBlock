package com.github.bloodredx.countryblock.listener;

import com.github.bloodredx.countryblock.CountryBlock;
import com.github.bloodredx.countryblock.utility.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import java.util.List;
import java.util.Set;

public class PlayerJoinListener implements Listener {
    private final CountryBlock plugin;
    private final ProxyCheckIO proxyCheckIO;
    private final WebhookUtil webhookUtil;

    public PlayerJoinListener(CountryBlock plugin) {
        this.plugin = plugin;
        this.proxyCheckIO = new ProxyCheckIO();
        this.webhookUtil = new WebhookUtil(plugin.getConfigManager().getWebhookUrl());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (event.getPlayer().isOp() || event.getPlayer().hasPermission("countryblock.admin")) {
            String currentVersion = plugin.getDescription().getVersion();
            String latestVersion = plugin.getUpdateChecker().getLatestVersion();
            
            if (latestVersion != null && !currentVersion.equals(latestVersion)) {
                event.getPlayer().sendMessage(MessageUtil.info("New version available: " + latestVersion));
                event.getPlayer().sendMessage(MessageUtil.info("Download URL: " + plugin.getUpdateChecker().getDownloadUrl()));
            }
        }
    }

    @EventHandler
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        String ip = event.getAddress().getHostAddress();
        String country = proxyCheckIO.getCountry(ip);
        String playerName = event.getName();
        
        if (country == null) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                MessageUtil.error("Unable to determine your country!"));
            return;
        }
    
        if (plugin.getConfigManager().isEnableVpnCheck() && proxyCheckIO.isVPN(ip)) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                MessageUtil.error("VPN/Proxy connections are not allowed!"));
            
            if (plugin.getConfigManager().isEnableNotifications()) {
                webhookUtil.sendMessage(
                    "VPN Detection",
                    String.format("Player: %s\nIP: %s\nCountry: %s\nReason: VPN/Proxy detected",
                        playerName, ip, country),
                    0xFF0000
                );
            }
            return;
        }
    
        List<String> configuredCountries = plugin.getConfigManager().getCountryList();
        boolean isAllowMode = "ALLOW".equalsIgnoreCase(plugin.getConfigManager().getModeType());
        boolean isCountryInList = false;
    
        for (String entry : configuredCountries) {
            if (ContinentUtil.isContinent(entry)) {
                String continentCode = entry.substring(2).toUpperCase();
                Set<String> countriesInContinent = ContinentUtil.getCountriesInContinent(continentCode);
                if (countriesInContinent != null && countriesInContinent.contains(country.toUpperCase())) {
                    isCountryInList = true;
                    break;
                }
            } else {
                String countryCode = CountryUtil.getCountryCode(entry);
                if (countryCode == null) {
                    countryCode = entry;
                }
                if (country.equalsIgnoreCase(countryCode)) {
                    isCountryInList = true;
                    break;
                }
            }
        }
    
        if (isAllowMode && !isCountryInList || !isAllowMode && isCountryInList) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                MessageUtil.error("Your country is not allowed to join this server!"));
            
            if (plugin.getConfigManager().isEnableNotifications()) {
                webhookUtil.sendMessage(
                    "Country Restriction",
                    String.format("Player: %s\nIP: %s\nCountry: %s\nMode: %s\nReason: Country %s",
                        playerName, ip, country,
                        isAllowMode ? "ALLOW" : "RESTRICT",
                        isAllowMode ? "not in allowlist" : "in restrictlist"),
                    0xFFA500
                );
            }

            plugin.getLogger().info("Country detected: " + country);
            plugin.getLogger().info("Mode: " + (isAllowMode ? "ALLOW" : "RESTRICT"));
            plugin.getLogger().info("Is country in list: " + isCountryInList);
        }
    }
}