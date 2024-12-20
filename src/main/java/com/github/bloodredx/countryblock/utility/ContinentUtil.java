package com.github.bloodredx.countryblock.utility;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ContinentUtil {
    private static final Map<String, Set<String>> CONTINENT_COUNTRIES = new HashMap<>();

    static {
        CONTINENT_COUNTRIES.put("EU", Set.of(
            "GB", "FR", "DE", "IT", "ES", "NL", "BE", "SE", "PL", "FI", 
            "NO", "DK", "GR", "AT", "IE", "PT", "CZ", "HU", "CH", "RU"
        ));

        CONTINENT_COUNTRIES.put("AS", Set.of(
            "CN", "JP", "IN", "KR", "SG", "TH", "VN", "MY", "PH", "PK", 
            "BD", "LK", "ID", "IR", "IQ", "IL", "SA", "TR", "AE"
        ));

        CONTINENT_COUNTRIES.put("NA", Set.of(
            "US", "CA", "MX", "GT", "CR", "PA", "CU", "DO", "HT", "JM", "BZ"
        ));

        CONTINENT_COUNTRIES.put("SA", Set.of(
            "BR", "AR", "CO", "VE", "CL", "PE", "UY", "PY", "BO", "EC", "GY", "SR"
        ));

        CONTINENT_COUNTRIES.put("AF", Set.of(
            "ZA", "EG", "NG", "KE", "GH", "DZ", "MA", "ET", "TZ", "UG", 
            "SN", "CI", "CM", "ZW", "RW", "SD", "SO", "LY"
        ));

        CONTINENT_COUNTRIES.put("OC", Set.of(
            "AU", "NZ", "FJ", "PG", "WS", "SB", "VU", "TO", "TV", "NR", 
            "MH", "FM", "KI", "PW"
        ));
    }

    public static boolean isContinent(String input) {
        return input.startsWith("c:") && CONTINENT_COUNTRIES.containsKey(input.substring(2).toUpperCase());
    }

    public static Set<String> getCountriesInContinent(String continentCode) {
        return CONTINENT_COUNTRIES.get(continentCode.toUpperCase());
    }
}
