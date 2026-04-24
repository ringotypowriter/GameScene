package skypixeldev.gamescene.journals.conversation.prompts;

import org.bukkit.entity.Player;
import skypixeldev.gamescene.Bootstrap;
import skypixeldev.gamescene.Log;
import skypixeldev.gamescene.journals.Journals;
import skypixeldev.gamescene.journals.conversation.Conversation;
import skypixeldev.gamescene.journals.conversation.ConversationPrompt;
import skypixeldev.gamescene.parser.FunctionManager;
import skypixeldev.gamescene.parser.Parsing;
import skypixeldev.gamescene.parser.wildcard.WildcardPlayer;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;

public class SelectionPrompt implements ConversationPrompt {

    private Map<String, String> selectionFunctions;
    public SelectionPrompt(){
        selectionFunctions = new LinkedHashMap<>();
    }

    public void addSelection(String selectionText, String functionText) {
        selectionFunctions.put(selectionText, functionText);

    }

    private Map<String, Consumer<Player>> compile(final Conversation conversation){
        Map<String,Consumer<Player>> map = new LinkedHashMap<>();
        for(Map.Entry<String,String> entry : selectionFunctions.entrySet()) {
            String text = entry.getValue();
            if (text.contains("(") && text.contains(")")) {
                try {
                    Parsing parsed = new Parsing(text);
                    Log.lJr("Parsed Function: " + parsed);
                    map.put(entry.getKey(), pl -> {
                        FunctionManager.execute(parsed, null, new WildcardPlayer(pl));
                        conversation.unlock();
                    });
                }catch (Throwable throwable){
                    Bootstrap.getOLogger().log(Level.WARNING,"failed to parse function text of " + text, throwable);
                }
            }else{
                Log.lJr("Conversation Named Function: " + text);
                if(text.isEmpty()){
                    map.put(entry.getKey(), player -> conversation.unlock()); // Just End
                }
                map.put(entry.getKey(), new Consumer<Player>() {
                    final String conversationName = text;
                    @Override
                    public void accept(Player player) {
                        conversation.unlock();
                        conversation.setData("NextConversation", conversationName);
                        Journals.startConversation(player, conversationName);
                    }
                });
            }
        }
        return map;
    }

    @Override
    public void sendPrompt(Player receiver, Conversation conversation) {
        Journals.getHandler().normalSelection(receiver, conversation,compile(conversation));
    }

    @Override
    public PromptResult getResult(Player player, Conversation conversation) {
        return PromptResult.builder().delayTicks(20).lock(true).build();
    }
}
