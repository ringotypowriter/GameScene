package skypixeldev.gamescene.scene;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import skypixeldev.gamescene.Bootstrap;
import skypixeldev.gamescene.Log;
import skypixeldev.gamescene.STD;
import skypixeldev.gamescene.config.GConfig;
import skypixeldev.gamescene.config.GConfigProvider;
import skypixeldev.gamescene.config.OptionBuilder;
import skypixeldev.gamescene.events.SceneRunEvent;
import skypixeldev.gamescene.interfaces.Removable;
import skypixeldev.gamescene.parser.FunctionManager;
import skypixeldev.gamescene.parser.Parsing;
import skypixeldev.gamescene.parser.wildcard.WildcardGameScene;
import skypixeldev.gamescene.parser.wildcard.WildcardPlayer;
import skypixeldev.gamescene.scene.events.EventEmitter;
import skypixeldev.gamescene.scene.events.EventResult;
import skypixeldev.gamescene.scene.manager.SceneManager;
import skypixeldev.gamescene.scene.manager.ScenePlayer;
import skypixeldev.gamescene.scene.pub.property.PlayerProperty;

import java.awt.geom.Area;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class GameScene implements Removable, Listener {
    @Getter private GConfigProvider provider;
    @Getter private Locations locations;
    @Getter private Areas areas;
    @Getter private Variables variables;
    @Getter private Schedulers schedulers;
    @Getter private GameObjects objects;
    @Getter private EventEmitter events;
    @Getter private LootTable lootTable;
    @Getter private GConfig settings;

    @Getter private String name;

    @Getter private Cache<UUID,ScenePlayer> playerList;
    @Getter private boolean running;

    @Getter private boolean autoJoinPlayers = false;

    private World specificWorld;

    public GameScene(GameScene another, World world) {
        provider = another.getProvider().makeMirrors();

        this.name = another.name + "$" + UUID.randomUUID();

        settings = another.settings;
        events = new EventEmitter(this);
        locations = another.locations;
        // location set
        specificWorld = world;
        locations.setWorldReplacer(() -> specificWorld);

        areas = new Areas(this);
        variables = new Variables(this);
        schedulers = new Schedulers(this, true);
        objects = new GameObjects(this, true);

        lootTable = new LootTable(this);
        int minutes = settings.getInt("ScenePlayersCleanupMinutes");
        autoJoinPlayers = false;
        this.playerList = Caffeine.newBuilder()
                .expireAfterAccess(minutes, TimeUnit.MINUTES)
                .expireAfterWrite(minutes, TimeUnit.MINUTES)
                .maximumSize(Bukkit.getMaxPlayers() + 16)
                .removalListener(((key, value, cause) -> {
                    if (value != null)
                        Log.l("<" + name + "> dropped a scenePlayer of " + ((ScenePlayer) value).getName() + " ");
                }))
                .build();

        Bukkit.getPluginManager().registerEvents(this, Bootstrap.getInstance());

        running = true;

        SceneManager.appendRunningScene(this);

        deepInit();
    }

    public GameScene(String name, boolean autoRun){
        provider = Bootstrap.getSpecificProvider(name);
        this.name = name;

        settings = provider.getOrCreateConfig("Settings",
                OptionBuilder.of()
                        .key("AutoJoinPlayers").value(false)
                        .key("ScenePlayersCleanupMinutes").value(60)
                        .key("Allow-AutoReconnection").value(false)
                        );
        events = new EventEmitter(this);
        locations = new Locations(this);
        areas = new Areas(this);
        variables = new Variables(this);
        schedulers = new Schedulers(this, autoRun);
        objects = new GameObjects(this,autoRun);

        lootTable = new LootTable(this);

        // Settings
        int minutes = settings.getInt("ScenePlayersCleanupMinutes");
        autoJoinPlayers = settings.getBoolean("AutoJoinPlayers");

        this.playerList = Caffeine.newBuilder()
                .expireAfterAccess(minutes, TimeUnit.MINUTES)
                .expireAfterWrite(minutes,TimeUnit.MINUTES)
                .maximumSize(Bukkit.getMaxPlayers() + 16)
                .removalListener(((key, value, cause) -> {
                    if(value != null)
                    Log.l("<" + name + "> dropped a scenePlayer of " + ((ScenePlayer) value).getName() + " ");
                }))
                .build();

        Bukkit.getPluginManager().registerEvents(this, Bootstrap.getInstance());

        running = autoRun;

        if(autoRun) {
            deepInit();
        }
    }

    private void deepInit(){
        if(autoJoinPlayers){
            Bukkit.getOnlinePlayers().forEach(this::joinPlayer);
        }
        Bukkit.getPluginManager().callEvent(new SceneRunEvent(this));
    }

    public void run(){
        schedulers.start();
        objects.start();

        deepInit();
    }


    public void joinPlayer(Player player){
        this.playerList.put(player.getUniqueId(), new ScenePlayer(player, this));
        this.events.emit(STD.EVENT_PLAYER_JOIN, player,this);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerQuit(PlayerQuitEvent evt){
        EventResult result = this.events.emit(STD.EVENT_GLOBAL_PLAYER_QUIT, evt.getPlayer(),evt.getQuitMessage());
        evt.setQuitMessage((String) result.getEventArguments()[1]);
        if(isInside(evt.getPlayer().getUniqueId())){
            leavePlayer(evt.getPlayer());
        }
    }

    public ScenePlayer getScenePlayerIfPresent(Player player) {
        return getScenePlayerIfPresent(player.getUniqueId());
    }

    public ScenePlayer getScenePlayerIfPresent(UUID player) {
        return playerList.getIfPresent(player);
    }

    public Optional<ScenePlayer> getScenePlayer(UUID player){
        return Optional.ofNullable(playerList.getIfPresent(player));
    }

    public Optional<ScenePlayer> getScenePlayer(Player player){
       return getScenePlayer(player.getUniqueId());
    }

    public Set<UUID> players(){
        return playerList.asMap().keySet();
    }

    public boolean isInside(UUID player){
        return playerList.getIfPresent(player) != null;
    }
    public boolean isInside(Player player) {
        return isInside(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent evt){
        EventResult result = this.events.emit(STD.EVENT_GLOBAL_PLAYER_JOIN, evt.getPlayer(),evt.getJoinMessage());
        evt.setJoinMessage((String) result.getEventArguments()[1]);
        if(autoJoinPlayers){
            joinPlayer(evt.getPlayer());
        }
    }


    public void leavePlayer(Player player){
        this.events.emit(STD.EVENT_PLAYER_LEFT, player, this);
        this.playerList.invalidate(player.getUniqueId());
    }

    public void broadcastMessage(String msg){
        String ctx = msg.replaceAll("&", "§");
        this.playerList.asMap().values().forEach(scenePlayer -> scenePlayer.getPlayer().ifPresent(pl -> pl.sendMessage(ctx)));
    }

    public void doPipeline(Consumer<Player> consumer){
        this.playerList.asMap().values().forEach(scenePlayer -> scenePlayer.getPlayer().ifPresent(consumer));
    }

    public void executeRawForPlayer(Player player, String... parsingRaw){
        executeForPlayer(player, Arrays.stream(parsingRaw).map(Parsing::new).collect(Collectors.toList()).toArray(new Parsing[parsingRaw.length]));
    }

    public void executeRaw(String... parsingRaw){
        execute(Arrays.stream(parsingRaw).map(Parsing::new).collect(Collectors.toList()).toArray(new Parsing[parsingRaw.length]));
    }


    public void executeForPlayer(Player player, Parsing... parsings){
        WildcardGameScene scene = new WildcardGameScene(this);
        WildcardPlayer playerScene = new WildcardPlayer(player);
        for(Parsing parsing : parsings){
            FunctionManager.execute(parsing, this, scene, playerScene);
        }
    }

    public void execute(Parsing... parsings){
        WildcardGameScene scene = new WildcardGameScene(this);
        for(Parsing parsing : parsings){
            FunctionManager.execute(parsing, this, scene);
        }
    }

    @Override
    public void remove() {
        schedulers.remove();
        HandlerList.unregisterAll(this);
        SceneManager.unloadScene(name);
        objects.remove();
    }
}
