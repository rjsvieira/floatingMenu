package rjsv.floatingmenu.floatingmenubutton.listeners;

import rjsv.floatingmenu.floatingmenubutton.FloatingMenuButton;
import rjsv.floatingmenu.floatingmenubutton.subbutton.SubButton;

/**
 * Description
 *
 * @author <a href="mailto:ricardo.vieira@xpand-it.com">RJSV</a>
 * @version $Revision : 1 $
 */

public class SubButtonViewQueueListener implements Runnable {

    private static final int MAX_TRIES = 10;
    private int tries;
    private SubButton button;
    private FloatingMenuButton floatingActionMenu;

    public SubButtonViewQueueListener(FloatingMenuButton floatingActionMenu, SubButton button) {
        this.floatingActionMenu = floatingActionMenu;
        this.button = button;
        this.tries = 0;
    }

    @Override
    public void run() {
        // Wait until the the view can be measured but do not push too hard.
        if (button.getView().getMeasuredWidth() == 0 && tries < MAX_TRIES) {
            button.getView().post(this);
            return;
        }
        // Measure the size of the button view
        button.setWidth(button.getView().getMeasuredWidth());
        button.setHeight(button.getView().getMeasuredHeight());
        // Revert everything back to normal
        button.setAlpha(button.getAlpha());
        // Remove the button view from view hierarchy
        floatingActionMenu.removeViewFromCurrentContainer(button.getView());
    }

}