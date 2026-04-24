package skypixeldev.gamescene;

import co.aikar.commands.PaperCommandManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import skypixeldev.gamescene.config.GConfig;
import skypixeldev.gamescene.config.GConfigProvider;
import skypixeldev.gamescene.config.OptionBuilder;
import skypixeldev.gamescene.export.dungeonplus.DPCompat;
import skypixeldev.gamescene.interfaces.ExternalLibrary;
import skypixeldev.gamescene.journals.Journals;
import skypixeldev.gamescene.parser.FunctionManager;
import skypixeldev.gamescene.pool.commands.SItemCommand;
import skypixeldev.gamescene.scene.gameObject.GObjectManager;
import skypixeldev.gamescene.scene.itemParser.ItemParsingManager;
import skypixeldev.gamescene.scene.manager.SceneManager;
import skypixeldev.gamescene.scene.pub.MovableLight;
import skypixeldev.gamescene.scene.pub.TemporaryEntities;
import skypixeldev.gamescene.scene.pub.UnlockGateHandler;
import skypixeldev.gamescene.scene.pub.property.PropertyImplementation;
import skypixeldev.gamescene.setup.SetupManager;
import skypixeldev.gamescene.setup.commands.FunctionCommands;
import skypixeldev.gamescene.setup.commands.SceneDataCommands;

import java.io.File;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

public final class Bootstrap extends JavaPlugin {

    @Getter private static JavaPlugin instance;
    private static File scenesFolder;

    @Getter private static boolean isDebug = true;

    public static GConfigProvider getSpecificProvider(String folderName){
        return new GConfigProvider(instance, folderName, scenesFolder);
    }

    public static Logger getOLogger(){
        return getInstance().getLogger();
    }

    public static void log(String msg){
        Bukkit.getConsoleSender().sendMessage("§7[§dGAME SCENE§7] §f" + msg);
    }

    public static void logPf(String msg){
        Bukkit.getConsoleSender().sendMessage("§7[§6Parsing Functions§7] §f" + msg);
    }

    @Getter private static GConfig globalSettings;
    @Getter private static PaperCommandManager commandManager;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.getDataFolder().mkdirs();
        scenesFolder = new File(this.getDataFolder(),"GameScenes");
        scenesFolder.mkdirs();

        globalSettings = GConfig.of(this,"globalSettings", OptionBuilder.of()
                                            .key("Automatic-Unlocking-IronDoors").value(false)
                                            .key("ServerSide-DynamicLighting").value(true)
                                            .key("Auto-Hide-And-Show-CustomTag-Of-ArmorStand").value(true)
                                            .key("Primary-Scene.Enable").value(false)
                                            .key("Primary-Scene.SceneName").value("AutoRunSceneHere"));


        // Commands
        commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(new SItemCommand());
        commandManager.registerCommand(new SceneDataCommands());

        SetupManager.onLoadCommand(this, commandManager);
        FunctionCommands.initCommands(this, commandManager);



        TemporaryEntities.init();

        // DO NOTHING
        STD.getPools();

        STD.importFunctions();

        // External Listener
        Bukkit.getPluginManager().registerEvents(new UnlockGateHandler(), this);
        if(getGlobalSettings().getBoolean("ServerSide-DynamicLighting")){
            MovableLight.init(this);
        }
        Bukkit.getPluginManager().registerEvents(new PropertyImplementation(), this);

        // External Functions
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
            for(Plugin plugin : Bukkit.getPluginManager().getPlugins()){
                if(plugin instanceof ExternalLibrary){
                    try {
                        ExternalLibrary library = (ExternalLibrary) plugin;
                        library.onLoadFunctions();
                        library.onLoadGameObjects();
                        ItemParsingManager.registerItemProvider(library.getItemProvider());
                        Log.l("Loaded External Library of " + plugin.getName());
                    }catch (Exception exc){
                        exc.printStackTrace();
                        Log.l("§c<WARNING> Failed to load External Library of '" + plugin.getName() + "' because of an exception.");
                    }
                }
            }
            if(getGlobalSettings().getBoolean("Primary-Scene.Enable")){
                String sceneName = getGlobalSettings().getString("Primary-Scene.SceneName");
                Log.l("即将自动运行: §a" + sceneName);
                SceneManager.loadScene(sceneName,true);
            }
        });

        Log.l("Loaded " + FunctionManager.getLoadedFunctionSize() + " functions & " + GObjectManager.getLoadedGObjectSize() + " gameObject Implementations");

        if(getGlobalSettings().getBoolean("Auto-Hide-And-Show-CustomTag-Of-ArmorStand")) {
            new BukkitRunnable() {
                final Set<UUID> lastEntity = new HashSet<>();

                @Override
                public void run() {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (player.getGameMode().equals(GameMode.SPECTATOR)) {
                            continue;
                        }
                        lastEntity.removeIf(uuid -> {
                            Optional.ofNullable(Bukkit.getEntity(uuid)).ifPresent(entity -> entity.setCustomNameVisible(false));
                            return true;
                        });
                        World world = player.getWorld();
                        if (player.getLocation().isChunkLoaded()) {
                            for (LivingEntity entity : world.getNearbyLivingEntities(player.getLocation(), 6, (entity) -> entity instanceof ArmorStand)) {
                                if (!entity.isCustomNameVisible()) {
                                    entity.setCustomNameVisible(true);
                                }
                                lastEntity.add(entity.getUniqueId());
                            }
                        }
                    }
                }
            }.runTaskTimerAsynchronously(Bootstrap.getInstance(), 10, 10);
        }

        // Journals
        Journals.initialize();

        if(Bukkit.getPluginManager().getPlugin("DungeonPlus") != null){
            DPCompat.initCompat();
        }
    }

    public static String[] getAllSuspiciousScene(){
        return scenesFolder.list();
    }



    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
