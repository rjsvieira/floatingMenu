# FloatingMenuButton

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-FloatingMenu-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/5697)
![Current Version](https://img.shields.io/badge/Current%20Version-1.1.0-brightgreen.svg)
[![](https://jitpack.io/v/rjsvieira/floatingMenu.svg)](https://jitpack.io/#rjsvieira/floatingMenu)
![Minimum SDK](https://img.shields.io/badge/minSdkVersion%20-15-blue.svg)

<img src="images/banner.png">

<kbd><img src="https://github.com/rjsvieira/FloatingMenu/blob/master/images/radial_open_close.gif" width="24%" height="200px">  <img src="https://github.com/rjsvieira/FloatingMenu/blob/master/images/radial_adapt.gif" width="24%" height="200px">  <img src="https://github.com/rjsvieira/FloatingMenu/blob/master/images/expand_open_close.gif" width="24%" height="200px">  <img src="https://github.com/rjsvieira/FloatingMenu/blob/master/images/expand_adapt.gif" width="25%" height="200px"></kbd>


<h2>Include in your project</h2>

<h4> In your root/build.gradle</h4>

```groovy
allprojects {
  repositories {
  ...
  maven { url 'https://jitpack.io' }
  }
}  
```

<h4> In your app/build.gradle</h4>

```groovy
dependencies {
  compile 'com.github.rjsvieira:FloatingMenuButton:1.1.0'
}
```


<h2>Customization</h2>

The FloatingMenuButton central image allows the user to give it any appearance he desires.<br>
The user can also specify a ClickListener and add it to it. Thanks to the FloatingMenuButton's composite clickListener, it <b>will not</b> override the animation toggling.<br><br>
The FloatingMenuButton <b>only accepts FloatingSubButton children.</b> <br>
Like the FloatingMenuButton, the FloatingSubButton can be configured to have a specific background and a ClickListener for interaction.
The following XML file specifies the example on the radial gifs : 
<br>
<br>

```xml
<rjsv.floatingmenu.floatingmenubutton.FloatingMenuButton
  android:id="@+id/my_floating_button"
  android:layout_width="40dp"
  android:layout_height="40dp"
  android:scaleType="centerInside"
  app:layout_constraintBottom_toBottomOf="parent"
  app:layout_constraintLeft_toLeftOf="parent"
  app:layout_constraintRight_toRightOf="parent"
  app:layout_constraintTop_toTopOf="parent"
  floatingMenuActionButton:anchored="false"
  floatingMenuActionButton:animationType="radial"
  floatingMenuActionButton:dispositionEndAngle="360"
  floatingMenuActionButton:dispositionStartAngle="0"
  floatingMenuActionButton:subActionButtonRadius="100">

  <rjsv.floatingmenu.floatingmenubutton.subbutton.FloatingSubButton
    android:id="@+id/sub_button_1"
    android:layout_width="50dp"
    android:layout_height="50dp"
    android:background="@drawable/one" />

  <rjsv.floatingmenu.floatingmenubutton.subbutton.FloatingSubButton
    android:id="@+id/sub_button_2"
    android:layout_width="50dp"
    android:layout_height="50dp"
    android:background="@drawable/two" />

  <rjsv.floatingmenu.floatingmenubutton.subbutton.FloatingSubButton
    android:id="@+id/sub_button_3"
    android:layout_width="50dp"
    android:layout_height="50dp"
    android:background="@drawable/three" />

  <rjsv.floatingmenu.floatingmenubutton.subbutton.FloatingSubButton
    android:id="@+id/sub_button_4"
    android:layout_width="50dp"
    android:layout_height="50dp"
    android:background="@drawable/four" />

  <rjsv.floatingmenu.floatingmenubutton.subbutton.FloatingSubButton
    android:id="@+id/sub_button_5"
    android:layout_width="50dp"
    android:layout_height="50dp"
    android:background="@drawable/five" />

</rjsv.floatingmenu.floatingmenubutton.FloatingMenuButton>
 ```
<h2>Attribute configuration list</h2>

| Attribute        | Type           | Default  | Default |
| ------------- |:-------------:| :-----| :------ |
| startAngle           | int | The starting angle for button disposition | 0 |
| endAngle             | int | The ending angle for button disposition   | 180 |
| radius               | int | The distance between the central button and its children | 100(dp) |
| anchored             | boolean | Configures whether the user can or not drag the FloatingMenu around | false |
| animationType        | AnimationType (Enumerator) | The open/close animation for FloatingMenuButton | AnimationType.EXPAND |
| openingDuration      | int | The opening duration, in milliseconds, of the animation | 500 |
| closingDuration      | int | The closing duration, in milliseconds, of the animation | 500 |
| lagBetweenItems      | int | The lag duration between animating items. Affects only AnimationType.EXPAND | 100 |
| openingInterpolator  | Interpolator | The opening interpolator. Allows different rythms on the animations| OvershootInterpolator |
| closingInterpolator  | Interpolator | The closing interpolator. Allows different rythms on the animations | AccelerateDecelerateInterpolator |
| shouldRotate         | boolean | Specify whether the items should rotate while animating. Available only in AnimationType.EXPAND | true |
| shouldFade           | boolean | Specify whether the items should fade while animating. Available only in AnimationType.EXPAND | true |
| shouldScale          | boolean | Specify whether the items should scale while animating. Available only in AnimationType.EXPAND | true |

<h2>FloatingMenuButton State Change Listener</h2>

You can track whether the FloatingMenuButton is in open or closed status

```java
public interface FloatingMenuButtonStateChangeListener {

    void onMenuOpened(FloatingMenuButton menu);

    void onMenuClosed(FloatingMenuButton menu);

}
```


<h2>FloatingMenuButton Animation Handler (Wrapper)</h2>

You can configure the FloatingMenuButton programmatically rather than xml, specifying and settings its variables.
Consider the following example :

```java
floatingButton = (FloatingMenuButton) findViewById(R.id.my_floating_button);
floatingButton.setStartAngle(0)
        .setEndAngle(360)
        .setAnimationType(AnimationType.EXPAND)
        .setAnchored(false);
floatingButton.getAnimationHandler()
        .setOpeningAnimationDuration(500)
        .setClosingAnimationDuration(200)
        .setLagBetweenItems(0)
        .setOpeningInterpolator(new FastOutSlowInInterpolator())
        .setClosingInterpolator(new FastOutLinearInInterpolator())
        .shouldFade(true)
        .shouldScale(true)
        .shouldRotate(false)
;
```
