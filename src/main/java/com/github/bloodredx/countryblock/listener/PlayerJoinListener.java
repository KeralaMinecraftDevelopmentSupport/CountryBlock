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
        this.webhookUtil = plugin.getWebhookUtil();
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

    private boolean isLocalhost(String ip) {
        return ip.equals("127.0.0.1") || 
               ip.equals("localhost") || 
               ip.equals("0:0:0:0:0:0:0:1") || 
               ip.equals("::1") ||
               ip.startsWith("192.168.") ||
               ip.startsWith("10.") ||
               ip.startsWith("172.");
    }

    @EventHandler
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        String ip = event.getAddress().getHostAddress();
        String playerName = event.getName();
        
        if (isLocalhost(ip)) {
            plugin.getLogger().info("Allowing local connection from " + playerName + " (" + ip + ")");
            return;
        }
        
        String country = proxyCheckIO.getCountry(ip);
        
        if (country == null) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                MessageUtil.error("Unable to determine your country!"));
            
            if (plugin.getConfigManager().isEnableNotifications()) {
                webhookUtil.sendStructuredMessage(
                    "⚠️ Country Detection Failed",
                    playerName,
                    ip,
                    "Unknown",
                    "Unable to determine country from IP address",
                    null,
                    0xFF0000
                );
            }
            return;
        }

        if (plugin.getConfigManager().isEnableVpnCheck() && proxyCheckIO.isVPN(ip)) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                MessageUtil.error("VPN/Proxy connections are not allowed!"));
            
            if (plugin.getConfigManager().isEnableNotifications()) {
                webhookUtil.sendStructuredMessage(
                    "🚫 VPN/Proxy Detected",
                    playerName,
                    ip,
                    country,
                    "VPN or Proxy connection detected and blocked",
                    null,
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
                String reason = isAllowMode ? 
                    "Country not in allowlist" : 
                    "Country in restriction list";
                    
                webhookUtil.sendStructuredMessage(
                    "🌍 Country Restriction",
                    playerName,
                    ip,
                    country,
                    reason,
                    isAllowMode ? "ALLOWLIST" : "RESTRICTLIST",
                    0xFFA500
                );
            }

            plugin.getLogger().info("Blocked player from " + country + " (Mode: " + 
                (isAllowMode ? "ALLOW" : "RESTRICT") + ", In list: " + isCountryInList + ")");
        }
    }
}