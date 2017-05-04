package rjsv.floatingmenu.floatingmenubutton.subbutton;

import android.view.View;

/**
 * Description
 *
 * @author <a href="mailto:ricardo.vieira@xpand-it.com">RJSV</a>
 * @version $Revision : 1 $
 */

public class SubButton {

    private int x;
    private int y;
    private int width;
    private int height;
    private float alpha;
    private View view;

    public SubButton(View view, int width, int height) {
        this.view = view;
        this.width = width;
        this.height = height;
        this.alpha = view.getAlpha();
        this.x = 0;
        this.y = 0;
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
        this.view.setAlpha(alpha);
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

}
