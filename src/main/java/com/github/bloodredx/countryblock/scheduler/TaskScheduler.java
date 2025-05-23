package com.github.bloodredx.countryblock.scheduler;

import org.bukkit.plugin.java.JavaPlugin;

public interface TaskScheduler {
    void runTask(JavaPlugin plugin, Runnable runnable);
    void runTaskLater(JavaPlugin plugin, Runnable runnable, long delay);
    void runTaskTimer(JavaPlugin plugin, Runnable runnable, long delay, long period);
    void runTaskAsynchronously(JavaPlugin plugin, Runnable runnable);
    void runTaskLaterAsynchronously(JavaPlugin plugin, Runnable runnable, long delay);
    void cancelTasks(JavaPlugin plugin);
}