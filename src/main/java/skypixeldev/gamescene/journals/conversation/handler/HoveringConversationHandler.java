package skypixeldev.gamescene.journals.conversation.handler;

import com.google.common.collect.Maps;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import skypixeldev.gamescene.GlobalSchedulers;
import skypixeldev.gamescene.Log;
import skypixeldev.gamescene.journals.conversation.Conversation;
import skypixeldev.gamescene.journals.conversation.hovering.Hovering;
import skypixeldev.gamescene.journals.conversation.hovering.HoveringManager;

import java.util.*;
import java.util.function.Consumer;

public class HoveringConversationHandler implements ConversationHandler{

    private final static Set<String> selectionLabels = new HashSet<>();

    @Override
    public void normalMessage(Player player, Conversation conversation, String speaker, List<String> text) {
        GlobalSchedulers.getInstance().runSync(() -> {
            Hovering hovering = HoveringManager.getHoveringOrCreate(player);
            hovering.addLine("§e" + speaker + ":");
            text.forEach(t -> hovering.addLine("§f" + t));
            hovering.addLine(" ");
            player.playSound(player.getLocation(), Sound.BLOCK_WOOD_PRESSUREPLATE_CLICK_OFF,1,2f);
        });

    }

    @Override
    public void cleanPage(Player player, Conversation conversation) {
        GlobalSchedulers.getInstance().runSync(() -> HoveringManager.getHoveringOrCreate(player).clear());
    }

    @Override
    public void normalSelection(Player player, Conversation conversation, Map<String, Consumer<Player>> selections) {
        GlobalSchedulers.getInstance().runSync(() -> {
            Hovering hovering = HoveringManager.getHoveringOrCreate(player);
            int order = 0;
            Map<String, String> labelNames = new HashMap<>();
            for(String key : selections.keySet()){
                labelNames.put(key, UUID.randomUUID().toString());
            }
            selectionLabels.addAll(labelNames.values());
            for (Map.Entry<String, Consumer<Player>> entry : selections.entrySet()) {
                order++;
                hovering.addClickableLine("§6§n" + order + "." + entry.getKey(), new Consumer<Player>() {
                    final String label = labelNames.get(entry.getKey());
                    final Consumer<Player> original = entry.getValue();
                    @Override
                    public void accept(Player player) {
                        if (!selectionLabels.contains(label)) {
                            player.sendMessage("§c该对话选项已失效");
                        } else {
                            original.accept(player);
                            selectionLabels.removeAll(labelNames.values());
                        }
                    }
                });
            }
            player.playSound(player.getLocation(), Sound.BLOCK_SHULKER_BOX_OPEN, 1, 0.5f);
        });
    }

    @Override
    public void endConversation(Player player, Conversation conversation) {
        GlobalSchedulers.getInstance().runSync(() -> HoveringManager.getHoveringOrCreate(player).destroy());
    }
}
