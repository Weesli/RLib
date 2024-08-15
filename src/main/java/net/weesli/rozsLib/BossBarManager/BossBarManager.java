package net.weesli.rozsLib.BossBarManager;

import org.bukkit.boss.BossBar;

import java.util.*;

/**
 * @author Weesli
 */

public class BossBarManager {


    private static final Map<Integer, BossBar> registeredBars = new HashMap<>();

    public static void registerBossBar(int ID, BossBar bossBar){
        registeredBars.put(ID, bossBar);
    }

    public static void unregisterBossBar(int ID){
        registeredBars.remove(ID);
    }

    public static Map<Integer, BossBar> getRegisteredBars(){
        return registeredBars;
    }

    public static Optional<BossBar> getBossBarById(int id){
        return getRegisteredBars().entrySet().stream().filter(item -> Objects.equals(item.getKey(), id)).findFirst().map(Map.Entry::getValue);
    }



}
