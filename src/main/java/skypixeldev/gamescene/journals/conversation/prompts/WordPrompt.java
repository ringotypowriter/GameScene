package skypixeldev.gamescene.journals.conversation.prompts;

import org.bukkit.entity.Player;
import skypixeldev.gamescene.journals.Journals;
import skypixeldev.gamescene.journals.conversation.Conversation;
import skypixeldev.gamescene.journals.conversation.ConversationPrompt;

import java.util.Arrays;
import java.util.List;

public class WordPrompt implements ConversationPrompt {

    private String speaker;
    private List<String> sentences;

    public String getSpeaker() {
        return speaker;
    }

    public List<String> getSentences() {
        return sentences;
    }

    public WordPrompt(String speaker, String... sentences){
        this(speaker, Arrays.asList(sentences));
    }

    public WordPrompt(String speaker, List<String> sentences){
        this.speaker = speaker;
        this.sentences = sentences;
    }


    @Override
    public void sendPrompt(Player receiver, Conversation conversation) {
        Journals.getHandler().normalMessage(receiver, conversation, speaker, sentences);
    }

    @Override
    public PromptResult getResult(Player player, Conversation conversation) {
        return PromptResult.builder().build();
    }
}
