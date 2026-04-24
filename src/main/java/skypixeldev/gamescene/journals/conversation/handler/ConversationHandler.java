package skypixeldev.gamescene.journals.conversation.handler;

import org.bukkit.entity.Player;
import skypixeldev.gamescene.journals.conversation.Conversation;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public interface ConversationHandler {
    void normalMessage(Player player, Conversation conversation, String speaker, List<String> text);
    void cleanPage(Player player, Conversation conversation);
    void normalSelection(Player player, Conversation conversation, Map<String, Consumer<Player>> selections);
    void endConversation(Player player, Conversation conversation);
}
