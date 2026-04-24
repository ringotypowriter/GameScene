package skypixeldev.gamescene.journals.conversation;

import skypixeldev.gamescene.Log;
import skypixeldev.gamescene.journals.conversation.prompts.FunctionPrompt;
import skypixeldev.gamescene.journals.conversation.prompts.SelectionPrompt;
import skypixeldev.gamescene.journals.conversation.prompts.WordPrompt;

import java.util.ArrayList;
import java.util.List;

public class ConversationStructure {
    private List<ConversationPrompt> prompts = new ArrayList<>();

    public ConversationStructure with(ConversationPrompt prompt){
        addPrompt(prompt);
        return this;
    }

    public boolean isValid(){
        return !prompts.isEmpty();
    }

    public ConversationPrompt get(int order){
        if(order >= prompts.size()){
            return null;
        }
        return prompts.get(order);
    }

    public ConversationPrompt at(int order){
        return get((order));
    }

    public List<ConversationPrompt> getPrompts() {
        return prompts;
    }

    public void addPrompt(ConversationPrompt prompt){
        this.prompts.add(prompt);
    }



    public void loadPrompts(List<String> rawText){
        SelectionPrompt constructing = null;
        for(Eval.EvalResult result : Eval.eval(rawText)){
            if(constructing == null) {
                if (result instanceof Eval.Prompt) {
                    addPrompt(new WordPrompt(((Eval.Prompt) result).getSpeaker(), ((Eval.Prompt) result).getText()));
                } else if (result instanceof Eval.Function) {
                    addPrompt(new FunctionPrompt(((Eval.Function) result).getFunctionText()));
                } else if (result instanceof Eval.Selection) {
                    constructing = new SelectionPrompt();
                    constructing.addSelection(((Eval.Selection) result).getSelectText(), ((Eval.Selection) result).getFunctionText());
                }
            }else{
                if(result instanceof Eval.Selection){
                    constructing.addSelection(((Eval.Selection) result).getSelectText(), ((Eval.Selection) result).getFunctionText());
                }else{
                    addPrompt(constructing);
                    constructing = null;
                    if (result instanceof Eval.Prompt) {
                        addPrompt(new WordPrompt(((Eval.Prompt) result).getSpeaker(), ((Eval.Prompt) result).getText()));
                    } else if (result instanceof Eval.Function) {
                        addPrompt(new FunctionPrompt(((Eval.Function) result).getFunctionText()));
                    }
                }
            }
        }
        if(constructing != null){
            addPrompt(constructing);
        }
    }
}
