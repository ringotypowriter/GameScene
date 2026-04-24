package skypixeldev.gamescene.interfaces;

public interface ISchedulers {
    int runAsync(Runnable runnable);

    int runSync(Runnable runnable);

    int runSyncLater(Runnable runnable, int ticks);

    int runAsyncLater(Runnable runnable, int ticks);

    int runSyncRepeating(Runnable runnable, int interval);

    int runAsyncRepeating(Runnable runnable, int interval);

    boolean cancel(int taskId);
}
