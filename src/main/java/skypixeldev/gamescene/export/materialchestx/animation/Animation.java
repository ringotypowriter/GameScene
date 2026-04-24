package skypixeldev.gamescene.export.materialchestx.animation;

import java.util.List;

public abstract class Animation {
    private boolean playing = false;

    public abstract void reset();

    public void play() {
        reset();
        playing = true;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void closure() {
        playing = false;
        reset();
    }

    public abstract Animation clone();

    public abstract int getFrameSlot(int slots, List<Integer> available);
}
