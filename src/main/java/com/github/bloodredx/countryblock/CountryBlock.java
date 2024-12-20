package com.github.bloodredx.countryblock;

import com.github.bloodredx.countryblock.manager.CommandManager;
import com.github.bloodredx.countryblock.manager.ConfigManager;
import com.github.bloodredx.countryblock.manager.ListenerManager;
import com.github.bloodredx.countryblock.utility.UpdateChecker;
import org.bukkit.plugin.java.JavaPlugin;

public class CountryBlock extends JavaPlugin {
    private static CountryBlock instance;
    private ConfigManager configManager;
    private CommandManager commandManager;
    private UpdateChecker updateChecker;
    private ListenerManager listenerManager;
    
    @Override
    public void onEnable() {
        instance = this;
        this.configManager = new ConfigManager(this);
        this.commandManager = new CommandManager(this);
        this.listenerManager = new ListenerManager(this);
        configManager.loadConfig();
        this.updateChecker = new UpdateChecker(
            this,
            configManager.isIgnoreBeta(),
            configManager.isIgnoreAlpha()
        );
        
        updateChecker.checkForUpdates();
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
}
