package rjsv.floatingmenu.floatingmenubutton;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import rjsv.floatingmenu.R;
import rjsv.floatingmenu.animation.enumerators.AnimationType;
import rjsv.floatingmenu.animation.handlers.FloatingMenuAnimationHandler;
import rjsv.floatingmenu.floatingmenubutton.general.Utils;
import rjsv.floatingmenu.floatingmenubutton.listeners.FloatingMenuButtonClickListener;
import rjsv.floatingmenu.floatingmenubutton.listeners.FloatingMenuStateChangeListener;
import rjsv.floatingmenu.floatingmenubutton.listeners.SubButtonViewQueueListener;
import rjsv.floatingmenu.floatingmenubutton.subbutton.FloatingSubButton;
import rjsv.floatingmenu.floatingmenubutton.subbutton.SubButton;


/**
 * Description
 *
 * @author <a href="mailto:ricardo.vieira@xpand-it.com">RJSV</a>
 * @version $Revision : 1 $
 */

public class FloatingMenuButton extends FrameLayout implements View.OnTouchListener {

    // General Variables
    private static final String TAG = FloatingMenuButton.class.getName();
    private static final int clickThreshold = 15;
    private static final int INVALID_POINTER_ID = -1;
    private int startAngle = 0, endAngle = 180;
    private int preservedStartAngle = 0, preservedEndAngle = 180;
    private int radius;
    private boolean isMenuOpened = false;
    private Context context;
    private List<SubButton> subMenuButtons;
    private FloatingMenuAnimationHandler menuAnimationHandler;
    private FloatingMenuStateChangeListener stateChangeListener;
    private FloatingMenuButtonClickListener floatingMenuActionButtonClickListener;
    private AnimationType animationType;
    // private touch related
    private float startPositionX, startPositionY;
    private float currentPositionX, currentPositionY;
    private float aLastTouchX, aLastTouchY;
    private float screenWidth, screenHeight;
    private float viewWidth, viewHeight;
    private int mActivePointerId = INVALID_POINTER_ID;


    // Constructors
    public FloatingMenuButton(Context context) {
        this(context, null);
    }

    public FloatingMenuButton(Context context, AttributeSet attr) {
        this(context, attr, 0);
    }

