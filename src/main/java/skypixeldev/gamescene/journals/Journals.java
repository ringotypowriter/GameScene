package skypixeldev.gamescene.journals;

import co.aikar.commands.PaperCommandManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import skypixeldev.gamescene.Bootstrap;
import skypixeldev.gamescene.Log;
import skypixeldev.gamescene.journals.conversation.Conversation;
import skypixeldev.gamescene.journals.conversation.ConversationStructure;
import skypixeldev.gamescene.journals.conversation.handler.ConversationHandler;
import skypixeldev.gamescene.journals.conversation.handler.HoveringConversationHandler;
import skypixeldev.gamescene.journals.conversation.handler.TextConversationHandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class Journals {
    private static ConversationHandler handler;

    protected static ConcurrentHashMap<UUID, Conversation> runningConversations = new ConcurrentHashMap<>();

    public static void registerRunningConversation(UUID uuid, Conversation conversation){
        if(runningConversations.containsKey(uuid)){
            runningConversations.get(uuid).endConversation();
        }
        runningConversations.put(uuid, conversation);
    }

    public static boolean isInConversation(Player player){
        return runningConversations.containsKey(player.getUniqueId());
    }

    public static boolean isInConversation(UUID uuid){
        return runningConversations.containsKey(uuid);
    }

    public static void unregisterRunningConversation(UUID uuid){
        runningConversations.remove(uuid);
    }

    private static File journalsFolder;
    private static PaperCommandManager journalCommandManager;

    public static void initialize(){
        handler = new HoveringConversationHandler();
        journalsFolder = new File(Bootstrap.getInstance().getDataFolder(),"Journals");
        if(journalsFolder.mkdirs() && journalsFolder.isDirectory()){
            Log.lJr("Loading Conversation Files...");
            Log.lJr("Available: " + Arrays.toString(journalsFolder.list()));
        }

        journalCommandManager =  new PaperCommandManager(Bootstrap.getInstance());
        journalCommandManager.registerCommand(new JournalCommand());
        Log.lJr("Loaded Journal Commands");
        Bukkit.getPluginManager().registerEvents(new JournalListener(), Bootstrap.getInstance());
        Log.lJr("Loaded Journal Listener");
    }

    public static void loadStructureAsync(String fileName, Consumer<ConversationStructure> structureConsumer){
        new BukkitRunnable(){
            @Override
            public void run() {
                structureConsumer.accept(loadStructureAsync(fileName));
            }
        }.runTaskAsynchronously(Bootstrap.getInstance());
    }

    public static void loadConversation(final Player player, final String conversationName, final Consumer<Conversation> conversationConsumer){
        loadStructureAsync(conversationName, structure -> conversationConsumer.accept(new Conversation(player, structure)));
    }

    public static void startConversation(final Player player, final String conversationName) {
        startConversation(player, conversationName, player1 -> {});
    }

    public static void startConversation(final Player player, final String conversationName,final Consumer<Player> failCallback){
        loadConversation(player, conversationName, conversation -> {
            if(conversation.isStructureValid()){
                if(!conversation.runConversation()){
                    failCallback.accept(player);
                }
            }else{
                failCallback.accept(player);
            }
        });
    }

    public static ConversationStructure loadStructureAsync(String fileName){
        ConversationStructure conversationStructure = new ConversationStructure();
        File file = new File(journalsFolder,fileName + ".journal");
        if(file.exists()){
            try {
                conversationStructure.loadPrompts(Files.readAllLines(file.toPath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return conversationStructure;
    }

    public static ConversationHandler getHandler() {
        return handler;
    }


}
