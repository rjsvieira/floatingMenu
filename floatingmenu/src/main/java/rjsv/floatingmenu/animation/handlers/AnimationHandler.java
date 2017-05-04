package rjsv.floatingmenu.animation.handlers;

import android.graphics.Point;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import rjsv.floatingmenu.animation.enumerators.AnimationType;
import rjsv.floatingmenu.animation.enumerators.MenuState;
import rjsv.floatingmenu.floatingmenubutton.FloatingMenuButton;
import rjsv.floatingmenu.floatingmenubutton.subbutton.SubButton;

/**
 * Description
 *
 * @author <a href="mailto:ricardo.vieira@xpand-it.com">RJSV</a>
 * @version $Revision : 1 $
 */

public abstract class AnimationHandler {

    protected FloatingMenuButton menu;

    public void setMenu(FloatingMenuButton menu) {
        this.menu = menu;
    }

    public void animateMenuOpening(Point center, AnimationType animationType) {
    }

    public void animateMenuClosing(Point center, AnimationType animationType) {
    }

    public void animateMenuReOpening(Point center) {
    }

    public void restoreSubActionViewAfterAnimation(SubButton subActionItem, MenuState actionType) {
        ViewGroup.LayoutParams params = subActionItem.getView().getLayoutParams();
        View subActionItemView = subActionItem.getView();
        int openFactor = (actionType == MenuState.OPENING || actionType == MenuState.OPENING_RADIAL) ? 1 : (actionType == MenuState.CLOSING || actionType == MenuState.CLOSING_RADIAL ? 0 : -1);
        subActionItemView.setTranslationX(0);
        subActionItemView.setTranslationY(0);
        subActionItemView.setRotation(0);
        subActionItemView.setScaleX(openFactor);
        subActionItemView.setScaleY(openFactor);
        subActionItemView.setAlpha(openFactor);
        if (openFactor > 0) { // open
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) params;
            lp.setMargins(subActionItem.getX(), subActionItem.getY(), 0, 0);
            subActionItemView.setLayoutParams(lp);
        } else { // close
            Point center = menu.getActionViewCenter();
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) params;
            int x = center.x - subActionItem.getWidth() / 2;
            int y = center.y - subActionItem.getHeight() / 2;
            lp.setMargins(x, y, 0, 0);
            subActionItemView.setLayoutParams(lp);
            menu.removeViewFromCurrentContainer(subActionItemView);
        }
    }

    public abstract boolean isAnimating();

    public abstract void setAnimating(boolean animating);

}
