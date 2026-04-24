package skypixeldev.gamescene.interfaces;

import java.util.Comparator;

public interface Prioritized{
    Comparator<Prioritized> COMPARATOR = (o1, o2) -> o2.getPriority() - o1.getPriority();
    int getPriority();
}
