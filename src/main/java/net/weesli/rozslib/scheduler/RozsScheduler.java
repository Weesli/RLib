package net.weesli.rozslib.scheduler;

import net.weesli.rozslib.scheduler.task.BukkitRozsTask;
import net.weesli.rozslib.scheduler.task.FoliaRozsTask;
import net.weesli.rozslib.scheduler.task.RozsTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

public class RozsScheduler {

    private static final boolean IS_FOLIA = isFolia();


    public static RozsTask run(Plugin plugin, Runnable task) {
        if (IS_FOLIA) {
            var scheduled = Bukkit.getGlobalRegionScheduler()
                    .run(plugin, t -> task.run());
            return new FoliaRozsTask(scheduled);
        } else {
            return new BukkitRozsTask(
                    Bukkit.getScheduler().runTask(plugin, task)
            );
        }
    }

    public static RozsTask run(Plugin plugin, Location location, Runnable task) {
        if (IS_FOLIA) {
            var scheduled = Bukkit.getRegionScheduler()
                    .run(plugin, location, t -> task.run());
            return new FoliaRozsTask(scheduled);
        } else {
            return new BukkitRozsTask(
                    Bukkit.getScheduler().runTask(plugin, task)
            );
        }
    }

    public static RozsTask run(Plugin plugin, Entity entity, Runnable task) {
        if (IS_FOLIA) {
            var scheduled = entity.getScheduler()
                    .run(plugin, t -> task.run(), null);
            return new FoliaRozsTask(scheduled);
        } else {
            return new BukkitRozsTask(
                    Bukkit.getScheduler().runTask(plugin, task)
            );
        }
    }


    public static RozsTask runLater(Plugin plugin, Runnable task, long delay) {
        if (IS_FOLIA) {
            var scheduled = Bukkit.getGlobalRegionScheduler()
                    .runDelayed(plugin, t -> task.run(), delay);
            return new FoliaRozsTask(scheduled);
        } else {
            return new BukkitRozsTask(
                    Bukkit.getScheduler().runTaskLater(plugin, task, delay)
            );
        }
    }

    public static RozsTask runLater(Plugin plugin, Location location, Runnable task, long delay) {
        if (IS_FOLIA) {
            var scheduled = Bukkit.getRegionScheduler()
                    .runDelayed(plugin, location, t -> task.run(), delay);
            return new FoliaRozsTask(scheduled);
        } else {
            return new BukkitRozsTask(
                    Bukkit.getScheduler().runTaskLater(plugin, task, delay)
            );
        }
    }

    public static RozsTask runLater(Plugin plugin, Entity entity, Runnable task, long delay) {
        if (IS_FOLIA) {
            var scheduled = entity.getScheduler()
                    .runDelayed(plugin, t -> task.run(), null, delay);
            return new FoliaRozsTask(scheduled);
        } else {
            return new BukkitRozsTask(
                    Bukkit.getScheduler().runTaskLater(plugin, task, delay)
            );
        }
    }


    public static RozsTask runTimer(Plugin plugin, Runnable task, long delay, long period) {
        if (IS_FOLIA) {
            var scheduled = Bukkit.getGlobalRegionScheduler()
                    .runAtFixedRate(plugin, t -> task.run(), delay, period);
            return new FoliaRozsTask(scheduled);
        } else {
            return new BukkitRozsTask(
                    Bukkit.getScheduler().runTaskTimer(plugin, task, delay, period)
            );
        }
    }

    public static RozsTask runTimer(Plugin plugin, Location location, Runnable task, long delay, long period) {
        if (IS_FOLIA) {
            var scheduled = Bukkit.getRegionScheduler()
                    .runAtFixedRate(plugin, location, t -> task.run(), delay, period);
            return new FoliaRozsTask(scheduled);
        } else {
            return new BukkitRozsTask(
                    Bukkit.getScheduler().runTaskTimer(plugin, task, delay, period)
            );
        }
    }

    public static RozsTask runTimer(Plugin plugin, Entity entity, Runnable task, long delay, long period) {
        if (IS_FOLIA) {
            var scheduled = entity.getScheduler()
                    .runAtFixedRate(plugin, t -> task.run(), null, delay, period);
            return new FoliaRozsTask(scheduled);
        } else {
            return new BukkitRozsTask(
                    Bukkit.getScheduler().runTaskTimer(plugin, task, delay, period)
            );
        }
    }

    private static boolean isFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
