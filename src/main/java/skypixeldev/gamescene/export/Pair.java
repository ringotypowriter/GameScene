package skypixeldev.gamescene.export;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class Pair<T,S> {
    private T first;
    private S second;

    public Pair<S,T> reverse(){
        return new Pair<>(second, first);
    }
}
