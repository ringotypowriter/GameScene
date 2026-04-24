package skypixeldev.gamescene.journals.conversation;

import lombok.*;
import org.bukkit.entity.Player;

public interface ConversationPrompt {
    @Value
    @Builder
    class PromptResult{
        @Override
        public String toString() {
            return "PromptResult{" +
                    "delayTicks=" + delayTicks +
                    ", endConversation=" + endConversation +
                    ", newPage=" + newPage +
                    ", lock=" + lock +
                    '}';
        }

        @Builder.Default
        int delayTicks = 100;
        @Builder.Default
        boolean endConversation = false;
        @Builder.Default
        boolean newPage = false;
        @Builder.Default
        boolean lock = false;
    }

    void sendPrompt(Player receiver, Conversation conversation);
    PromptResult getResult(Player player, Conversation conversation);
}
