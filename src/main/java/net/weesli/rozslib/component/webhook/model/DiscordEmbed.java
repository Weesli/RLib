package net.weesli.rozslib.component.webhook.model;

public class DiscordEmbed {

    private final String title;
    private final String description;
    private final int color;
    private String url;

    public DiscordEmbed(String title, String description, int color) {
        this.title = title;
        this.description = description;
        this.color = color;
    }

    public String toJson() {
        StringBuilder json = new StringBuilder();
        json.append("{");

        if (title != null)
            json.append("\"title\":\"").append(escape(title)).append("\",");

        if (description != null)
            json.append("\"description\":\"").append(escape(description)).append("\",");

        if (url != null)
            json.append("\"url\":\"").append(url).append("\",");

        json.append("\"color\":").append(color);
        json.append("}");

        return json.toString();
    }

    private String escape(String text) {
        return text.replace("\"", "\\\"");
    }
}
