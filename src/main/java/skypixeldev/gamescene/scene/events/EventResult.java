package skypixeldev.gamescene.scene.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data @AllArgsConstructor
public class EventResult {
    @Getter private final boolean cancelled;
    @Getter private final Object[] eventArguments;
}
