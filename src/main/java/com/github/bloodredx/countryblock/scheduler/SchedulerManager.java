package com.github.bloodredx.countryblock.scheduler;

import com.github.bloodredx.countryblock.CountryBlock;
import org.bukkit.plugin.java.JavaPlugin;

public class SchedulerManager {
    private final TaskScheduler scheduler;
    private final CountryBlock plugin;
    private final boolean isFolia;

    public SchedulerManager(CountryBlock plugin) {
        this.plugin = plugin;
        this.isFolia = isFoliaDetected();
        
        if (isFolia) {
            plugin.getLogger().info("Folia detected! Using Folia scheduler.");
            this.scheduler = new FoliaTaskScheduler();
        } else {
            plugin.getLogger().info("Using Bukkit scheduler.");
            this.scheduler = new BukkitTaskScheduler();
        }
    }

    private boolean isFoliaDetected() {
        try {
            Class.forName("io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public void runTask(Runnable runnable) {
        scheduler.runTask(plugin, runnable);
    }

    public void runTaskLater(Runnable runnable, long delay) {
        scheduler.runTaskLater(plugin, runnable, delay);
    }

    public void runTaskTimer(Runnable runnable, long delay, long period) {
        scheduler.runTaskTimer(plugin, runnable, delay, period);
    }

    public void runTaskAsynchronously(Runnable runnable) {
        scheduler.runTaskAsynchronously(plugin, runnable);
    }

    public void runTaskLaterAsynchronously(Runnable runnable, long delay) {
        scheduler.runTaskLaterAsynchronously(plugin, runnable, delay);
    }

    public void cancelTasks() {
        scheduler.cancelTasks(plugin);
    }

    public TaskScheduler getScheduler() {
        return scheduler;
    }

    public boolean isFolia() {
        return isFolia;
    }
}