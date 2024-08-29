package net.weesli.rozsLib.bossbar;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class BossBarBuilder implements Listener {

    private int TaskID = 0;

    private int totaltime;
    private Plugin plugin;
    private String text;
    private BarStyle style = BarStyle.SOLID;
    private BarFlag flag = BarFlag.PLAY_BOSS_MUSIC;
    private BarColor color = BarColor.WHITE;
    private int refreshTime = 1;
    private int time = 0;
    private BossBarModal bossBarModal = BossBarModal.NONE;
    private List<Player> viewers = new ArrayList<>();

    public BossBarBuilder(Plugin plugin){
        this.plugin = plugin;
    }

    public BossBarBuilder setText(String text){
        this.text = text;
        return this;
    }

    public BossBarBuilder setStyle(BarStyle style){
        this.style = style;
        return this;
    }

    public BossBarBuilder setFlag(BarFlag flag){
        this.flag = flag;
        return this;
    }

    public BossBarBuilder setColor(BarColor color){
        this.color = color;
        return this;
    }

    public BossBarBuilder setRefreshTime(int refreshTime){
        this.refreshTime = refreshTime;
        return this;
    }

    public BossBarBuilder setBossBarModal(BossBarModal bossBarModal){
        this.bossBarModal = bossBarModal;
        return this;
    }

    public BossBarBuilder setTime(int time){
        this.time = time;
        return this;
    }


    public BossBarBuilder addViewer(Player viewer){
        viewers.add(viewer);
        return this;
    }

    public void build(){
        this.getPlugin().getServer().getPluginManager().registerEvents(this,getPlugin());
        List<Player> players = new ArrayList<>();
        if (!viewers.isEmpty()){
            players.addAll(viewers);
        }else {
            players.addAll(Bukkit.getOnlinePlayers());
        }
        players.forEach(player -> {
            BossBar bossBar = getPlugin().getServer().createBossBar(PlaceholderAPI.setPlaceholders(player, getText()), getColor(), getStyle(), getFlag());
            int id = new Random().nextInt(999999);
            BossBarManager.registerBossBar(id, bossBar);
            TaskID = Bukkit.getScheduler().runTaskTimerAsynchronously(this.getPlugin(), refreshBar(id), 0, refreshTime * 20L).getTaskId();
            totaltime = time;
            bossBar.addPlayer(player);
        });
    }

    private @NotNull Runnable refreshBar(int ID) {
        Optional<BossBar> bossBar = BossBarManager.getBossBarById(ID);
        return () -> {
            bossBar.ifPresent(bossBar1 -> bossBar1.setTitle(PlaceholderAPI.setPlaceholders(bossBar1.getPlayers().get(0), getText())));
            if(totaltime!=0){
                if (getTime() == 0){
                    BossBarManager.unregisterBossBar(ID);
                    bossBar.get().removeAll();
                    cancelTask();
                }
            }
            switch (getBossBarModal()){
                case RAINBOW:
                    color = BarColor.values()[(int) (Math.random() * BarColor.values().length)];
                    bossBar.ifPresent(b -> b.setColor(color));
                    break;
                case PROGRESS:
                    float progress = (float) getTime() / totaltime;
                    bossBar.ifPresent(b -> b.setProgress(progress));
                default:
            }
            if (totaltime!=0)time--;
        };
    }

    private void cancelTask() {
        Bukkit.getScheduler().cancelTask(TaskID);
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public String getText() {
        return text;
    }

    public BarStyle getStyle() {
        return style;
    }

    public BarFlag getFlag() {
        return flag;
    }

    public BarColor getColor() {
        return color;
    }

    public int getRefreshTime() {
        return refreshTime;
    }

    public BossBarModal getBossBarModal() {
        return bossBarModal;
    }

    public int getTime() {
        return time;
    }

}
