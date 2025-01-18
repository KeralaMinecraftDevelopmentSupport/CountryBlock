package com.github.bloodredx.countryblock.utility;

import com.eduardomcb.discord.webhook.*;
import com.eduardomcb.discord.webhook.models.*;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WebhookUtil {
    private final String webhookUrl;
    private final String username;
    private final String avatarUrl;
    private final String footerText;
    private final String footerIconUrl;

    public WebhookUtil(String webhookUrl, String username, String avatarUrl, String footerText, String footerIconUrl) {
        this.webhookUrl = webhookUrl;
        this.username = username;
        this.avatarUrl = avatarUrl;
        this.footerText = footerText;
        this.footerIconUrl = footerIconUrl;
    }

    public void sendMessage(String title, String description, int color) {
        if (webhookUrl == null || webhookUrl.isEmpty()) return;

        try {
            Message message = new Message()
                .setUsername(username)
                .setAvatarUrl(avatarUrl)
                .setContent("(beta)");
            Date currentDate = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            String formattedTimestamp = formatter.format(currentDate);
            Embed embed = new Embed()
                .setTitle(title)
                .setDescription(description)
                .setColor(color)
                .setTimestamp(formattedTimestamp)
                .setFooter(new Footer(footerText, footerIconUrl));
            new WebhookManager()
                .setChannelUrl(webhookUrl)
                .setMessage(message)
                .setEmbeds(new Embed[]{embed})
                .setListener(new WebhookClient.Callback() {
                    @Override
                    public void onSuccess(String response) {
                        // Success
                    }

                    @Override
                    public void onFailure(int statusCode, String errorMessage) {
                        System.out.println("Webhook Error - Code: " + statusCode + " Message: " + errorMessage);
                    }
                })
                .exec();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
