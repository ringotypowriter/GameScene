package skypixeldev.gamescene.journals;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("journal")
public class JournalCommand extends BaseCommand {
    @Subcommand("conversation")
    @CommandAlias("jconver")
    @CommandPermission("journals.conversation")
    @CommandCompletion("@players")
    @Syntax("<Conversation File Name> [Player]")
    public void conversation(CommandSender user, String conversationName, @Optional String player){
        String username = player;
        if(player == null && user instanceof Player){
            username = user.getName();
        }
        if(username == null){
            user.sendMessage("§e你没有指定一个玩家!");
            return;
        }
        if(conversationName == null){
            user.sendMessage("§e你没有指定一个对话");
            return;
        }
        Player userPl = Bukkit.getPlayer(username);
        if(userPl == null){
            user.sendMessage("§e指定的玩家无效!");
            return;
        }
        user.sendMessage("§a已请求开始对话 §6§nNothing means fine.");
        Journals.startConversation(userPl, conversationName, fallback -> {
            fallback.sendTitle("§f<§c错误§f>","§e对话文件载入失败, 请截图回报管理员§8(" + conversationName + ")",1,20,1);
            user.sendMessage("§e对话文件载入出错");
        });

    }
}
