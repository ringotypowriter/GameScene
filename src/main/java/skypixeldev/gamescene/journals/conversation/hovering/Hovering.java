package skypixeldev.gamescene.journals.conversation.hovering;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.handler.TouchHandler;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import skypixeldev.gamescene.Bootstrap;

import java.util.UUID;
import java.util.function.Consumer;

public class Hovering {
    private final int maximumLength;
    private final Hologram hologram;
    private final Player viewer;
    private final UUID viewerID;
    private final double originY;
    public Hovering(@NonNull Player viewer,@NonNull int maximumLength){
        this.maximumLength = maximumLength;
        this.viewer = viewer;
        this.viewerID = viewer.getUniqueId();
        HoveringManager.addHovering(this);
        Location location = viewer.getLocation().clone().add(viewer.getLocation().getDirection().setY(0));
        location.setY(viewer.getEyeLocation().getY());
        hologram = HologramsAPI.createHologram(Bootstrap.getInstance(), location);
        hologram.getVisibilityManager().setVisibleByDefault(false);
        hologram.getVisibilityManager().showTo(viewer);
        originY = hologram.getLocation().getY();
    }

    public void destroy(){
        hologram.delete();
        HoveringManager.removeHoveringRecord(viewer.getUniqueId());
    }

    public Player getViewer(){
        return viewer;
    }

    private String handleText(String text){
        if(containRepeatChar(text,' ') && text.length() <= 10){
            return "                         ";
        }else if(text.length() <= 6){
            return text + "        ";
        }
        return text;
    }

    public void addLine(String line){
        if(hologram.size() < maximumLength) {
            hologram.teleport(hologram.getLocation().add(0, 0.2, 0));
        }
        hologram.appendTextLine(handleText(line));
        updateSize();
    }

    public void addClickableLine(String line,final Consumer<Player> handler){
        if(hologram.size() < maximumLength) {
            hologram.teleport(hologram.getLocation().add(0, 0.2, 0));
        }
        hologram.appendTextLine(handleText(line)).setTouchHandler(new TouchHandler() {
            final Consumer<Player> inner = handler;
            @Override
            public void onTouch(Player player) {
                inner.accept(player);
            }
        });
        updateSize();
    }

    public void clear(){
        hologram.clearLines();
        hologram.teleport(hologram.getWorld(),hologram.getX(), originY, hologram.getZ());
    }

    private void updateSize(){
        if(hologram.size() > maximumLength){
            hologram.removeLine(0);
        }
        if(hologram.size() > maximumLength/2) {
            while (containRepeatChar(((TextLine) hologram.getLine(0)).getText(), ' ')) {
                hologram.removeLine(0);
            }
        }
    }


    private boolean containRepeatChar(String str, char symbol){
        if(str==null||str.isEmpty()){
            return false;
        }
        char[] elements=str.toCharArray();
        for(char e:elements){
            if(str.indexOf(e)!=str.lastIndexOf(e)){
                return e == symbol;
            }
        }
        return false;
    }


    public UUID getViewerID() {
        return viewerID;
    }
}
