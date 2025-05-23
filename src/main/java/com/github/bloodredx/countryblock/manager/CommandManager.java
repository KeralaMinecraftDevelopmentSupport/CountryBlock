package com.github.bloodredx.countryblock.manager;

import com.github.bloodredx.countryblock.CountryBlock;
import com.github.bloodredx.countryblock.command.CountryBlockCommand;
import org.bukkit.command.PluginCommand;

public class CommandManager {
    private final CountryBlock plugin;

    public CommandManager(CountryBlock plugin) {
        this.plugin = plugin;
        registerCommands();
    }

    private void registerCommands() {
        PluginCommand command = plugin.getCommand("countryblock");
        if (command != null) {
            CountryBlockCommand executor = new CountryBlockCommand(plugin);
            command.setExecutor(executor);
            command.setTabCompleter(executor);
        }
    }
}
