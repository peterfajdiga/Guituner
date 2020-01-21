package peterfajdiga.guituner.gui;

import android.animation.Animator;
import android.view.View;
import android.view.ViewAnimationUtils;

public class RippleVisibility {
    private RippleVisibility() {}

    public static void showViewWithRipple(final View view, final int centerX, final int centerY) {
        if (view.getVisibility() == View.VISIBLE) {
            return;
        }
        view.setVisibility(View.VISIBLE);
        createCircularRevealFullSize(view, centerX, centerY, false).start();
    }

    public static void showViewWithRipple(final View view) {
        final int centerX = view.getWidth() / 2;
        final int centerY = view.getHeight() / 2;
        showViewWithRipple(view, centerX, centerY);
    }

    public static void hideViewWithRipple(final View view, final int centerX, final int centerY) {
        if (view.getVisibility() == View.INVISIBLE) {
            return;
        }
        Animator anim = createCircularRevealFullSize(view, centerX, centerY, true);
        anim.addListener(new HideListener(view));
        anim.start();
    }

    public static void hideViewWithRipple(final View view) {
        final int centerX = view.getWidth() / 2;
        final int centerY = view.getHeight() / 2;
        hideViewWithRipple(view, centerX, centerY);
    }

    private static Animator createCircularRevealFullSize(final View view, final int centerX, final int centerY, final boolean reverse) {
        final float radius = getRadius(view.getWidth(), view.getHeight(), centerX, centerY);
        if (reverse) {
            return ViewAnimationUtils.createCircularReveal(view, centerX, centerY, radius, 0.0f);
        } else {
            return ViewAnimationUtils.createCircularReveal(view, centerX, centerY, 0.0f, radius);
        }
    }

    private static float getRadius(final int width, final int height, final int centerX, final int centerY) {
        return getDiagonal(Math.max(centerX, width-centerX), Math.max(centerY, height-centerY));
    }

    private static float getDiagonal(final int width, final int height) {
        return (float)Math.sqrt(width*width + height*height);
    }

    private static class HideListener implements Animator.AnimatorListener {
        private final View viewToHide;

        HideListener(final View view) {
            this.viewToHide = view;
        }

        @Override
        public void onAnimationStart(final Animator animator) {}

        @Override
        public void onAnimationEnd(final Animator animator) {
            viewToHide.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onAnimationCancel(final Animator animator) {}

        @Override
        public void onAnimationRepeat(final Animator animator) {}
    }
}
