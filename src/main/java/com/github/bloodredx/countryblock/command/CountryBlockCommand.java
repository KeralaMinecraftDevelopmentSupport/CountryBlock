package com.github.bloodredx.countryblock.command;

import com.github.bloodredx.countryblock.CountryBlock;
import com.github.bloodredx.countryblock.utility.MessageUtil;
import com.github.bloodredx.countryblock.command.handler.CountryCommandHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CountryBlockCommand implements CommandExecutor, TabCompleter {
    private final CountryBlock plugin;
    private final CountryCommandHandler commandHandler;
    private final List<String> subCommands = Arrays.asList("reload", "add", "remove", "help", "update", "list");

    public CountryBlockCommand(CountryBlock plugin) {
        this.plugin = plugin;
        this.commandHandler = new CountryCommandHandler(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("countryblock.admin")) {
            sender.sendMessage(MessageUtil.error("You don't have permission to use this command!"));
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                if (!sender.hasPermission("countryblock.admin.reload")) {
                    sender.sendMessage(MessageUtil.error("You don't have permission to use reload command!"));
                } else {
                    plugin.getConfigManager().loadConfig();
                    sender.sendMessage(MessageUtil.success("Configuration reloaded!"));
                }
                break;
            case "add":
                if (args.length < 2) {
                    sender.sendMessage(MessageUtil.info("Usage: /countryblock add <country/continent>"));
                    return true;
                }
                if (!sender.hasPermission("countryblock.admin.add")) {
                    sender.sendMessage(MessageUtil.error("You don't have permission to use add command!"));
                    return true;
                }
                commandHandler.handleAdd(sender, args[1].toUpperCase());
                break;
            case "remove":
                if (args.length < 2) {
                    sender.sendMessage(MessageUtil.info("Usage: /countryblock remove <country/continent>"));
                    return true;
                }
                if (!sender.hasPermission("countryblock.admin.remove")) {
                    sender.sendMessage(MessageUtil.error("You don't have permission to use remove command!"));
                    return true;
                }
                commandHandler.handleRemove(sender, args[1].toUpperCase());
                break;
            case "update":
                if (!sender.hasPermission("countryblock.admin.update")) {
                    sender.sendMessage(MessageUtil.error("You don't have permission to use update command!"));
                    return true;
                }
                String currentVersion = plugin.getDescription().getVersion();
                String latestVersion = plugin.getUpdateChecker().getLatestVersion();
                
                if (latestVersion == null) {
                    sender.sendMessage(MessageUtil.error("Failed to check for updates!"));
                    return true;
                }
                
                if (currentVersion.equals(latestVersion)) {
                    sender.sendMessage(MessageUtil.success("You are running the latest version!"));
                } else {
                    sender.sendMessage(MessageUtil.info("New version available: " + latestVersion));
                    sender.sendMessage(MessageUtil.info("Download URL: " + plugin.getUpdateChecker().getDownloadUrl()));
                }
                break;
            case "list":
                if (!sender.hasPermission("countryblock.admin.list")) {
                    sender.sendMessage(MessageUtil.error("You don't have permission to use list command!"));
                    return true;
                }
                List<String> countryList = plugin.getConfigManager().getCountryList();
                String mode = plugin.getConfigManager().getModeType();
                
                sender.sendMessage(MessageUtil.info("Current Mode: " + mode));
                sender.sendMessage(MessageUtil.info("Countries/Continents in list:"));
                if (countryList.isEmpty()) {
                    sender.sendMessage(MessageUtil.info("- No countries/continents in list"));
                } else {
                    countryList.forEach(country -> 
                        sender.sendMessage(MessageUtil.info("- " + country))
                    );
                }
                break;
            default:
                sendHelp(sender);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return subCommands.stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return null;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(MessageUtil.info("Available Commands:"));
        sender.sendMessage(MessageUtil.info("/countryblock reload - Reload configuration"));
        sender.sendMessage(MessageUtil.info("/countryblock add <country/continent> - Add country/continent"));
        sender.sendMessage(MessageUtil.info("/countryblock remove <country/continent> - Remove country/continent"));
        sender.sendMessage(MessageUtil.info("/countryblock list - Show country/continent list"));
        sender.sendMessage(MessageUtil.info("/countryblock update - Check for updates"));
    }
}