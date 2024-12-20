package com.github.bloodredx.countryblock.utility;

import com.github.bloodredx.countryblock.CountryBlock;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.bukkit.Bukkit;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class UpdateChecker {
    private final CountryBlock plugin;
    private final boolean ignoreBeta;
    private final boolean ignoreAlpha;
    private String latestVersion = null;
    private String downloadUrl = null;
    
    private static final String PROJECT_ID = "YOUR_PROJECT_ID_HERE";

    public UpdateChecker(CountryBlock plugin, boolean ignoreBeta, boolean ignoreAlpha) {
        this.plugin = plugin;
        this.ignoreBeta = ignoreBeta;
        this.ignoreAlpha = ignoreAlpha;
    }

    public void checkForUpdates() {
        if (!plugin.getConfigManager().isUpdateCheckEnabled()) return;

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (CloseableHttpClient client = HttpClients.createDefault()) {
                HttpGet request = new HttpGet("https://api.modrinth.com/v2/project/" + PROJECT_ID + "/version");
                request.setHeader("User-Agent", "CountryBlock/" + plugin.getDescription().getVersion());

                HttpResponse response = client.execute(request);
                int statusCode = response.getStatusLine().getStatusCode();
                
                if (statusCode == HttpStatus.SC_NOT_FOUND) {
                    plugin.getLogger().warning("Project not found on Modrinth. Please check the project ID.");
                    return;
                }
                
                if (statusCode == HttpStatus.SC_OK) {
                    String jsonResponse = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
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
            } catch (IOException e) {
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