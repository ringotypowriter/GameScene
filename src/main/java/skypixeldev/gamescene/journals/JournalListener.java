package skypixeldev.gamescene.journals;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import skypixeldev.gamescene.journals.conversation.Conversation;

import java.util.Optional;

public class JournalListener implements Listener {
    @EventHandler
    public void onQuit(PlayerQuitEvent evt){
        Optional.ofNullable(Journals.runningConversations.get(evt.getPlayer().getUniqueId())).ifPresent(Conversation::endConversation);
    }
}
