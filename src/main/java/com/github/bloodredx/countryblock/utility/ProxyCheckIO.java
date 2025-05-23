package com.github.bloodredx.countryblock.utility;

import org.json.JSONObject;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.time.Duration;

public class ProxyCheckIO {
    private final HttpClient httpClient;
    
    public ProxyCheckIO() {
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    }
    
    private boolean isLocalhost(String ip) {
        return ip.equals("127.0.0.1") || 
               ip.equals("localhost") || 
               ip.equals("0:0:0:0:0:0:0:1") || 
               ip.equals("::1") ||
               ip.startsWith("192.168.") ||
               ip.startsWith("10.") ||
               ip.startsWith("172.");
    }

    public JSONObject checkIP(String ip) {
        if (isLocalhost(ip)) {
            return null;
        }
        
        try {
            String url = String.format("https://proxycheck.io/v2/%s?&vpn=1&asn=1", ip);
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return new JSONObject(response.body());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isVPN(String ip) {
        if (isLocalhost(ip)) {
            return false;
        }
        
        JSONObject response = checkIP(ip);
        if (response != null && response.has(ip)) {
            return "yes".equals(response.getJSONObject(ip).optString("proxy"));
        }
        return false;
    }

    public String getCountry(String ip) {
        if (isLocalhost(ip)) {
            return "LOCAL";
        }
        
        JSONObject response = checkIP(ip);
        if (response != null && response.has(ip)) {
            return response.getJSONObject(ip).getString("isocode");
        }
        return null;
    }
}