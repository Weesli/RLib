package net.weesli.rozslib.scheduler.task;

public interface RozsTask {
    void cancel();
    boolean isCancelled();
}