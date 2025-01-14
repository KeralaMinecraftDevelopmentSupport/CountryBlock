package com.github.bloodredx.countryblock.utility;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;

import java.time.Instant;

public class WebhookUtil {
    private final WebhookClient client;
    private final String username;
    private final String avatarUrl;
    private final String footerText;
    private final String footerIconUrl;

    public WebhookUtil(String webhookUrl, String username, String avatarUrl, String footerText, String footerIconUrl) {
        if (webhookUrl != null && !webhookUrl.isEmpty()) {
            WebhookClientBuilder builder = new WebhookClientBuilder(webhookUrl);
            builder.setThreadFactory((job) -> {
                Thread thread = new Thread(job);
                thread.setName("CountryBlock Webhook");
                thread.setDaemon(true);
                return thread;
            });
            this.client = builder.build();
        } else {
            this.client = null;
        }
        
        this.username = username;
        this.avatarUrl = avatarUrl;
        this.footerText = footerText;
        this.footerIconUrl = footerIconUrl;
    }

    public void sendMessage(String title, String description, int color) {
        if (client == null) return;

        WebhookEmbed embed = new WebhookEmbedBuilder()
            .setTitle(new WebhookEmbed.EmbedTitle(title, null))
            .setDescription(description)
            .setColor(color)
            .setTimestamp(Instant.now())
            .setFooter(new WebhookEmbed.EmbedFooter(footerText, footerIconUrl))
            .build();

        WebhookMessageBuilder messageBuilder = new WebhookMessageBuilder()
            .setUsername(username)
            .setAvatarUrl(avatarUrl)
            .addEmbeds(embed);

        try {
            client.send(messageBuilder.build()).join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        if (client != null) {
            client.close();
        }
    }
}