    public FloatingMenuButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        subMenuButtons = new ArrayList<>();
        menuAnimationHandler = new FloatingMenuAnimationHandler(this);
        floatingMenuActionButtonClickListener = new FloatingMenuButtonClickListener();
        if (attrs != null) {
            final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FloatingMenuButton, 0, 0);
            this.animationType = AnimationType.match(a.getString(R.styleable.FloatingMenuButton_animationType));
            this.radius = a.getInt(R.styleable.FloatingMenuButton_subActionButtonRadius, 100);
            this.preservedStartAngle = a.getInt(R.styleable.FloatingMenuButton_dispositionStartAngle, startAngle);
            this.preservedEndAngle = a.getInt(R.styleable.FloatingMenuButton_dispositionEndAngle, endAngle);
            setDefaultImage(this);
            setStartAngle(this.preservedStartAngle, false);
            setEndAngle(this.preservedEndAngle, false);
            a.recycle();
        }
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleMenu();
            }
        });
        setOnTouchListener(this);
        if (menuAnimationHandler != null) {
            menuAnimationHandler.setMenu(FloatingMenuButton.this);
        }
    }


    // Overridden Methods
    @Override
    public boolean onTouch(View view, MotionEvent event) {
        try {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    mActivePointerId = event.getPointerId(0);
                    aLastTouchX = event.getX(mActivePointerId);
                    aLastTouchY = event.getY(mActivePointerId);
                    startPositionX = getX();
                    currentPositionX = startPositionX;
                    startPositionY = getY();
                    currentPositionY = startPositionY;
                    break;

                case MotionEvent.ACTION_UP:
                    if (Utils.isAClick(clickThreshold, startPositionX, getX(), startPositionY, getY())) {
                        floatingMenuActionButtonClickListener.onClick(FloatingMenuButton.this);
                    } else if (isMenuOpened) {
                        Point p = new Point();
                        p.x += (viewWidth / 2) + currentPositionX;
                        p.y += (viewHeight / 2) + currentPositionY;
                        reOpenMenu(p);
                    }
                    break;

                case MotionEvent.ACTION_MOVE:
                    final int pointerIndexMove = event.findPointerIndex(mActivePointerId);
                    // get the old coordinates
                    float oldPositionX = getX();
                    float oldPositionY = getY();
                    // calculate the new coordinates based on the finger's movement
                    currentPositionX = oldPositionX + event.getX(pointerIndexMove) - aLastTouchX;
                    currentPositionY = oldPositionY + event.getY(pointerIndexMove) - aLastTouchY;
                    // check if the difference between old and new coordinates violates the boundaries
                    boolean[] boundaries = isCentralViewOutsideBoundaries(oldPositionX, oldPositionY, currentPositionX, currentPositionY);
                    currentPositionX = boundaries[0] ? 0 : (boundaries[2] ? (screenWidth - viewWidth) : currentPositionX);
                    currentPositionY = boundaries[1] ? 0 : (boundaries[3] ? (screenHeight - viewHeight) : currentPositionY);
                    // set the coordinates
                    this.setX(currentPositionX);
                    this.setY(currentPositionY);
                    break;

                case MotionEvent.ACTION_CANCEL: {
                    mActivePointerId = INVALID_POINTER_ID;
                    break;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        invalidate();
        return true;
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        if (child instanceof FloatingSubButton) {
            if (params != null) {
                child.setLayoutParams(params);
            }
            SubButton button = new SubButton(child, 0, 0);
            setDefaultImage(button.getView());
            subMenuButtons.add(button);
            if (button.getWidth() == 0 || button.getHeight() == 0) {
                addViewToCurrentContainer(button.getView());
                button.setAlpha(0);
                button.getView().post(new SubButtonViewQueueListener(FloatingMenuButton.this, button));
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Point screen = Utils.getDisplayDimensions(getContext());
        screenWidth = screen.x;
        screenHeight = screen.y;
        viewWidth = getMeasuredWidth();
        viewHeight = getMeasuredHeight();
    }


    // General Methods
    @Override
    public void setOnClickListener(OnClickListener l) {
        floatingMenuActionButtonClickListener.addClickListener(l);
    }

    public void openMenu() {
        if (menuAnimationHandler != null && !menuAnimationHandler.isAnimating()) {
            Pair<Integer, Integer> angles = calculateDispositionAngles();
            Point center = calculateItemPositions(angles.first, angles.second);
            for (int i = 0; i < subMenuButtons.size(); i++) {
                SubButton currentSubButton = subMenuButtons.get(i);
                if (subMenuButtons.get(i).getView().getParent() == null) {
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(currentSubButton.getWidth(), currentSubButton.getHeight(), Gravity.TOP | Gravity.START);
                    params.setMargins(center.x - currentSubButton.getWidth() / 2, center.y - currentSubButton.getHeight() / 2, 0, 0);
                    addViewToCurrentContainer(currentSubButton.getView(), params);
                }
            }
            menuAnimationHandler.animateMenuOpening(center, animationType);
            isMenuOpened = true;
            if (stateChangeListener != null) {
                stateChangeListener.onMenuOpened(this);
            }
        }
    }

    public void closeMenu() {
        if (menuAnimationHandler != null && !menuAnimationHandler.isAnimating()) {
            menuAnimationHandler.animateMenuClosing(getActionViewCenter(), animationType);
            isMenuOpened = false;
            if (stateChangeListener != null) {
                stateChangeListener.onMenuClosed(this);
            }
        }
    }

    private void reOpenMenu(Point p) {
        if (menuAnimationHandler != null) {
            if (menuAnimationHandler.isAnimating()) {
                menuAnimationHandler.cancelMenuAnimations();
            }
            menuAnimationHandler.animateMenuReOpening(p);
        }
        isMenuOpened = false;
    }

    public void toggleMenu() {
        if (isMenuOpened) {
            closeMenu();
        } else {
            openMenu();
        }
    }

    public boolean isMenuOpen() {
        return isMenuOpened;
    }

    private void setDefaultImage(View v) {
        if (v != null && v.getBackground() == null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                v.setBackground(context.getResources().getDrawable(R.drawable.defaultimage, context.getTheme()));
            } else {
                v.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.defaultimage));
            }
        }
    }

    private void addViewToCurrentContainer(View view) {
        addViewToCurrentContainer(view, null);
    }

    private void addViewToCurrentContainer(View view, ViewGroup.LayoutParams layoutParams) {
        try {
            if (layoutParams != null) {
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) layoutParams;
                ViewGroup vg = (ViewGroup) Utils.getActivityContentView(context);
                removeViewFromCurrentContainer(view);
                if (vg != null) {
                    vg.addView(view, lp);
                }
            } else {
                ViewGroup vg = (ViewGroup) Utils.getActivityContentView(context);
                if (vg != null) {
                    vg.addView(view);
                }
            }
        } catch (ClassCastException e) {
            Log.e(FloatingMenuButton.class.getName(), e.getMessage());
        }
    }

    public void removeViewFromCurrentContainer(View view) {
        ViewGroup vg = ((ViewGroup) Utils.getActivityContentView(context));
        if (vg != null) {
            vg.removeView(view);
        }
    }


    // Utils
    public Point getActionViewCenter() {
        Point point = getActionViewCoordinates();
        point.x += viewWidth / 2;
        point.y += viewHeight / 2;
        return point;
    }

    private Point getActionViewCoordinates() {
        int[] coordinates = new int[2];
        // This method returns a x and y values that can be larger than the dimensions of the device screen.
        getLocationOnScreen(coordinates);
        // We then need to deduce the offsets
        Rect activityFrame = new Rect();
        View v = Utils.getActivityContentView(context);
        if (v != null) {
            v.getWindowVisibleDisplayFrame(activityFrame);
            coordinates[0] -= (Utils.getScreenSize(context).x - v.getMeasuredWidth());
            coordinates[1] -= (activityFrame.height() + activityFrame.top - v.getMeasuredHeight());
        }
        return new Point(coordinates[0], coordinates[1]);
    }

    private Point calculateItemPositions(Integer startAngle, Integer endAngle) {
        final Point center = getActionViewCenter();
        RectF area = new RectF(center.x - radius, center.y - radius, center.x + radius, center.y + radius);
        Path orbit = new Path();
        orbit.addArc(area, startAngle, endAngle - startAngle);
        PathMeasure measure = new PathMeasure(orbit, false);
        // Prevent overlapping when it is a full circle
        int divisor;
        if (Math.abs(endAngle - startAngle) >= 360 || subMenuButtons.size() <= 1) {
            divisor = subMenuButtons.size();
        } else {
            divisor = subMenuButtons.size() - 1;
        }
        // Measure the path in order to find points that have the same distance between each other
        for (int i = 0; i < subMenuButtons.size(); i++) {
            SubButton currentSubButton = subMenuButtons.get(i);
            float[] coordinates = new float[]{0f, 0f};
            int factor = animationType == AnimationType.RADIAL ? 0 : i;
            measure.getPosTan(factor * measure.getLength() / divisor, coordinates, null);
            currentSubButton.setX((int) coordinates[0] - currentSubButton.getWidth() / 2);
            currentSubButton.setY((int) coordinates[1] - currentSubButton.getHeight() / 2);
        }
        return center;
    }

    /**
     * Returns a boolean array (left, top, right, bottom) that indicates whether the closed central view touches its sides
     */
    private boolean[] isCentralViewOutsideBoundaries(float oldPositionX, float oldPositionY, float newPositionX, float newPositionY) {
        boolean[] results = new boolean[4];
        results[0] = newPositionX <= 0 && newPositionX <= oldPositionX; // left
        results[2] = (newPositionX + getMeasuredWidth()) >= screenWidth && newPositionX >= oldPositionX; // right
        results[1] = newPositionY <= 0 && newPositionY <= oldPositionY; // top
        results[3] = (newPositionY + getMeasuredHeight()) >= screenHeight && newPositionY >= oldPositionY; // bottom
        return results;
    }

    /**
     * Returns a boolean array (left, top, right, bottom) that indicates whether the opened menu view touches its sides
     */
    private boolean[] isGlobalViewOutsideBoundaries() {
        boolean[] results = new boolean[4];
        // 1 - get the largest width/height of the children
        int largestWidth = 0, largestHeight = 0;
        for (SubButton button : subMenuButtons) {
            if (button.getWidth() >= largestWidth) {
                largestWidth = button.getWidth();
            }
            if (button.getHeight() >= largestHeight) {
                largestHeight = button.getHeight();
            }
        }
        // 2 - add radius to the width to calculate the wides
        float realRadius = radius + (largestWidth >= largestHeight ? largestWidth : largestHeight) / 2;
        // 3 - get the center
        Point center = getActionViewCenter();
        results[0] = center.x - realRadius <= 0; // left
        results[2] = center.x + realRadius >= screenWidth; // right
        results[1] = center.y - realRadius <= 0; // top
        results[3] = center.y + realRadius >= screenHeight; // bottom
        return results;
    }

    private Pair<Integer, Integer> calculateDispositionAngles() {
        int defaultStartAngle = this.preservedStartAngle, defaultEndAngle = this.preservedEndAngle;
        boolean[] boundariesTouched = isGlobalViewOutsideBoundaries();
        boolean topLeftCorner = boundariesTouched[0] && boundariesTouched[1];
        boolean topRightCorner = boundariesTouched[1] && boundariesTouched[2];
        boolean bottomRightCorner = boundariesTouched[2] && boundariesTouched[3];
        boolean bottomLeftCorner = boundariesTouched[3] && boundariesTouched[0];
        boolean touchesAnyCorner = topLeftCorner || topRightCorner || bottomRightCorner || bottomLeftCorner;
        // 1 - else, check if touches any wall/boundary
        if (!touchesAnyCorner) {
            if (boundariesTouched[0]) {
                defaultStartAngle = 270;
                defaultEndAngle = 450;
            } else if (boundariesTouched[1]) {
                defaultStartAngle = 180;
                defaultEndAngle = 360;
            } else if (boundariesTouched[2]) {
                defaultStartAngle = 90;
                defaultEndAngle = 270;
            } else if (boundariesTouched[3]) {
                defaultStartAngle = 0;
                defaultEndAngle = 180;
            }
        } else {
            // 2 - check if it touched any corners
            if (topLeftCorner) {
                defaultStartAngle = 270;
                defaultEndAngle = 360;
            } else if (topRightCorner) {
                defaultStartAngle = 180;
                defaultEndAngle = 270;
            } else if (bottomRightCorner) {
                defaultStartAngle = 90;
                defaultEndAngle = 180;
            } else /*if (bottomLeftCorner) */ {
                defaultStartAngle = 0;
                defaultEndAngle = 90;
            }
        }
        setStartAngle(defaultStartAngle, false);
        setEndAngle(defaultEndAngle, false);
        return new Pair<>(this.startAngle, this.endAngle);
    }


    // FloatingMenuStateChangeListener interface
    public void setStateChangeListener(FloatingMenuStateChangeListener stateChangeListener) {
        this.stateChangeListener = stateChangeListener;
    }


    // Setters and Getters
    public int getStartAngle() {
        return startAngle;
    }

    public FloatingMenuButton setStartAngle(int startAngle) {
        setStartAngle(startAngle, true);
        return this;
    }

    private void setStartAngle(int startAngle, boolean overridePreservedAngles) {
        if (overridePreservedAngles) {
            this.preservedStartAngle = startAngle;
        }
        this.startAngle = 360 - startAngle;
    }

    public int getEndAngle() {
        return endAngle;
    }

    public FloatingMenuButton setEndAngle(int endAngle) {
        setEndAngle(endAngle, true);
        return this;
    }

    private void setEndAngle(int endAngle, boolean overridePreservedAngles) {
        if (overridePreservedAngles) {
            this.preservedEndAngle = endAngle;
        }
        this.endAngle = 360 - endAngle;
    }

    public int getRadius() {
        return radius;
    }

    public FloatingMenuButton setRadius(int radius) {
        this.radius = radius;
        return this;
    }

    public AnimationType getAnimationType() {
        return this.animationType;
    }

    public FloatingMenuButton setAnimationType(AnimationType animationType) {
        if (animationType != null) {
            this.animationType = animationType;
        }
        return this;
    }

    public FloatingMenuAnimationHandler getAnimationHandler() {
        return this.menuAnimationHandler;
    }

    public List<SubButton> getSubMenuButtons() {
        return subMenuButtons;
    }

}