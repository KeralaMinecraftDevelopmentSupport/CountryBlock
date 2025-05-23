package com.github.bloodredx.countryblock.utility;

import com.github.bloodredx.countryblock.CountryBlock;
import org.bukkit.Bukkit;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class UpdateChecker {
    private final CountryBlock plugin;
    private final boolean ignoreBeta;
    private final boolean ignoreAlpha;
    private final HttpClient httpClient;
    private String latestVersion = null;
    private String downloadUrl = null;
    
    private static final String PROJECT_ID = "CountryBlock";

    public UpdateChecker(CountryBlock plugin, boolean ignoreBeta, boolean ignoreAlpha) {
        this.plugin = plugin;
        this.ignoreBeta = ignoreBeta;
        this.ignoreAlpha = ignoreAlpha;
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    }

    public void checkForUpdates() {
        if (!plugin.getConfigManager().isUpdateCheckEnabled()) return;
    
        plugin.getSchedulerManager().runTaskAsynchronously(() -> {
            try {
                String url = "https://api.modrinth.com/v2/project/" + PROJECT_ID + "/version";
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "CountryBlock/" + plugin.getDescription().getVersion())
                    .timeout(Duration.ofSeconds(30))
                    .GET()
                    .build();
    
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                int statusCode = response.statusCode();
                
                if (statusCode == 404) {
                    plugin.getLogger().warning("Project not found on Modrinth. Please check the project ID.");
                    return;
                }
                
                if (statusCode == 200) {
                    String jsonResponse = response.body();
                    JSONArray versions = new JSONArray(jsonResponse);
                    String currentVersion = plugin.getDescription().getVersion();
                    
                    for (int i = 0; i < versions.length(); i++) {
                        JSONObject version = versions.getJSONObject(i);
                        String versionType = version.getString("version_type").toLowerCase();
                        
                        if ((ignoreBeta && versionType.equals("beta")) || 
                            (ignoreAlpha && versionType.equals("alpha"))) {
                            continue;
                        }
    
                        if (latestVersion == null) {
                            latestVersion = version.getString("version_number");
                            downloadUrl = version.getJSONArray("files")
                                .getJSONObject(0)
                                .getString("url");
                            break;
                        }
                    }
    
                    if (latestVersion != null && !currentVersion.equals(latestVersion)) {
                        plugin.getLogger().info("New version available: " + latestVersion);
                        plugin.getLogger().info("Download URL: " + downloadUrl);
                    }
                } else {
                    plugin.getLogger().warning("Failed to check for updates. Status code: " + statusCode);
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Unable to check for updates: " + e.getMessage());
            }
        });
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }
}