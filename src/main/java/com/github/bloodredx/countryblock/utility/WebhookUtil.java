package com.github.bloodredx.countryblock.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONArray;
import org.json.JSONObject;
import com.github.bloodredx.countryblock.CountryBlock;

public class WebhookUtil {
    private final String webhookUrl;
    private final CountryBlock plugin;

    public WebhookUtil(String webhookUrl, JavaPlugin plugin) {
        this.webhookUrl = webhookUrl;
        this.plugin = (CountryBlock) plugin;
    }

    public void sendMessage(String title, String description, int color) {
        if (webhookUrl == null || webhookUrl.isEmpty()) {
            plugin.getLogger().warning("Webhook URL is not configured!");
            return;
        }

        JSONObject embed = new JSONObject();
        if (title != null && !title.isEmpty()) {
            embed.put("title", title);
        }
        if (description != null && !description.isEmpty()) {
            embed.put("description", description);
        }
        embed.put("color", color);

        JSONObject payload = createBasePayload();
        JSONArray embeds = new JSONArray();
        embeds.put(embed);
        payload.put("embeds", embeds);

        executeWebhook(payload);
    }

    public void sendStructuredMessage(String title, String playerName, String ip, String country, String reason, String mode, int color) {
        if (webhookUrl == null || webhookUrl.isEmpty()) {
            plugin.getLogger().warning("Webhook URL is not configured!");
            return;
        }

        JSONObject embed = createEmbed();
        setEmbedTitle(embed, title);
        setEmbedColor(embed, color);
        setEmbedTimestamp(embed, java.time.Instant.now().toString());
        addEmbedField(embed, "👤 Player", playerName, true);
        addEmbedField(embed, "🌐 IP Address", ip, true);
        addEmbedField(embed, "🏳️ Country", country, true);
        
        if (mode != null) {
            addEmbedField(embed, "⚙️ Mode", mode, true);
        }
        
        addEmbedField(embed, "📝 Reason", reason, false);
        String footerText = plugin.getConfigManager().getWebhookFooterText();
        String footerIconUrl = plugin.getConfigManager().getWebhookFooterIconUrl();
        setEmbedFooter(embed, footerText, footerIconUrl.isEmpty() ? null : footerIconUrl);

        JSONObject payload = createBasePayload();
        JSONArray embeds = new JSONArray();
        embeds.put(embed);
        payload.put("embeds", embeds);

        executeWebhook(payload);
    }

    private JSONObject createBasePayload() {
        JSONObject payload = new JSONObject();
        
        String username = plugin.getConfigManager().getWebhookUsername();
        String avatarUrl = plugin.getConfigManager().getWebhookAvatarUrl();
        
        if (username != null && !username.isEmpty()) {
            payload.put("username", username);
        }
        
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            payload.put("avatar_url", avatarUrl);
        }
        
        return payload;
    }

    public WebhookBuilder createWebhook() {
        return new WebhookBuilder();
    }

    public class WebhookBuilder {
        private final JSONObject payload;

        public WebhookBuilder() {
            this.payload = createBasePayload();
        }

        public WebhookBuilder setUsername(String username) {
            if (username != null && !username.isEmpty()) {
                payload.put("username", username);
            }
            return this;
        }

        public WebhookBuilder setAvatarUrl(String avatarUrl) {
            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                payload.put("avatar_url", avatarUrl);
            }
            return this;
        }

        public WebhookBuilder setContent(String content) {
            if (content != null && !content.isEmpty()) {
                payload.put("content", content);
            }
            return this;
        }

        public WebhookBuilder addEmbed(JSONObject embed) {
            if (!payload.has("embeds")) {
                payload.put("embeds", new JSONArray());
            }
            payload.getJSONArray("embeds").put(embed);
            return this;
        }

        public void execute() {
            executeWebhook(payload);
        }
    }

    public JSONObject createEmbed() {
        return new JSONObject();
    }

    public JSONObject setEmbedTitle(JSONObject embed, String title) {
        if (title != null && !title.isEmpty()) {
            embed.put("title", title);
        }
        return embed;
    }

    public JSONObject setEmbedDescription(JSONObject embed, String description) {
        if (description != null && !description.isEmpty()) {
            embed.put("description", description);
        }
        return embed;
    }

    public JSONObject setEmbedColor(JSONObject embed, int color) {
        embed.put("color", color);
        return embed;
    }

    public JSONObject setEmbedTimestamp(JSONObject embed, String timestamp) {
        if (timestamp != null && !timestamp.isEmpty()) {
            embed.put("timestamp", timestamp);
        }
        return embed;
    }

    public JSONObject addEmbedField(JSONObject embed, String name, String value, boolean inline) {
        if (!embed.has("fields")) {
            embed.put("fields", new JSONArray());
        }

        JSONObject field = new JSONObject();
        field.put("name", name);
        field.put("value", value);
        field.put("inline", inline);

        embed.getJSONArray("fields").put(field);
        return embed;
    }

    public JSONObject setEmbedFooter(JSONObject embed, String text, String iconUrl) {
        JSONObject footer = new JSONObject();
        footer.put("text", text);

        if (iconUrl != null && !iconUrl.isEmpty()) {
            footer.put("icon_url", iconUrl);
        }

        embed.put("footer", footer);
        return embed;
    }

    public JSONObject setEmbedAuthor(JSONObject embed, String name, String url, String iconUrl) {
        JSONObject author = new JSONObject();
        author.put("name", name);
        
        if (url != null && !url.isEmpty()) {
            author.put("url", url);
        }
        
        if (iconUrl != null && !iconUrl.isEmpty()) {
            author.put("icon_url", iconUrl);
        }
        
        embed.put("author", author);
        return embed;
    }

    public JSONObject setEmbedThumbnail(JSONObject embed, String url) {
        if (url != null && !url.isEmpty()) {
            JSONObject thumbnail = new JSONObject();
            thumbnail.put("url", url);
            embed.put("thumbnail", thumbnail);
        }
        return embed;
    }

    public JSONObject setEmbedImage(JSONObject embed, String url) {
        if (url != null && !url.isEmpty()) {
            JSONObject image = new JSONObject();
            image.put("url", url);
            embed.put("image", image);
        }
        return embed;
    }

    private void executeWebhook(JSONObject payload) {
        if (webhookUrl == null || webhookUrl.isEmpty()) {
            plugin.getLogger().warning("Webhook URL is not configured!");
            return;
        }
    
        plugin.getSchedulerManager().runTaskAsynchronously(() -> {
            try {
                URL url = new URI(webhookUrl).toURL();
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("User-Agent", "CountryBlock Plugin");
                connection.setDoOutput(true);
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
    
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = payload.toString().getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
    
                int responseCode = connection.getResponseCode();
    
                StringBuilder response = new StringBuilder();
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(
                                responseCode >= 400 ? connection.getErrorStream() : connection.getInputStream(),
                                StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }
                }
    
                if (responseCode >= 200 && responseCode < 300) {
                    plugin.getLogger().fine("Webhook sent successfully (Response: " + responseCode + ")");
                } else {
                    plugin.getLogger().warning("Failed to send webhook: " + responseCode + " - " + response.toString());
                }
    
            } catch (IOException | URISyntaxException e) {
                plugin.getLogger().severe("Error sending webhook: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    public void close() {
    }
}