package skypixeldev.gamescene.scene;

import lombok.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import skypixeldev.gamescene.Bootstrap;
import skypixeldev.gamescene.Log;
import skypixeldev.gamescene.config.GConfig;
import skypixeldev.gamescene.config.OptionBuilder;
import skypixeldev.gamescene.interfaces.ISchedulers;
import skypixeldev.gamescene.interfaces.IWildcard;
import skypixeldev.gamescene.interfaces.Removable;
import skypixeldev.gamescene.parser.FunctionManager;
import skypixeldev.gamescene.parser.Parsing;
import skypixeldev.gamescene.parser.wildcard.WildcardGameScene;
import skypixeldev.gamescene.parser.wildcard.WildcardString;
import sun.reflect.generics.tree.Wildcard;

import java.io.Closeable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Schedulers implements Closeable, ISchedulers, Removable {

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

    @Override
    public void remove() {
        close();
    }

    @RequiredArgsConstructor()
    private static class SingletonScheduler{
        @Getter @NonNull
        private String key;
        @Getter @NonNull
        private List<Parsing> functions;
        @Getter @NonNull
        private int countTimer;
        @Getter @Setter
        private int runnableIndices = -1;

        @Getter @NonNull
        private Schedulers owner;

        @Getter @NonNull
        private Map<String,Object> options;

        @Getter
        private int currentCount = 0;

        @Getter(AccessLevel.PUBLIC) @Setter(value = AccessLevel.PROTECTED) boolean isActive = true;

        public boolean isAsync(){
            return (boolean) options.getOrDefault("Async", false);
        }

        public void reset(){
            reset(true);
        }

        public void reset(boolean reActivate){
            currentCount = 0;
            if(!isActive && reActivate){
                getOwner().activate(this.getKey());
            }
        }

        public void count(){
            this.currentCount++;
            if(currentCount == countTimer){
                executeFunction();
            }else if(currentCount > countTimer){
                isActive = false;
                reset(false);
                cancel();
            }
        }

        public void cancel(){
            if(runnableIndices != -1){
                if(owner.cancel(runnableIndices)) {
                    runnableIndices = -1;
                }else{
                    throw new IllegalStateException("Scheduler " + getKey() + "'s Runnable#" + runnableIndices + " is invalid!");
                }
            }
        }

        public void executeFunction(){
            IWildcard wildcard = new WildcardGameScene(getOwner().getScene());
            IWildcard stringCard = new WildcardString("scheduler", getKey());
            IWildcard sceneCard = new WildcardString("gameScene", getOwner().getScene().getName());
            boolean delayedRunning = false;
            int ticks = 0;
            Set<Parsing> remains = new HashSet<>();
            for(Parsing parsing : functions){
                // Global Internal Implement
                if(!delayedRunning) {
                    if (parsing.getFunctionName().equals("wait")) {
                        ticks = Integer.parseInt(parsing.getArguments().getOrDefault("val", "1")) * 20;
                        delayedRunning = true;
                        continue;
                    } else if (parsing.getFunctionName().equals("waitTick")) {
                        ticks = Integer.parseInt(parsing.getArguments().getOrDefault("val", "20"));
                        delayedRunning = true;
                        continue;
                    }
                }
                if(delayedRunning){
                    remains.add(parsing);
                }else{
                    FunctionManager.execute(parsing.getFunctionName(), parsing.getArguments(), getOwner().getScene(), isAsync(), wildcard, stringCard, sceneCard);
                }
            }
            if(delayedRunning){
                internalExecuteFunctions(remains, ticks);
            }
        }

        // For Delayed Running
        public void internalExecuteFunctions(final Collection<Parsing> remains, int ticks){
            Runnable runnable = () -> {
                IWildcard wildcard = new WildcardGameScene(getOwner().getScene());
                IWildcard stringCard = new WildcardString("scheduler", getKey());
                IWildcard sceneCard = new WildcardString("gameScene", getOwner().getScene().getName());
                boolean delayedRunning = false;
                int secs1 = 0;
                Set<Parsing> remains1 = new HashSet<>();
                for (Parsing parsing : remains) {
                    // Global Internal Implement
                    if(!delayedRunning) {
                        if (parsing.getFunctionName().equals("wait")) {
                            secs1 = Integer.parseInt(parsing.getArguments().getOrDefault("val", "1")) * 20;
                            delayedRunning = true;
                            continue;
                        } else if (parsing.getFunctionName().equals("waitTick")) {
                            secs1 = Integer.parseInt(parsing.getArguments().getOrDefault("val", "20"));
                            delayedRunning = true;
                            continue;
                        }
                    }
                    if (delayedRunning) {
                        remains1.add(parsing);
                    } else {
                        FunctionManager.execute(parsing.getFunctionName(), parsing.getArguments(), getOwner().getScene(), isAsync(), wildcard, stringCard, sceneCard);
                    }
                }
                if (delayedRunning) {
                    internalExecuteFunctions(remains1, secs1);
                }
            };
            if(isAsync()){
                getOwner().runAsyncLater(runnable, ticks);
            }else{
                getOwner().runSyncLater(runnable, ticks);
            }
        }


    }

    @Getter
    private GameScene scene;
    @Getter private GConfig config;

    @Getter private List<BukkitTask> runningTasks;
    @Getter private ConcurrentHashMap<String,SingletonScheduler> schedulers;

    @Getter private boolean isRunning = false;

    public Schedulers(GameScene scene, boolean autoRun){
        this.scene = scene;
        this.config = scene.getProvider().getOrCreateConfig("Schedulers");

        runningTasks = Collections.synchronizedList(new ArrayList<>());
        schedulers = new ConcurrentHashMap<>();
        if(config.getConfiguration().getConfigurationSection("Schedulers") == null){
           config.getConfiguration().createSection("Schedulers");
        }
        ConfigurationSection section = config.getConfiguration().getConfigurationSection("Schedulers");
        for(String key : section.getKeys(false)){
            ConfigurationSection schedulerSection = section.getConfigurationSection(key);
            int countTimer = schedulerSection.getInt("CounterTime");
            List<Parsing> parsings = new ArrayList<>();
            try{
                schedulerSection.getStringList("Functions").forEach(str -> parsings.add(new Parsing(str)));
            }catch (Exception exc){
                exc.printStackTrace();
            }
            Map<String,Object> options = OptionBuilder.of(schedulerSection.getConfigurationSection("Options")).build();
            SingletonScheduler singleton = new SingletonScheduler(key, parsings, countTimer, this, options);
            singleton.setActive(!(Boolean) options.getOrDefault("Deactivate", false));
            this.schedulers.put(key, singleton);
        }
        this.config.saveToFile();

        if(autoRun){
            start();
        }
    }

    public boolean isActive(String key){
        if(schedulers.containsKey(key)){
            return schedulers.get(key).isActive();
        }
        return false;
    }

    public void activate(String key){
        if(!schedulers.containsKey(key)){
            throw new IllegalArgumentException("cannot found scheduler '" + key + "'");
        }
        SingletonScheduler object = schedulers.get(key);
        if(object.isActive()){
            Log.lSc("Scheduler '" + object.getKey() + "' has been active");
            return;
        }else{
            object.setActive(true);
            Runnable runnable = new Runnable() {
                final String schedulerKey = key;
                final SingletonScheduler singleton = object;

                @Override
                public void run() {
                    singleton.count();
                    getScene().getVariables().setInteger("Schedulers:" + schedulerKey, singleton.getCurrentCount());
                }
            };
            if (object.isAsync()) {
                object.setRunnableIndices(this.runAsyncRepeating(runnable, 20));
            } else {
                object.setRunnableIndices(this.runSyncRepeating(runnable, 20));
            }
        }
    }

    public void resetIfExist(String key){
        Optional.ofNullable(schedulers.get(key)).ifPresent(SingletonScheduler::reset);
    }

    public void start(){
        if(isRunning){
            throw new IllegalStateException("Schedulers has been running");
        }
        try {
            isRunning = true;
            schedulers.forEach((key, scheduler) -> {
                if(scheduler.isActive) {
                    Log.lSc("Running Scheduler: "  + key + " for " + getScene().getName());
                    Runnable runnable = new Runnable() {
                        final String schedulerKey = key;
                        final SingletonScheduler singleton = scheduler;

                        @Override
                        public void run() {
                            singleton.count();
                            getScene().getVariables().setInteger("Schedulers:" + schedulerKey, singleton.getCurrentCount());
                        }
                    };
                    if (scheduler.isAsync()) {
                        scheduler.setRunnableIndices(this.runAsyncRepeating(runnable, 20));
                    } else {
                        scheduler.setRunnableIndices(this.runSyncRepeating(runnable, 20));
                    }
                }else{
                    Log.lSc("Scheduler " + key + " is idling");
                }
            });
        }catch (Exception exc){
            isRunning = false;
            exc.printStackTrace();;
        }
    }


    @Override
    public void close() {
        runningTasks.forEach(BukkitTask::cancel);
        runningTasks.clear();
    }
}
