package net.weesli.rozslib.scheduler;

import net.weesli.rozslib.scheduler.task.RozsTask;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

public abstract class RozsRunnable implements Runnable {

    private RozsTask task;


    public synchronized boolean isCancelled() throws IllegalStateException {
        checkScheduled();
        return task.isCancelled();
    }


    public synchronized void cancel() throws IllegalStateException {
        checkScheduled();
        task.cancel();
    }

    public synchronized RozsTask getTask() throws IllegalStateException {
        checkScheduled();
        return task;
    }

    public synchronized RozsTask runTask(Plugin plugin) throws IllegalArgumentException, IllegalStateException {
        checkNotScheduled();
        return setupTask(RozsScheduler.run(plugin, this));
    }

    public synchronized RozsTask runTaskLater(Plugin plugin, long delay) throws IllegalArgumentException, IllegalStateException {
        checkNotScheduled();
        return setupTask(RozsScheduler.runLater(plugin, this, delay));
    }

    public synchronized RozsTask runTaskTimer(Plugin plugin, long delay, long period) throws IllegalArgumentException, IllegalStateException {
        checkNotScheduled();
        return setupTask(RozsScheduler.runTimer(plugin, this, delay, period));
    }

    public synchronized RozsTask runTaskAsynchronously(Plugin plugin) throws IllegalArgumentException, IllegalStateException {
        checkNotScheduled();
        return setupTask(RozsScheduler.runAsync(plugin, this));
    }

    public synchronized RozsTask runTaskLaterAsynchronously(Plugin plugin, long delay) throws IllegalArgumentException, IllegalStateException {
        checkNotScheduled();
        return setupTask(RozsScheduler.runLaterAsync(plugin, this, delay));
    }

    public synchronized RozsTask runTaskTimerAsynchronously(Plugin plugin, long delay, long period) throws IllegalArgumentException, IllegalStateException {
        checkNotScheduled();
        return setupTask(RozsScheduler.runTimerAsync(plugin, this, delay, period));
    }

    public synchronized RozsTask runTask(Plugin plugin, Location location) throws IllegalArgumentException, IllegalStateException {
        checkNotScheduled();
        return setupTask(RozsScheduler.run(plugin, location, this));
    }

    public synchronized RozsTask runTaskLater(Plugin plugin, Location location, long delay) throws IllegalArgumentException, IllegalStateException {
        checkNotScheduled();
        return setupTask(RozsScheduler.runLater(plugin, location, this, delay));
    }

    public synchronized RozsTask runTaskTimer(Plugin plugin, Location location, long delay, long period) throws IllegalArgumentException, IllegalStateException {
        checkNotScheduled();
        return setupTask(RozsScheduler.runTimer(plugin, location, this, delay, period));
    }

    public synchronized RozsTask runTask(Plugin plugin, Entity entity) throws IllegalArgumentException, IllegalStateException {
        checkNotScheduled();
        return setupTask(RozsScheduler.run(plugin, entity, this));
    }

    public synchronized RozsTask runTaskLater(Plugin plugin, Entity entity, long delay) throws IllegalArgumentException, IllegalStateException {
        checkNotScheduled();
        return setupTask(RozsScheduler.runLater(plugin, entity, this, delay));
    }

    public synchronized RozsTask runTaskTimer(Plugin plugin, Entity entity, long delay, long period) throws IllegalArgumentException, IllegalStateException {
        checkNotScheduled();
        return setupTask(RozsScheduler.runTimer(plugin, entity, this, delay, period));
    }

    private void checkScheduled() {
        if (task == null) {
            throw new IllegalStateException("This task isn't started!");
        }
    }

    private void checkNotScheduled() {
        if (task != null) {
            throw new IllegalStateException("This task already started!");
        }
    }

    private RozsTask setupTask(RozsTask task) {
        this.task = task;
        return task;
    }
}