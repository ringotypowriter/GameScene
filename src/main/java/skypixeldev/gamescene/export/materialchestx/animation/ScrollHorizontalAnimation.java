package skypixeldev.gamescene.export.materialchestx.animation;

import java.util.List;

public class ScrollHorizontalAnimation extends Animation {

    long lastTime = -1;

    @Override
    public void reset() {
        lastTime = -1;
    }

    @Override
    public Animation clone() {
        return new ScrollHorizontalAnimation();
    }

    @Override
    public int getFrameSlot(int slots, List<Integer> available) {
        if (isPlaying()) {
            if (lastTime < 0) {
                lastTime = System.currentTimeMillis();
            }
            if (System.currentTimeMillis() - lastTime >= 1200) {
                lastTime = -1;
                this.closure();
                return slots;
            }
            double f = ((System.currentTimeMillis() - lastTime) / 1200d);
            if (f > 1) {
                f = 1;
            }
//			System.out.println("Float process: " + f + " (" + (System.currentTimeMillis()-lastTime) + " / 1200d )");
            for (int i : available.subList(0, (int) (available.size() * f))) {
                if (i == slots) {
                    return slots;
                }
            }
            return -1;
        }
        return slots;
    }
}
