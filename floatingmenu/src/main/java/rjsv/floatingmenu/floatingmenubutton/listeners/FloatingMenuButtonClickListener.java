package rjsv.floatingmenu.floatingmenubutton.listeners;

import android.view.View;

import java.util.ArrayList;

/**
 * Description
 *
 * @author <a href="mailto:ricardo.vieira@xpand-it.com">RJSV</a>
 * @version $Revision : 1 $
 */

public class FloatingMenuButtonClickListener implements View.OnClickListener {

    private ArrayList<View.OnClickListener> listeners;

    public FloatingMenuButtonClickListener() {
        this.listeners = new ArrayList<>();
    }

    public void addClickListener(View.OnClickListener listener) {
        if (listener != null) {
            this.listeners.add(listener);
        } else {
            this.listeners.clear();
        }
    }

    @Override
    public void onClick(View v) {
        if (this.listeners != null) {
            for (View.OnClickListener listener : listeners) {
                listener.onClick(v);
            }
        }
    }

}
