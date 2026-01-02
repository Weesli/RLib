package net.weesli.rozslib.scheduler.task;

import org.bukkit.scheduler.BukkitTask;

public class BukkitRozsTask implements RozsTask {

    private BukkitTask task;

    public BukkitRozsTask(BukkitTask task) {
        this.task = task;
    }

    @Override
    public void cancel() {
        task.cancel();
    }

    @Override
    public boolean isCancelled() {
        return task.isCancelled();
    }
}
