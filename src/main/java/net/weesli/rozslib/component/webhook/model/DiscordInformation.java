package net.weesli.rozslib.component.webhook.model;

import net.weesli.rozslib.config.model.WebHookSection;

public record DiscordInformation(
        String url,
        String username,
        String iconUrl
) {

    public static DiscordInformation of(WebHookSection section){
        return new DiscordInformation(
                section.getUrl(),
                section.getUsername(),
                section.getIconUrl()
        );
    }

}
