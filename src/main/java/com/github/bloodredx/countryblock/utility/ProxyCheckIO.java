package com.github.bloodredx.countryblock.utility;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class ProxyCheckIO {
    public JSONObject checkIP(String ip) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String url = String.format("https://proxycheck.io/v2/%s?&vpn=1&asn=1", ip);
            HttpGet request = new HttpGet(url);
            
            String response = EntityUtils.toString(client.execute(request).getEntity());
            return new JSONObject(response);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isVPN(String ip) {
        JSONObject response = checkIP(ip);
        if (response != null && response.has(ip)) {
            return "yes".equals(response.getJSONObject(ip).optString("proxy"));
        }
        return false;
    }

    public String getCountry(String ip) {
        JSONObject response = checkIP(ip);
        if (response != null && response.has(ip)) {
            return response.getJSONObject(ip).getString("isocode");
        }
        return null;
    }
}
