package net.weesli.rozslib.scheduler.task;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;

public class FoliaRozsTask implements RozsTask {

    private final ScheduledTask task;

    public FoliaRozsTask(ScheduledTask task) {
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
