package net.weesli.rozslib.config.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import net.weesli.rozsconfig.annotations.ConfigKey;
@Getter
@NoArgsConstructor
public class WebHookSection {
    private String url;
    private String username;
    @ConfigKey("icon-url")
    private String iconUrl;
}
