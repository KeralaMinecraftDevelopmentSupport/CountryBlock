package com.github.bloodredx.countryblock.utility;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class IPCache {
    private static final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private static final long CACHE_DURATION = 3600000;

    static class CacheEntry {
        final String country;
        final boolean isVpn;
        final long timestamp;

        CacheEntry(String country, boolean isVpn) {
            this.country = country;
            this.isVpn = isVpn;
            this.timestamp = System.currentTimeMillis();
        }

        boolean isExpired() {
            return System.currentTimeMillis() - timestamp > CACHE_DURATION;
        }
    }

    public static void addToCache(String ip, String country, boolean isVpn) {
        cache.put(ip, new CacheEntry(country, isVpn));
    }

    public static CacheEntry getFromCache(String ip) {
        CacheEntry entry = cache.get(ip);
        if (entry != null && entry.isExpired()) {
            cache.remove(ip);
            return null;
        }
        return entry;
    }

    public static void clearCache() {
        cache.clear();
    }
}
