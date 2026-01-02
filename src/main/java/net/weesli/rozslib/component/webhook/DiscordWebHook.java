package net.weesli.rozslib.component.webhook;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.weesli.rozslib.component.webhook.model.DiscordInformation;
import net.weesli.rozslib.format.FormatterUtil;
import net.weesli.rozslib.format.model.Placeholder;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class DiscordWebHook {

    private final DiscordInformation information;

    public DiscordWebHook(DiscordInformation information) {
        this.information = information;
    }


    public void sendWebhook(InputStream input, Placeholder... placeholders) {
        try(InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
            runReader(reader, placeholders);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendWebHook(File file, Placeholder... placeholders) {
        try(FileReader reader = new FileReader(file, StandardCharsets.UTF_8)) {
            runReader(reader, placeholders);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void runReader(Reader reader, Placeholder... placeholders) {
        JsonObject object = JsonParser.parseReader(reader).getAsJsonObject();
        String content = object.has("content")
                ? FormatterUtil.format(object.get("content").getAsString(), placeholders)
                : null;
        List<String> embeds = new ArrayList<>();
        if (object.has("embeds")) {
            JsonArray array = object.getAsJsonArray("embeds");
            for (JsonElement element : array) {
                embeds.add(
                        FormatterUtil.format(element.toString(), placeholders)
                );
            }
        }
        send(content,embeds);
    }

    private void send(
            String content,
            List<String> embeds
    ) {
        try {
            URL url = new URL(information.url());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            StringBuilder json = new StringBuilder();
            json.append("{");

            if (content != null)
                json.append("\"content\":\"").append(escape(content)).append("\",");

            if (information.username() != null)
                json.append("\"username\":\"").append(escape(information.username())).append("\",");

            if (information.iconUrl() != null)
                json.append("\"avatar_url\":\"").append(information.iconUrl()).append("\",");

            if (embeds != null && !embeds.isEmpty()) {
                json.append("\"embeds\":[");
                for (int i = 0; i < embeds.size(); i++) {
                    json.append(embeds.get(i));
                    if (i < embeds.size() - 1) json.append(",");
                }
                json.append("]");
            } else {
                if (json.charAt(json.length() - 1) == ',')
                    json.deleteCharAt(json.length() - 1);
            }

            json.append("}");

            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(json.toString());
            writer.flush();
            writer.close();

            connection.getInputStream().close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String escape(String text) {
        return text.replace("\"", "\\\"");
    }
}
