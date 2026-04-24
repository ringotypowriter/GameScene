package skypixeldev.gamescene.export;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Random;

@RequiredArgsConstructor
public class RangedRandom {
    private Random random = new Random(System.currentTimeMillis() - 2000);

    public void setRandomSeed(long seed) {
         random.setSeed(seed);
    }
    @Getter @NonNull private int randomMin;
    @Getter @NonNull private int randomMax;

    public int nextInt(){
        return random.nextInt(randomMax + 1 - randomMin) + randomMin;
    }

    public float nextFloat(){
        return Math.max(randomMin, Math.min(randomMax,nextInt() + random.nextFloat()));
    }
}
