package skypixeldev.gamescene.journals.conversation.prompts;

import org.bukkit.entity.Player;
import skypixeldev.gamescene.Bootstrap;
import skypixeldev.gamescene.journals.conversation.Conversation;
import skypixeldev.gamescene.journals.conversation.ConversationPrompt;
import skypixeldev.gamescene.parser.FunctionManager;
import skypixeldev.gamescene.parser.Parsers;
import skypixeldev.gamescene.parser.Parsing;
import skypixeldev.gamescene.parser.Selector;
import skypixeldev.gamescene.parser.wildcard.WildcardPlayer;

import java.util.logging.Level;

public class FunctionPrompt implements ConversationPrompt {

    private int customDelay = 100;
    private Parsing parsing;

    public FunctionPrompt(String functionText){
        try{
            customDelay = Integer.parseInt(functionText);
        }catch (NumberFormatException ignored){
            try {
                parsing = new Parsing(functionText);
            }catch (Throwable throwable) {
                Bootstrap.getOLogger().log(Level.WARNING, "[Journal] Failed to parse function text of " + parsing, throwable);
            }
        }

        if(parsing != null){
            if(parsing.getFunctionName().equalsIgnoreCase("delay")){
                try{
                    customDelay = Parsers.parseInteger(Selector.searchNonNull(parsing.getArguments(),"val","value","v","t","tick"),customDelay);
                }catch (NumberFormatException ignored){}
            }
        }
    }

    @Override
    public void sendPrompt(Player receiver, Conversation conversation) {
        if(!parsing.getFunctionName().equalsIgnoreCase("delay")){
            FunctionManager.execute(parsing,null, new WildcardPlayer(receiver));
        }
    }

    @Override
    public PromptResult getResult(Player player, Conversation conversation) {
        return PromptResult.builder().delayTicks(customDelay).build();
    }
}
