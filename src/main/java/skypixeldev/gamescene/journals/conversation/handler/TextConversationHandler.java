package skypixeldev.gamescene.journals.conversation.handler;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.PaperCommandManager;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import skypixeldev.gamescene.Bootstrap;
import skypixeldev.gamescene.journals.conversation.Conversation;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class TextConversationHandler implements ConversationHandler{

    public TextConversationHandler(){
        PaperCommandManager manager = Bootstrap.getCommandManager();
        manager.registerCommand(new SelectionCommand());
    }

    @CommandAlias("csel")
    private static class SelectionCommand extends BaseCommand{
        @Subcommand("pick")
        public void pick(Player player, String label){
            Consumer<Player> handler = selectionHandler.getIfPresent(label);
            if(handler != null){
                handler.accept(player);
            }else{
                player.sendMessage("§c该对话选项已失效!");
            }
        }
    }

    protected final static Cache<String,Consumer<Player>> selectionHandler = Caffeine.newBuilder()
            .expireAfterAccess(10, TimeUnit.MINUTES).expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(10000)
            .build();

    @Override
    public void normalMessage(Player player, Conversation conversation, String speaker, List<String> text) {
        player.sendRawMessage("");
        player.sendRawMessage("  §e" + speaker);
        text.forEach(str -> player.sendRawMessage("§f" + str));
        player.sendRawMessage("");
        player.playSound(player.getLocation(), Sound.BLOCK_TRIPWIRE_CLICK_ON,1,0.5f);
    }

    @Override
    public void cleanPage(Player player, Conversation conversation) {
        for(int i = 0; i < 16;i++){
            player.sendRawMessage("");
        }
    }

    @Override
    public void normalSelection(Player player, Conversation conversation, Map<String, Consumer<Player>> selections) {

        player.sendRawMessage("");
        Map<String,String> labelMaps = new HashMap<>();
        int order = 0;
        for(Map.Entry<String,Consumer<Player>> entry : selections.entrySet()){
            order++;
            String label = UUID.randomUUID().toString();
            TextComponent textComponent = new net.md_5.bungee.api.chat.TextComponent();
            textComponent.setText("    §6§n" + order + "." + entry.getKey());
            textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,TextComponent.fromLegacyText("§e点击回复 ("+ order + ")")));
            textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/csel pick " + label));
            labelMaps.put(entry.getKey(),label);
            player.spigot().sendMessage(textComponent);
            player.playSound(player.getLocation(), Sound.BLOCK_SHULKER_BOX_OPEN,1,0.5f);
        }
        player.sendRawMessage("");

        for(Map.Entry<String,Consumer<Player>> entry : selections.entrySet()){
            String label = labelMaps.get(entry.getKey());
            selectionHandler.put(label, new Consumer<Player>() {
                final Consumer<Player> original = entry.getValue();
                final List<String> toRemove = new ArrayList<>(labelMaps.values());
                @Override
                public void accept(Player player) {
                    original.accept(player);
                    toRemove.forEach(selectionHandler::invalidate);
                }
            });
        }
    }

    @Override
    public void endConversation(Player player, Conversation conversation) {

    }
}
