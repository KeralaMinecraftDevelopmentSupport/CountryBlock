package com.github.bloodredx.countryblock.manager;

import com.github.bloodredx.countryblock.CountryBlock;
import com.github.bloodredx.countryblock.metrics.Metrics;

import java.util.HashMap;
import java.util.Map;

public class MetricsManager {
    private final CountryBlock plugin;
    private final int BSTATS_ID = 24151;

    public MetricsManager(CountryBlock plugin) {
        this.plugin = plugin;
        setupMetrics();
    }

    private void setupMetrics() {
        Metrics metrics = new Metrics(plugin, BSTATS_ID);
        
        metrics.addCustomChart(new Metrics.SimplePie("mode_type", () -> 
            plugin.getConfigManager().getModeType()));
    }
}
