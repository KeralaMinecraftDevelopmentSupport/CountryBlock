package com.github.bloodredx.countryblock.utility;

import org.bukkit.ChatColor;

public class MessageUtil {
    public static final String PREFIX = ChatColor.translateAlternateColorCodes('&', "&0[&4&lCountryBlock&r&0] &r");
    
    public static String error(String message) {
        return PREFIX + ChatColor.RED + message;
    }
    
    public static String success(String message) {
        return PREFIX + ChatColor.GREEN + message;
    }
    
    public static String info(String message) {
        return PREFIX + ChatColor.BLUE + message;
    }
}
