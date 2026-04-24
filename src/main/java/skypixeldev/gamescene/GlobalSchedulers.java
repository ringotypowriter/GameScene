package skypixeldev.gamescene;


import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import skypixeldev.gamescene.interfaces.ISchedulers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GlobalSchedulers implements ISchedulers {
    private static GlobalSchedulers instance;

    public static GlobalSchedulers getInstance() {
        if(instance == null){
            instance = new GlobalSchedulers();
        }
        return instance;
    }

    private List<BukkitTask> runningTasks;
    private GlobalSchedulers(){
        runningTasks = Collections.synchronizedList(new ArrayList<>());
    }
    public int runAsync(Runnable runnable) {
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }.runTaskAsynchronously(Bootstrap.getInstance());
        runningTasks.add(task);
        return task.getTaskId();
    }

    public int runSync(Runnable runnable) {
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }.runTask(Bootstrap.getInstance());
        runningTasks.add(task);
        return task.getTaskId();
    }

    public int runSyncLater(Runnable runnable, int ticks) {
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }.runTaskLater(Bootstrap.getInstance(), ticks);
        runningTasks.add(task);
        return task.getTaskId();
    }

    public int runAsyncLater(Runnable runnable, int ticks) {
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }.runTaskLaterAsynchronously(Bootstrap.getInstance(), ticks);
        runningTasks.add(task);
        return task.getTaskId();
    }

    @Override
    public int runSyncRepeating(Runnable runnable, int interval) {
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }.runTaskTimer(Bootstrap.getInstance(), interval, interval);
        runningTasks.add(task);
        return task.getTaskId();
    }

    @Override
    public int runAsyncRepeating(Runnable runnable, int interval) {
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }.runTaskTimerAsynchronously(Bootstrap.getInstance(), interval, interval);
        runningTasks.add(task);
        return task.getTaskId();
    }

    @Override
    public boolean cancel(int taskId) {
        return runningTasks.removeIf(task -> {
            if(task.isCancelled()){
                return true;
            }
            if(taskId == task.getTaskId()){
                task.cancel();
                return true;
            }
            return false;
        });
    }


}
