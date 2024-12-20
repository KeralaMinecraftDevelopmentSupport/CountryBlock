package com.github.bloodredx.countryblock.config;

import com.typesafe.config.ConfigRenderOptions;
import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueFactory;

import java.util.List;
import java.util.Map;

public class ConfigSerializer {
    private static final ConfigRenderOptions OPTIONS = ConfigRenderOptions.defaults()
            .setOriginComments(false)
            .setJson(false);

    public static ConfigValue serializeCountryList(List<String> countries) {
        return ConfigValueFactory.fromIterable(countries);
    }

    public static String serializeConfig(Map<String, Object> configMap) {
        return ConfigValueFactory.fromMap(configMap).render(OPTIONS);
    }
}
