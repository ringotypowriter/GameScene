package skypixeldev.gamescene.scene;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import skypixeldev.gamescene.config.GConfig;
import skypixeldev.gamescene.config.OptionBuilder;
import skypixeldev.gamescene.interfaces.ISchedulers;
import skypixeldev.gamescene.interfaces.Removable;
import skypixeldev.gamescene.scene.gameObject.BaseObject;
import skypixeldev.gamescene.scene.gameObject.GObjectManager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GameObjects implements Removable {
    @Override
    public void remove() {
        //TODO
        for(SingletonObject obj : getRunningObjects()){
            UUID uuid = obj.getUniqueID();
            getSchedulers().cancel(schedulerMap.remove(uuid));
            obj.remove();
        }
        runningObjects.clear();
    }

    public static class SingletonObject{
        @Getter private GameObjects owner;
        @Getter private BaseObject objectImplement;
        @Getter private Map<String,Object> options;
        @Getter private UUID uniqueID;
        @Getter private final String name;
        public SingletonObject(String identifierName, GameObjects owner, BaseObject impl, Map<String,Object> options){
            this.uniqueID = UUID.randomUUID();
            this.name = identifierName;
            this.options = new HashMap<>(options);
            this.owner = owner;
            this.objectImplement = impl;
            objectImplement.onInitialize(owner.getScene(), owner,this);
            if(options.containsKey("applyDelay")){
                objectImplement.setMaximumApplyDelay((Integer) options.get("applyDelay"));
            }
        }

        public boolean isAsync(){
           return (boolean) getOptions().getOrDefault("Async",false);
        }

        public void remove(){
            objectImplement.remove();
        }
    }

    @Getter private GameScene scene;
    @Getter private GConfig config;

    private ConcurrentHashMap<UUID,SingletonObject> runningObjects;
    private ConcurrentHashMap<UUID,Integer> schedulerMap;

    @Getter  private boolean isRunning = false;

    public Collection<SingletonObject> getRunningObjects() {
        return Collections.unmodifiableCollection(runningObjects.values());
    }

    public SingletonObject getSingletonByUUID(UUID uuid){
        return runningObjects.get(uuid);
    }

    public GameObjects(GameScene scene, boolean autoRun){
        this.scene = scene;
        this.config = scene.getProvider().getOrCreateConfig("GameObjects");
        this.runningObjects = new ConcurrentHashMap<>();
        this.schedulerMap = new ConcurrentHashMap<>();

        ConfigurationSection section = config.getOrCreateConfigurationSection("Objects");
        for(String key : section.getKeys(false)){
            ConfigurationSection objSec = section.getConfigurationSection(key);
            BaseObject implement = GObjectManager.getImplement(objSec.getString("type"),scene);
            Map<String,Object> options = OptionBuilder.of(objSec.getConfigurationSection("options")).build();
            SingletonObject object = new SingletonObject(objSec.getName(),this, implement, options);
            runningObjects.put(object.getUniqueID(), object);
        }
        this.config.saveToFile();

        if(autoRun){
            start();
        }
    }

    public ISchedulers getSchedulers(){
        return scene.getSchedulers();
    }

    public void start(){
        if(isRunning){
            throw new IllegalStateException("GameObjects has been running");
        }
        try {
            isRunning = true;
            ISchedulers schedulers = getSchedulers();
            for(SingletonObject obj : getRunningObjects()){
                Runnable runnable = new Runnable() {
                    private final UUID uuid = obj.getUniqueID();

                    private SingletonObject getObject(){
                        return getSingletonByUUID(uuid);
                    }

                    @Override
                    public void run() {
                        this.getObject().getObjectImplement().tryUpdate(getScene(), getObject());
                    }
                };
//                Async: true
                boolean isAsync = (boolean) obj.getOptions().getOrDefault("Async",false);
                if(isAsync){
                    schedulerMap.put(obj.getUniqueID(),schedulers.runAsyncRepeating(runnable, 20));
                }else{
                    schedulerMap.put(obj.getUniqueID(),schedulers.runSyncRepeating(runnable, 20));
                }
            }
        }catch (Exception exc){
            isRunning = false;
            exc.printStackTrace();
        }
    }



}
