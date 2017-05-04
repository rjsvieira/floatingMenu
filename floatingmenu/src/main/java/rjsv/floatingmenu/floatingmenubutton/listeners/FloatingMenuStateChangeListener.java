package rjsv.floatingmenu.floatingmenubutton.listeners;

import rjsv.floatingmenu.floatingmenubutton.FloatingMenuButton;

/**
 * Description
 *
 * @author <a href="mailto:ricardo.vieira@xpand-it.com">RJSV</a>
 * @version $Revision : 1 $
 */

public interface FloatingMenuStateChangeListener {

    void onMenuOpened(FloatingMenuButton menu);

    void onMenuClosed(FloatingMenuButton menu);

}
