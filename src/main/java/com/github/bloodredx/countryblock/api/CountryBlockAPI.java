package com.github.bloodredx.countryblock.api;

import com.github.bloodredx.countryblock.CountryBlock;
import com.github.bloodredx.countryblock.utility.ProxyCheckIO;

public class CountryBlockAPI {
    private final CountryBlock plugin;
    private final ProxyCheckIO proxyCheckIO;

    public CountryBlockAPI(CountryBlock plugin) {
        this.plugin = plugin;
        this.proxyCheckIO = new ProxyCheckIO();
    }

    public boolean isVpnOrProxy(String ip) {
        return proxyCheckIO.isVPN(ip);
    }

    public String getCountry(String ip) {
        return proxyCheckIO.getCountry(ip);
    }

    public boolean isCountryAllowed(String country) {
        boolean isAllowMode = "ALLOW".equalsIgnoreCase(plugin.getConfigManager().getModeType());
        boolean isInList = plugin.getConfigManager().getCountryList().contains(country);
        return isAllowMode ? isInList : !isInList;
    }
}
