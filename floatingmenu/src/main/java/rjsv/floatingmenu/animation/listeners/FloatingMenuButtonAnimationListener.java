package rjsv.floatingmenu.animation.listeners;

import android.animation.Animator;

import rjsv.floatingmenu.floatingmenubutton.subbutton.SubButton;
import rjsv.floatingmenu.animation.handlers.AnimationHandler;
import rjsv.floatingmenu.animation.enumerators.MenuState;

/**
 * Description
 *
 * @author <a href="mailto:ricardo.vieira@xpand-it.com">RJSV</a>
 * @version $Revision : 1 $
 */

public class FloatingMenuButtonAnimationListener implements Animator.AnimatorListener {

    private SubButton subActionItem;
    private MenuState actionType;
    private AnimationHandler animationHandler;

    public FloatingMenuButtonAnimationListener(AnimationHandler animationHandler, SubButton subActionItem, MenuState actionType) {
        this.subActionItem = subActionItem;
        this.actionType = actionType;
        this.animationHandler = animationHandler;
    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
        animationHandler.restoreSubActionViewAfterAnimation(subActionItem, actionType);
    }

    @Override
    public void onAnimationCancel(Animator animation) {
        animationHandler.restoreSubActionViewAfterAnimation(subActionItem, actionType);
    }

    @Override
    public void onAnimationRepeat(Animator animation) {
    }
}