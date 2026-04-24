package skypixeldev.gamescene.export.materialchestx.layout;


import skypixeldev.gamescene.export.itemMenus.ItemMenu;
import skypixeldev.gamescene.export.materialchestx.animation.Animation;
import skypixeldev.gamescene.export.materialchestx.ctx.HeaderContent;

public abstract class Layout {
    private Animation animation;
    private int currentContent = 0;
    private int currentContentPage = 0;

    public abstract ItemMenu onDrawHeaders(ItemMenu menu, HeaderContent[] ctx);

    public abstract ItemMenu onDrawContent(ItemMenu menu, HeaderContent[] ctx);

    public void playAnimation(Animation animation) {
        if (this.animation != null) {
            if (animation.isPlaying()) {
                animation.closure();
                animation = null;
            }
        }
        this.animation = animation;
        animation.play();
    }

    public boolean hasPlayingAnimation() {
        if (animation == null) {
            return false;
        }
        if (!animation.isPlaying()) {
            return false;
        }
        return true;
    }

    public Animation getPlayingAnimation() {
        return animation;
    }

    public void closureAnimationIfPlaying() {
        if (animation != null) {
            if (animation.isPlaying()) {
                animation.closure();
            }
            animation = null;
        }
    }

    public int getCurrentContent() {
        return currentContent;
    }

    public void setCurrentContent(int ctx) {
        this.currentContent = ctx;
        this.currentContentPage = 0;
    }

    public int getCurrentContentPage() {
        return currentContentPage;
    }

    public void setCurrentContentPage(int page) {
        this.currentContentPage = page;
    }
}
