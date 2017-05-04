package rjsv.floatingmenu.animation.handlers;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.graphics.Point;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rjsv.floatingmenu.animation.enumerators.AnimationType;
import rjsv.floatingmenu.animation.enumerators.MenuState;
import rjsv.floatingmenu.animation.listeners.FloatingMenuButtonAnimationListener;
import rjsv.floatingmenu.floatingmenubutton.FloatingMenuButton;
import rjsv.floatingmenu.floatingmenubutton.subbutton.SubButton;

/**
 * Description
 *
 * @author <a href="mailto:ricardo.vieira@xpand-it.com">RJSV</a>
 * @version $Revision : 1 $
 */

/**
 * An example animation handler
 * Animates translation, rotation, scale and alpha at the same time using Property Animation APIs.
 */
public class FloatingMenuAnimationHandler extends AnimationHandler implements Animator.AnimatorListener {

    private int openingDuration = 500;
    private int closingDuration = 500;
    private int lagBetweenItems = 100;
    private boolean animating;
    private FloatingMenuButtonAnimationListener menuButtonAnimationListener;
    private Interpolator openingInterpolator;
    private Interpolator closingInterpolator;
    private boolean shouldRotate = true;
    private boolean shouldFade = true;
    private boolean shouldScale = true;
    private FloatingMenuButton floatingMenuButton;
    private MenuState currentState;
    private AnimatorSet openingAnimation;
    private AnimatorSet closingAnimation;

    // Constructors
    public FloatingMenuAnimationHandler(FloatingMenuButton floatingMenuButton) {
        setAnimating(false);
        this.floatingMenuButton = floatingMenuButton;
        this.openingInterpolator = new OvershootInterpolator(0.9f);
        this.closingInterpolator = new AccelerateDecelerateInterpolator();
    }

    // Overridden Methods
    @Override
    public void animateMenuOpening(Point center, AnimationType animationType) {
        super.animateMenuOpening(center, animationType);
        currentState = animationType == AnimationType.RADIAL ? MenuState.OPENING_RADIAL : MenuState.OPENING;
        openingAnimation = currentState == MenuState.OPENING_RADIAL ? openRadialMenuAnimation(center) : openMenuAnimation(center);
        openingAnimation.start();
    }

    @Override
    public void animateMenuClosing(Point center, AnimationType animationType) {
        super.animateMenuClosing(center, animationType);
        currentState = animationType == AnimationType.RADIAL ? MenuState.CLOSING_RADIAL : MenuState.CLOSING;
        closingAnimation = currentState == MenuState.CLOSING_RADIAL ? closeRadialMenuAnimation(center) : closeMenuAnimation(center);
        closingAnimation.start();
    }

    @Override
    public void animateMenuReOpening(Point center) {
        super.animateMenuReOpening(center);
        animateMenuClosing(center, AnimationType.EXPAND);
        currentState = MenuState.REOPENING;
    }

    @Override
    public boolean isAnimating() {
        return animating;
    }

    @Override
    public void setAnimating(boolean animating) {
        this.animating = animating;
        if (!this.animating && currentState != MenuState.REOPENING) {
            currentState = MenuState.IDLE;
        }
    }

