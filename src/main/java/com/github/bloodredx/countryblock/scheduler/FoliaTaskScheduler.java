package com.github.bloodredx.countryblock.scheduler;

import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.TimeUnit;

public class FoliaTaskScheduler implements TaskScheduler {
    @Override
    public void runTask(JavaPlugin plugin, Runnable runnable) {
        Bukkit.getGlobalRegionScheduler().execute(plugin, runnable);
    }

    @Override
    public void runTaskLater(JavaPlugin plugin, Runnable runnable, long delay) {
        Bukkit.getGlobalRegionScheduler().runDelayed(plugin, task -> runnable.run(), delay);
    }

    @Override
    public void runTaskTimer(JavaPlugin plugin, Runnable runnable, long delay, long period) {
        Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, task -> runnable.run(), delay, period);
    }

    @Override
    public void runTaskAsynchronously(JavaPlugin plugin, Runnable runnable) {
        Bukkit.getAsyncScheduler().runNow(plugin, task -> runnable.run());
    }

    @Override
    public void runTaskLaterAsynchronously(JavaPlugin plugin, Runnable runnable, long delay) {
        Bukkit.getAsyncScheduler().runDelayed(plugin, task -> runnable.run(), delay * 50, TimeUnit.MILLISECONDS);
    }

    @Override
    public void cancelTasks(JavaPlugin plugin) {
        Bukkit.getGlobalRegionScheduler().cancelTasks(plugin);
        Bukkit.getAsyncScheduler().cancelTasks(plugin);
    }
}