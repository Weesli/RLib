package net.weesli.rozsLib.API;

import lombok.Getter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Getter
public class DiscordIntegration {

    private String url;
    private String title;
    private String avatar_url;
    private Map<String, String> varibles = new HashMap<String, String>();


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
    public void execute(JSONObject object) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        JSONObject defaultEmbed = (JSONObject) parser.parse(object.toJSONString());

        defaultEmbed.put("username", getTitle());
        defaultEmbed.put("avatar_url", getAvatar_url());

        sendEmbed(defaultEmbed);
    }



    public void sendEmbed(JSONObject object) throws IOException{
        java.net.URL url = new URL(getUrl());
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.addRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        OutputStream stream = connection.getOutputStream();
        stream.write(object.toString().getBytes());
        stream.flush();
        stream.close();
        connection.getInputStream().close();
        connection.disconnect();
    }


    // Format changer like %x% = 15, %player% = Weesli
    // usage Map.of("%player%", "Weesli")

    private void processJSONObject(JSONObject jsonObject, Map<String,String> values) {
        for (Object key : jsonObject.keySet()) {
            Object value = jsonObject.get(key);
            if (value instanceof JSONObject) {
                processJSONObject((JSONObject) value, values);
            } else if (value instanceof JSONArray) {
                processJSONArray((JSONArray) value, values);
            } else if (value instanceof String) {
                String valueStr = (String) value;
                for (String varible : values.keySet()){
                    if (valueStr.contains(varible)){
                        valueStr = valueStr.replace(varible, values.get(varible));
                    }
                }
                jsonObject.put(key, valueStr);
            }
        }
    }
    private void processJSONArray(JSONArray jsonArray, Map<String,String> values) {
        for (int i = 0; i < jsonArray.size(); i++) {
            Object value = jsonArray.get(i);
            if (value instanceof JSONObject) {
                processJSONObject((JSONObject) value, values);
            } else if (value instanceof JSONArray) {
                processJSONArray((JSONArray) value, values);
            } else if (value instanceof String) {
                String valueStr = (String) value;
                for (String varible : values.keySet()){
                    if (valueStr.contains(varible)){
                        valueStr = valueStr.replace(varible, values.get(varible));
                    }
                }
                jsonArray.add(i, valueStr);
            }
        }
    }

}
