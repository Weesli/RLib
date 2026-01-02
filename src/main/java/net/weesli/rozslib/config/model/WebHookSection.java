package net.weesli.rozslib.config.model;

import lombok.Getter;
import net.weesli.rozsconfig.annotations.ConfigKey;
@Getter
public class WebHookSection {
    private String url;
    private String username;
    @ConfigKey("icon-url")
    private String iconUrl;
}
