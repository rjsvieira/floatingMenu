package com.rjsvieira.floatingmenu.sample;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.FastOutSlowInInterpolator;

import rjsv.floatingmenu.animation.enumerators.AnimationType;
import rjsv.floatingmenu.floatingmenubutton.FloatingMenuButton;
import rjsv.floatingmenu.floatingmenubutton.MovementStyle;

public class MainActivity extends Activity {

    FloatingMenuButton fab_1;
    FloatingMenuButton fab_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUi();
    }

    private void initUi() {
        fab_1 = (FloatingMenuButton) findViewById(R.id.fab_1);
        fab_1.setStartAngle(0)
                .setEndAngle(360)
                .setRadius(200)
                .setAnimationType(AnimationType.EXPAND)
                .setMovementStyle(MovementStyle.STICKED_TO_SIDES);

        fab_1.getAnimationHandler()
                .setOpeningAnimationDuration(500)
                .setClosingAnimationDuration(200)
                .setLagBetweenItems(0)
                .setOpeningInterpolator(new FastOutSlowInInterpolator())
                .setClosingInterpolator(new FastOutLinearInInterpolator())
                .shouldFade(true)
                .shouldScale(true)
                .shouldRotate(false);

        fab_2 = (FloatingMenuButton) findViewById(R.id.fab_2);
        fab_2.setStartAngle(0)
                .setEndAngle(360)
                .setAnimationType(AnimationType.RADIAL)
                .setMovementStyle(MovementStyle.ANCHORED);

        fab_2.getAnimationHandler()
                .setOpeningAnimationDuration(500)
                .setClosingAnimationDuration(200)
                .setLagBetweenItems(0)
                .setOpeningInterpolator(new FastOutSlowInInterpolator())
                .setClosingInterpolator(new FastOutLinearInInterpolator())
                .shouldFade(true)
                .shouldScale(true)
                .shouldRotate(false);

    }
}
