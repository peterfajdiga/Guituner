package peterfajdiga.guituner.gui;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

public class AlphaVisibility {
    private static final long DURATION = 200;

    private AlphaVisibility() {}

    public static void showView(final View view) {
        if (view.getVisibility() == View.VISIBLE) {
            return;
        }
        view.setVisibility(View.VISIBLE);
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(DURATION);
        anim.setFillAfter(false);
        view.startAnimation(anim);
    }

    public static void hideView(final View view) {
        if (view.getVisibility() == View.INVISIBLE) {
            return;
        }
        AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
        anim.setDuration(DURATION);
        anim.setFillAfter(false);
        anim.setAnimationListener(new HideListener(view));
        view.startAnimation(anim);
    }

    private static class HideListener implements Animation.AnimationListener {
        private final View viewToHide;

        HideListener(final View view) {
            this.viewToHide = view;
        }

        @Override
        public void onAnimationStart(final Animation animation) {}

        @Override
        public void onAnimationEnd(final Animation animation) {
            viewToHide.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onAnimationRepeat(final Animation animation) {}
    }
}
