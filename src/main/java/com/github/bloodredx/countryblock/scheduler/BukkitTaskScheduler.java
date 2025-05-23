package com.github.bloodredx.countryblock.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitTaskScheduler implements TaskScheduler {
    @Override
    public void runTask(JavaPlugin plugin, Runnable runnable) {
        Bukkit.getScheduler().runTask(plugin, runnable);
    }

    @Override
    public void runTaskLater(JavaPlugin plugin, Runnable runnable, long delay) {
        Bukkit.getScheduler().runTaskLater(plugin, runnable, delay);
    }

    @Override
    public void runTaskTimer(JavaPlugin plugin, Runnable runnable, long delay, long period) {
        Bukkit.getScheduler().runTaskTimer(plugin, runnable, delay, period);
    }

    @Override
    public void runTaskAsynchronously(JavaPlugin plugin, Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
    }

    @Override
    public void runTaskLaterAsynchronously(JavaPlugin plugin, Runnable runnable, long delay) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, runnable, delay);
    }

    @Override
    public void cancelTasks(JavaPlugin plugin) {
        Bukkit.getScheduler().cancelTasks(plugin);
    }
}