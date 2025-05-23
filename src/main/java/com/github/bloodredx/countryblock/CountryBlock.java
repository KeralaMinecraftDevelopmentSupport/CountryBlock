package com.github.bloodredx.countryblock;

import com.github.bloodredx.countryblock.manager.CommandManager;
import com.github.bloodredx.countryblock.manager.ConfigManager;
import com.github.bloodredx.countryblock.manager.ListenerManager;
import com.github.bloodredx.countryblock.manager.MetricsManager;
import com.github.bloodredx.countryblock.scheduler.SchedulerManager;
import com.github.bloodredx.countryblock.utility.UpdateChecker;
import com.github.bloodredx.countryblock.utility.WebhookUtil;
import org.bukkit.plugin.java.JavaPlugin;

public class CountryBlock extends JavaPlugin {
    private static CountryBlock instance;
    private ConfigManager configManager;
    private CommandManager commandManager;
    private UpdateChecker updateChecker;
    private ListenerManager listenerManager;
    private MetricsManager metricsManager;
    private WebhookUtil webhookUtil;
    private SchedulerManager schedulerManager;
    
    @Override
    public void onEnable() {
        instance = this;
        this.configManager = new ConfigManager(this);
        configManager.loadConfig();
        this.schedulerManager = new SchedulerManager(this);
        this.webhookUtil = new WebhookUtil(configManager.getWebhookUrl(), this);
        this.commandManager = new CommandManager(this);
        this.listenerManager = new ListenerManager(this);
        this.updateChecker = new UpdateChecker(
            this,
            configManager.isIgnoreBeta(),
            configManager.isIgnoreAlpha()
        );
        this.metricsManager = new MetricsManager(this);
        
        updateChecker.checkForUpdates();
    }

    @Override
    public void onDisable() {
        if (schedulerManager != null) {
            schedulerManager.cancelTasks();
        }
    }

    public static CountryBlock getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public UpdateChecker getUpdateChecker() {
        return updateChecker;
    }

    public MetricsManager getMetricsManager() {
        return metricsManager;
    }

    public WebhookUtil getWebhookUtil() {
        return webhookUtil;
    }

    public SchedulerManager getSchedulerManager() {
        return schedulerManager;
    }
}