    // Expand animation
    private AnimatorSet openMenuAnimation(Point center) {
        setAnimating(true);
        Animator lastAnimation = null;
        List<SubButton> subActionItems = menu.getSubMenuButtons();
        ArrayList<Animator> animatorArrayList = new ArrayList<>();
        for (int i = 0; i < subActionItems.size(); i++) {
            SubButton currentSubButton = subActionItems.get(i);
            ArrayList<PropertyValuesHolder> properties = new ArrayList<>();
            properties.add(PropertyValuesHolder.ofFloat(View.TRANSLATION_X, currentSubButton.getX() - center.x + currentSubButton.getWidth() / 2));
            properties.add(PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, currentSubButton.getY() - center.y + currentSubButton.getHeight() / 2));
            if (shouldRotate) {
                properties.add(PropertyValuesHolder.ofFloat(View.ROTATION, 720));
            }
            if (shouldScale) {
                properties.add(PropertyValuesHolder.ofFloat(View.SCALE_X, 1));
                properties.add(PropertyValuesHolder.ofFloat(View.SCALE_Y, 1));
            }
            if (shouldFade) {
                properties.add(PropertyValuesHolder.ofFloat(View.ALPHA, 1));
            }
            PropertyValuesHolder[] parameters = new PropertyValuesHolder[properties.size() - 1];
            parameters = properties.toArray(parameters);
            final ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(currentSubButton.getView(), parameters);
            animation.setDuration(openingDuration);
            animation.setInterpolator(openingInterpolator);
            menuButtonAnimationListener = new FloatingMenuButtonAnimationListener(FloatingMenuAnimationHandler.this, currentSubButton, MenuState.OPENING);
            animation.addListener(menuButtonAnimationListener);
            if (i == 0) {
                lastAnimation = animation;
            }
            animation.setStartDelay((subActionItems.size() - i) * lagBetweenItems);
            animatorArrayList.add(animation);
        }
        if (lastAnimation != null) {
            lastAnimation.addListener(this);
        }
        AnimatorSet openAnimatorSet = new AnimatorSet();
        openAnimatorSet.playTogether(animatorArrayList);
        return openAnimatorSet;
    }

    private AnimatorSet closeMenuAnimation(Point center) {
        setAnimating(true);
        Animator lastAnimation = null;
        ArrayList<Animator> animatorArrayList = new ArrayList<>();
        // We reverse the list to have the animation was intended
        ArrayList<SubButton> reverseSubActionItems = new ArrayList<>(menu.getSubMenuButtons());
        Collections.reverse(reverseSubActionItems);
        for (int i = 0; i < reverseSubActionItems.size(); i++) {
            SubButton currentSubButton = reverseSubActionItems.get(i);
            ArrayList<PropertyValuesHolder> properties = new ArrayList<>();
            properties.add(PropertyValuesHolder.ofFloat(View.TRANSLATION_X, -(currentSubButton.getX() - center.x + currentSubButton.getWidth() / 2)));
            properties.add(PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, -(currentSubButton.getY() - center.y + currentSubButton.getHeight() / 2)));
            if (shouldRotate) {
                properties.add(PropertyValuesHolder.ofFloat(View.ROTATION, -720));
            }
            if (shouldScale) {
                properties.add(PropertyValuesHolder.ofFloat(View.SCALE_X, 0));
                properties.add(PropertyValuesHolder.ofFloat(View.SCALE_Y, 0));
            }
            if (shouldFade) {
                properties.add(PropertyValuesHolder.ofFloat(View.ALPHA, 0));
            }
            PropertyValuesHolder[] parameters = new PropertyValuesHolder[properties.size() - 1];
            parameters = properties.toArray(parameters);
            ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(currentSubButton.getView(), parameters);
            animation.setDuration(closingDuration);
            animation.setInterpolator(closingInterpolator);
            menuButtonAnimationListener = new FloatingMenuButtonAnimationListener(FloatingMenuAnimationHandler.this, currentSubButton, MenuState.CLOSING);
            animation.addListener(menuButtonAnimationListener);
            if (i == 0) {
                lastAnimation = animation;
            }
            animation.setStartDelay((reverseSubActionItems.size() - i) * lagBetweenItems);
            animatorArrayList.add(animation);
        }
        if (lastAnimation != null) {
            lastAnimation.addListener(this);
        }
        AnimatorSet closeAnimatorSet = new AnimatorSet();
        closeAnimatorSet.playTogether(animatorArrayList);
        return closeAnimatorSet;
    }

    // Radial Animation
    private AnimatorSet openRadialMenuAnimation(Point center) {
        setAnimating(true);
        Animator lastAnimation = null;
        int startAngle = floatingMenuButton.getStartAngle();
        int endAngle = floatingMenuButton.getEndAngle();
        int radius = floatingMenuButton.getRadius();
        List<SubButton> subActionItems = menu.getSubMenuButtons();
        ArrayList<Animator> animatorArrayList = new ArrayList<>();
        for (int i = 0; i < subActionItems.size(); i++) {
            final SubButton currentSubButton = subActionItems.get(i);
            final View currentSubButtonView = currentSubButton.getView();
            // reset the sub button's properties
            resetSubButton(center, currentSubButton, currentSubButtonView);
            // Button Properties along the animation
            ArrayList<PropertyValuesHolder> properties = new ArrayList<>();
            properties.add(PropertyValuesHolder.ofFloat(View.ROTATION, 720));
            properties.add(PropertyValuesHolder.ofFloat(View.SCALE_X, 1));
            properties.add(PropertyValuesHolder.ofFloat(View.SCALE_Y, 1));
            properties.add(PropertyValuesHolder.ofFloat(View.ALPHA, 1));
            PropertyValuesHolder[] parameters = new PropertyValuesHolder[properties.size() - 1];
            parameters = properties.toArray(parameters);
            ObjectAnimator propertiesAnimation = ObjectAnimator.ofPropertyValuesHolder(currentSubButtonView, parameters);
            // Button Position along the animation
            final ArrayList<Point> radialPointsPorCurrentPosition = getArcPointsForButton(center, startAngle, endAngle, subActionItems.size(), i, radius);
            ValueAnimator positionAnimation = ValueAnimator.ofFloat(0, radialPointsPorCurrentPosition.size());
            positionAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    Float value = ((Float) animation.getAnimatedValue());
                    if (value.intValue() < radialPointsPorCurrentPosition.size()) {
                        Point p = radialPointsPorCurrentPosition.get(value.intValue());
                        currentSubButtonView.setX(p.x);
                        currentSubButtonView.setY(p.y);
                        currentSubButton.setX(p.x);
                        currentSubButton.setY(p.y);
                    }
                }
            });
            positionAnimation.setDuration(openingDuration);
            positionAnimation.setInterpolator(openingInterpolator);
            menuButtonAnimationListener = new FloatingMenuButtonAnimationListener(FloatingMenuAnimationHandler.this, currentSubButton, MenuState.OPENING_RADIAL);
            positionAnimation.addListener(menuButtonAnimationListener);
            if (i == 0) {
                lastAnimation = positionAnimation;
            }
            animatorArrayList.add(propertiesAnimation);
            animatorArrayList.add(positionAnimation);
        }
        if (lastAnimation != null) {
            lastAnimation.addListener(this);
        }
        AnimatorSet openRadialAnimatorSet = new AnimatorSet();
        openRadialAnimatorSet.playTogether(animatorArrayList);
        return openRadialAnimatorSet;
    }

    private AnimatorSet closeRadialMenuAnimation(Point center) {
        setAnimating(true);
        Animator lastAnimation = null;
        int startAngle = floatingMenuButton.getStartAngle();
        int endAngle = floatingMenuButton.getEndAngle();
        int radius = floatingMenuButton.getRadius();
        List<SubButton> subActionItems = menu.getSubMenuButtons();
        ArrayList<Animator> animatorArrayList = new ArrayList<>();
        for (int i = 0; i < subActionItems.size(); i++) {
            final SubButton currentSubButton = subActionItems.get(i);
            final View currentSubButtonView = currentSubButton.getView();
            // Button Properties along the animation
            ArrayList<PropertyValuesHolder> properties = new ArrayList<>();
            properties.add(PropertyValuesHolder.ofFloat(View.SCALE_X, (float) 0.75));
            properties.add(PropertyValuesHolder.ofFloat(View.SCALE_Y, (float) 0.75));
            PropertyValuesHolder[] parameters = new PropertyValuesHolder[properties.size() - 1];
            parameters = properties.toArray(parameters);
            ObjectAnimator propertiesAnimation = ObjectAnimator.ofPropertyValuesHolder(currentSubButtonView, parameters);
            // Button Position along the animation
            final ArrayList<Point> radialPointsPorCurrentPosition = getArcPointsForButton(center, startAngle, endAngle, subActionItems.size(), i, radius);
            Collections.reverse(radialPointsPorCurrentPosition);
            ValueAnimator positionAnimation = ValueAnimator.ofFloat(0, radialPointsPorCurrentPosition.size());
            positionAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    Float value = ((Float) animation.getAnimatedValue());
                    if (value.intValue() < radialPointsPorCurrentPosition.size()) {
                        Point p = radialPointsPorCurrentPosition.get(value.intValue());
                        currentSubButtonView.setX(p.x);
                        currentSubButtonView.setY(p.y);
                        currentSubButton.setX(p.x);
                        currentSubButton.setY(p.y);
                    }
                }
            });
            positionAnimation.setDuration(closingDuration);
            positionAnimation.setInterpolator(closingInterpolator);
            menuButtonAnimationListener = new FloatingMenuButtonAnimationListener(FloatingMenuAnimationHandler.this, currentSubButton, MenuState.CLOSING_RADIAL);
            positionAnimation.addListener(menuButtonAnimationListener);
            if (i == 0) {
                lastAnimation = positionAnimation;
            }
            animatorArrayList.add(propertiesAnimation);
            animatorArrayList.add(positionAnimation);
        }
        if (lastAnimation != null) {
            lastAnimation.addListener(this);
        }
        AnimatorSet closeRadialAnimatorSet = new AnimatorSet();
        closeRadialAnimatorSet.playTogether(animatorArrayList);
        return closeRadialAnimatorSet;
    }

    public void cancelMenuAnimations() {
        if (openingAnimation != null) {
            openingAnimation.cancel();
            openingAnimation = null;
        }
        if (closingAnimation != null) {
            closingAnimation.cancel();
            closingAnimation = null;
        }
    }


    // Configuration Methods
    public FloatingMenuAnimationHandler setOpeningInterpolator(Interpolator interpolator) {
        this.openingInterpolator = interpolator;
        return this;
    }

    public FloatingMenuAnimationHandler setClosingInterpolator(Interpolator interpolator) {
        this.closingInterpolator = interpolator;
        return this;
    }

    public FloatingMenuAnimationHandler setOpeningAnimationDuration(int openingDuration) {
        this.openingDuration = openingDuration;
        return this;
    }

    public FloatingMenuAnimationHandler setClosingAnimationDuration(int closingDuration) {
        this.closingDuration = closingDuration;
        return this;
    }

    public FloatingMenuAnimationHandler setLagBetweenItems(int duration) {
        this.lagBetweenItems = duration;
        return this;
    }

    public FloatingMenuAnimationHandler shouldRotate(boolean value) {
        this.shouldRotate = value;
        return this;
    }

    public FloatingMenuAnimationHandler shouldFade(boolean value) {
        this.shouldFade = value;
        return this;
    }

    public FloatingMenuAnimationHandler shouldScale(boolean value) {
        this.shouldScale = value;
        return this;
    }


    // Animator Interface Listener
    @Override
    public void onAnimationStart(Animator animation) {
        setAnimating(true);
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        setAnimating(false);
        if (MenuState.REOPENING == currentState) {
            cancelMenuAnimations();
            floatingMenuButton.openMenu();
        }
    }

    @Override
    public void onAnimationCancel(Animator animation) {
        setAnimating(false);
    }

    @Override
    public void onAnimationRepeat(Animator animation) {
        setAnimating(true);
    }

    // General Methods
    private void resetSubButton(Point center, SubButton subButton, View subButtonView) {
        if (subButton != null) {
            int floatingMenuButtonWidth = floatingMenuButton.getWidth();
            int floatingMenuButtonHeight = floatingMenuButton.getHeight();
            int xResetPos = center.x + floatingMenuButtonWidth / 2;
            int yResetPos = center.y - floatingMenuButtonHeight / 2;
            subButtonView.setX(xResetPos);
            subButtonView.setY(yResetPos);
            subButtonView.setScaleX(0);
            subButtonView.setScaleY(0);
            subButton.setX(xResetPos);
            subButton.setY(yResetPos);
            subButton.setAlpha(0);
        }
    }

    private ArrayList<Point> getArcPointsForButton(Point center, int minAngle, int maxAngle, int numberOfButtons, int buttonIndex, int radius) {
        ArrayList<Point> points = new ArrayList<>();
        // Counter the clockwise default for the angles
        int startAngle = 360 - minAngle;
        int endAngle = 360 - maxAngle;
        double radDegree = Math.toRadians(startAngle);
        if (!(Math.abs(endAngle - startAngle) >= 360 || numberOfButtons <= 1)) {
            numberOfButtons = numberOfButtons - 1;
        }
        SubButton currentButton = menu.getSubMenuButtons().get(buttonIndex);
        int currentButtonCenterX = currentButton.getView().getWidth() / 2;
        int currentButtonCenterY = currentButton.getView().getHeight() / 2;
        int angleDistributionValue = Math.abs(endAngle - startAngle) / numberOfButtons;
        for (int j = 0; j < radius; j++) {
            Point p = new Point();
            p.x = center.x + (int) (j * Math.cos(radDegree)) - currentButtonCenterX;
            p.y = center.y - (int) (j * Math.sin(radDegree)) - currentButtonCenterY;
            points.add(p);
        }
        for (int i = startAngle; i <= startAngle + buttonIndex * angleDistributionValue; i++) {
            Point p = new Point();
            radDegree = Math.toRadians(i);
            p.x = center.x + (int) (radius * Math.cos(radDegree)) - currentButtonCenterX;
            p.y = center.y - (int) (radius * Math.sin(radDegree)) - currentButtonCenterY;
            points.add(p);
        }
        return points;
    }

}


