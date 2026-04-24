package skypixeldev.gamescene.journals.conversation;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import skypixeldev.gamescene.Bootstrap;
import skypixeldev.gamescene.Log;
import skypixeldev.gamescene.journals.Journals;

import java.util.*;

public class Conversation {

    public Conversation(Player player, ConversationStructure structure){
        this.structure = structure;
        this.owner = player.getUniqueId();
    }

    @Getter private final ConversationStructure structure;
    private final UUID owner;
    private final Map<String,String> data = new HashMap<>();
    private boolean lock = false;
    private BukkitTask task;


    public String getData(String key){
        return data.get(key);
    }

    public String setData(String key, String val){
        return data.put(key, val);
    }

    public boolean containsData(String key){
        return data.containsKey(key);
    }

    public boolean isStructureValid(){
        return structure.isValid();
    }

    public UUID getOwner() {
        return owner;
    }

    public Optional<Player> getPlayer(){
        return Optional.ofNullable(Bukkit.getPlayer(owner));
    }

    public Player getPlayerIfPresent(){
        return getPlayer().orElse(null);
    }

    public void endConversation(){
        if(Journals.isInConversation(owner)){
            if(task != null){
                task.cancel();
            }
            Journals.unregisterRunningConversation(owner);
            if(!containsData("NextConversation")) {
                getPlayer().ifPresent(pl -> Journals.getHandler().endConversation(pl, this));
                getPlayer().ifPresent(pl -> pl.sendRawMessage("§c-------对话结束-------"));
            }
            unlock();
        }
    }

    public boolean runConversation(){
        lock = false;
        Player pl = getPlayerIfPresent();
        if(pl == null){
            return false;
        }
        if(!structure.isValid()){
            return false;
        }
        Journals.registerRunningConversation(owner, this);
        Journals.getHandler().cleanPage(pl, this);
        task = new BukkitRunnable(){
            final Player player = pl;
            int delayAction = 0;
            int cursorOrder = 0;

            @Override
            public void run() {
                if(delayAction <= 0){
                    ConversationPrompt prompt = getStructure().at(cursorOrder);

                    if(prompt == null){
                        end();
                    }else{
                        prompt.sendPrompt(player, Conversation.this);
                        ConversationPrompt.PromptResult result = prompt.getResult(player, Conversation.this);
                        delayAction = result.getDelayTicks();
                        if(result.isNewPage()){
                            Journals.getHandler().cleanPage(pl, Conversation.this);
                        }
                        if(result.isLock()){
                            lock();
                        }
                        if(result.isEndConversation()){
                            end();
                        }
                        cursorOrder++;
                    }
                }else{
                    if(!isLock()) {
                        delayAction--;
                    }
                }
            }

            public void end(){
                task = null;
                Journals.unregisterRunningConversation(player.getUniqueId());
                if(!containsData("NextConversation")) {
                    Journals.getHandler().endConversation(player, Conversation.this);
                }
                cancel();
                unlock();
            }
        }.runTaskTimerAsynchronously(Bootstrap.getInstance(), 1,1);
        return true;
    }

    public boolean isLock() {
        return lock;
    }

    public void lock(){
        lock = true;
    }

    public void unlock(){
        lock = false;
    }
}
