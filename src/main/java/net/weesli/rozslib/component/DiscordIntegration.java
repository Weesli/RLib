package net.weesli.rozslib.component;

import lombok.Getter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Getter
public class DiscordIntegration {

    private String url;
    private String title;
    private String avatar_url;
    private Map<String, String> varibles = new HashMap<>();


    /**
     * Create a simple discord web hook class
     * @param url
     * @param title
     * @param avatar_url
     */
    public DiscordIntegration(String url, String title, String avatar_url) {
        this.url = url;
        this.title = title;
        this.avatar_url = avatar_url;
    }

    public DiscordIntegration setValues(Map<String, String> values) {
        this.varibles = values;
        return this;
    }


    /**
     * Send json object to discord
     * @param object
     * @throws IOException
     * @throws ParseException
     */
    public void execute(JSONObject object) {
        object.put("username", getTitle());
        object.put("avatar_url", getAvatar_url());
        processJSONObject(object, getVaribles());
        sendEmbed(object);
    }

    public CompletableFuture<Void> sendEmbed(JSONObject object) {
        java.net.http.HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getUrl()))
                .POST(HttpRequest.BodyPublishers.ofString(object.toString()))
                .header("Content-Type", "application/json")
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {

                })
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });
    }


    // Format changer like %x% = 15, %player% = Weesli
    // usage Map.of("%player%", "Weesli")

    private void processJSONObject(JSONObject jsonObject, Map<String, String> values) {
        for (Object key : jsonObject.keySet()) {
            Object value = jsonObject.get(key);
            if (value instanceof JSONObject) {
                processJSONObject((JSONObject) value, values);
            } else if (value instanceof JSONArray) {
                processJSONArray((JSONArray) value, values);
            } else if (value instanceof String) {
                String valueStr = (String) value;
                for (Map.Entry<String, String> entry : values.entrySet()) {
                    if (valueStr.contains(entry.getKey())) {
                        valueStr = valueStr.replace(entry.getKey(), entry.getValue());
                    }
                }
                jsonObject.put(key, valueStr);
            }
        }
    }

    private void processJSONArray(JSONArray jsonArray, Map<String, String> values) {
        for (int i = 0; i < jsonArray.size(); i++) {
            Object value = jsonArray.get(i);
            if (value instanceof JSONObject) {
                processJSONObject((JSONObject) value, values);
            } else if (value instanceof JSONArray) {
                processJSONArray((JSONArray) value, values);
            } else if (value instanceof String) {
                String valueStr = (String) value;
                for (Map.Entry<String, String> entry : values.entrySet()) {
                    if (valueStr.contains(entry.getKey())) {
                        valueStr = valueStr.replace(entry.getKey(), entry.getValue());
                    }
                }
                jsonArray.set(i, valueStr);
            }
        }
    }


}
