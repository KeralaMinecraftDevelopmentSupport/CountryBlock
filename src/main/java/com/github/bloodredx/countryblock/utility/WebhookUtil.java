package com.github.bloodredx.countryblock.utility;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;

public class WebhookUtil {
    private final WebhookClient client;

    public WebhookUtil(String webhookUrl) {
        if (webhookUrl != null && !webhookUrl.isEmpty()) {
            this.client = WebhookClient.withUrl(webhookUrl);
        } else {
            this.client = null;
        }
    }

    public void sendMessage(String title, String description, int color) {
        if (client == null) return;

        WebhookEmbed embed = new WebhookEmbedBuilder()
            .setTitle(new WebhookEmbed.EmbedTitle(title, null))
            .setDescription(description)
            .setColor(color)
            .build();

        try {
            client.send(embed).join();
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