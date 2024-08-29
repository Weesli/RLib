package net.weesli.rozsLib.bossbar;

import lombok.Getter;
import org.bukkit.boss.BossBar;

import java.util.*;

/**
 * @author Weesli
 */

public class BossBarManager {

    @Getter
    private static final Map<Integer, BossBar> registeredBars = new HashMap<>();

    public static void registerBossBar(int ID, BossBar bossBar) {
        registeredBars.put(ID, bossBar);
    }

    public static void unregisterBossBar(int ID) {
        registeredBars.remove(ID);
    }

    public static Optional<BossBar> getBossBarById(int id) {
        for (Map.Entry<Integer, BossBar> entry : getRegisteredBars().entrySet()) {
            if (Objects.equals(entry.getKey(), id)) {
                return Optional.of(entry.getValue());
            }
        }
        return Optional.empty();
    }
}
