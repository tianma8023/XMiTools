## 基础 
```shell
adb shell pm list packages -f | grep ${package_name}
```
通过上述命令，可以找到对应包名的app在手机中的存储位置。
XMiTools中有两个关键app：
1. SystemUI - 包名(com.android.systemui) - 位置: /system_ext/priv-app/MiuiSystemUI/MiuiSystemUI.apk
2. miuisystem - 包名(com.miui.system) - 位置：/system/app/miuisystem/miuisystem.apk

## MIUI 12 - 12.4.30
锁屏界面对应的View为: NotificationPanelView；锁屏布局为 status_bar_expanded.xml

R.status_bar_expanded.xml:
```xml
<com.android.systemui.statusbar.phone.NotificationPanelView xmlns:android="http://schemas.android.com/apk/res/android" xmlns:app="http://schemas.android.com/apk/res-auto" xmlns:systemui="http://schemas.android.com/apk/res/com.android.systemui" android:id="@+id/notification_panel" android:background="@android:color/transparent" android:layout_width="@dimen/notification_panel_width" android:layout_height="match_parent">
    <ImageView android:id="@+id/wallpaper" android:visibility="gone" android:layout_width="match_parent" android:layout_height="match_parent" android:scaleType="centerCrop" android:importantForAccessibility="no"/>
    <include layout="@layout/notification_panel_window_bg"/>
    <ImageView android:id="@+id/left_view_bg" android:visibility="invisible" android:layout_width="match_parent" android:layout_height="match_parent" android:scaleType="centerCrop"/>
    <com.android.keyguard.clock.KeyguardClockContainer android:id="@+id/keyguard_clock_view" android:layout_width="match_parent" android:layout_height="wrap_content" android:importantForAccessibility="no"/>
    <include layout="@layout/miui_keyguard_lock_screen_magazine_pre_layout"/>
    <TextView android:textSize="16sp" android:textColor="#ffffff" android:layout_gravity="top|right|center_vertical|center_horizontal|center|end" android:id="@+id/switch_to_system_user" android:visibility="gone" android:layout_width="wrap_content" android:layout_height="wrap_content" android:layout_marginTop="40dp" android:text="@string/switch_system_user" android:drawableLeft="@drawable/logout_dark" android:drawablePadding="5dp" android:layout_marginEnd="23dp"/>
    <FrameLayout android:id="@+id/awesome_lock_screen_container" android:visibility="gone" android:layout_width="match_parent" android:layout_height="match_parent"/>
    <ImageView android:id="@+id/notch_corner" android:background="@drawable/screen_round_corner_notch" android:visibility="gone" android:layout_width="match_parent" android:layout_height="wrap_content"/>
    <include layout="@layout/miui_keyguard_face_unlock_view"/>
    <com.android.systemui.statusbar.phone.NotificationsQuickSettingsContainer android:layout_gravity="center_horizontal|clip_horizontal" android:id="@+id/notification_container_parent" android:focusable="true" android:focusableInTouchMode="true" android:clipChildren="false" android:clipToPadding="false" android:layout_width="match_parent" android:layout_height="match_parent" android:paddingStart="@dimen/panel_content_margin_horizontal" android:paddingEnd="@dimen/panel_content_margin_horizontal">
        <FrameLayout android:id="@+id/qs_frame" android:background="@android:color/transparent" android:focusable="true" android:focusableInTouchMode="true" android:nextFocusRight="@+id/notification_stack_scroller" android:nextFocusDown="@+id/notification_stack_scroller" android:layout="@layout/qs_panel" android:layout_width="match_parent" android:layout_height="match_parent" android:importantForAccessibility="no" android:accessibilityTraversalBefore="@+id/notification_stack_scroller" app:viewType="com.android.systemui.plugins.qs.QS"/>
        <com.android.systemui.statusbar.stack.NotificationStackScrollLayout android:layout_gravity="center_horizontal|clip_horizontal" android:id="@+id/notification_stack_scroller" android:clipToPadding="false" android:layout_width="match_parent" android:layout_height="match_parent" android:importantForAccessibility="no" android:elevation="2dp" android:accessibilityTraversalAfter="@+id/qs_frame"/>
        <ViewStub android:id="@+id/keyguard_user_switcher" android:layout="@layout/keyguard_user_switcher" android:layout_width="match_parent" android:layout_height="match_parent"/>
        <include android:visibility="invisible" layout="@layout/keyguard_status_bar"/>
        <Button android:id="@+id/report_rejected_touch" android:visibility="gone" android:layout_width="wrap_content" android:layout_height="wrap_content" android:layout_marginTop="@dimen/status_bar_header_height_keyguard" android:text="@string/report_rejected_touch"/>
    </com.android.systemui.statusbar.phone.NotificationsQuickSettingsContainer>
    <TextView android:textSize="@dimen/notification_panel_empty_text_size" android:textColor="@color/empty_shade_text_color" android:gravity="center" android:layout_gravity="center" android:id="@+id/no_notifications" android:visibility="gone" android:layout_width="match_parent" android:layout_height="wrap_content" android:text="@string/empty_shade_text"/>
    <include android:visibility="gone" layout="@layout/keyguard_bottom_area"/>
    <include android:id="@+id/keyguard_left_view" android:visibility="invisible" layout="@layout/miui_keyguard_left_view_container"/>
</com.android.systemui.statusbar.phone.NotificationPanelView>
```
其中包含了 KeyguardClockContainer

KeyguardClockContainer 是锁屏时钟的容器，里面持有了时钟对象 MiuiKeyguardBaseClockView

MiuiKeyguardBaseClock 有2个直接子类:
1. MiuiKeyguardSingleClock，有4个直接子类:
    1. MiuiKeyguardCenterHorizontalClock - 居中横向的时钟
    2. MiuiKeyguardCenterVerticalClock - 居中竖向的时钟
    3. MiuiKeyguardLeftTopClock - 左上角时钟
    4. MiuiKeyguardLeftTopLargeClock - 左上角大时钟
2. MiuiKeyguardDualClock: 锁屏双时钟

以上时钟都只是时钟的包裹类，具体实现类，实际上是 MiuiBaseClock 及其子类。

MiuiBaseClock 类所在位置 /system/app/miuisystem/miuisystem.apk
MiuiBaseClock 继承自 LinearLayout, MiuiBaseClock 有四个子类:
1. MiuiCenterHorizontalClock - 居中横向时钟
2. MiuiVerticalClock - 垂直时钟
3. MiuiLeftTopClock - 左上角时钟
4. MiuiLeftTopLargeClock - 左上角大时钟

MiuiDualClock 继承自 RelativeLayout ，表示双时钟

对应表里关系:
- MiuiKeyguardCenterHorizontalClock <-> MiuiCenterHorizontalClock
- MiuiKeyguardCenterVerticalClock <-> MiuiVerticalClock
- MiuiKeyguardLeftTopClock <-> MiuiLeftTopClock
- MiuiKeyguardLeftTopLargeClock <-> MiuiLeftTopLargeClock
- MiuiKeyguardDualClock <-> MiuiDualClock

## MIUI 12.5 21.1.28 Android 11, SystemUI(versionCode=202011090)
### 状态栏
StatusBar#inflateStatusBarWindow():
```java
class StatusBar {
    this.mPhoneStatusBarWindow = this.mSuperStatusBarViewFactory.getStatusBarWindowView();
}
```

StatusBar#mPhoneStatusBarWindow 是 StatusBarWindowView, 其对应的layout布局是 R.layout.super_status_bar

super_status_bar.xml:
```xml
<com.android.systemui.statusbar.phone.StatusBarWindowView>
    <FrameLayout android:id="@+id/status_bar_container" 
        android:background="@drawable/system_bar_background" 
        android:layout_width="match_parent" 
        android:layout_height="wrap_content"/>
</com.android.systemui.statusbar.phone.StatusBarWindowView>
```

其中 status_bar_container 是 FrameLayout, 它里面包含一个 状态栏 Fragment - MiuiCollapsedStatusBarFragment (这个跟之前的版本不一样) 也就是说整个状态栏其实是一个 Fragment
```java
class StatusBar {
    public void makeStatusBarView(RegisterStatusBarResult registerStatusBarResult) {
        // ... 省略
        inflateStatusBarWindow();
        // ... 省略

        FragmentHostManager fragmentHostManager = FragmentHostManager.get(this.mPhoneStatusBarWindow);
        fragmentHostManager.addTagListener("CollapsedStatusBarFragment", new FragmentHostManager.FragmentListener() {
            public final void onFragmentViewCreated(String str, Fragment fragment) {
                StatusBar.this.lambda$makeStatusBarView$4$StatusBar(str, fragment);
            }
        });
        fragmentHostManager.getFragmentManager().beginTransaction().replace(C0015R$id.status_bar_container, new MiuiCollapsedStatusBarFragment(), "CollapsedStatusBarFragment").commit();
    }
}
```

MiuiCollapsedStatusBarFragment 继承自 CollapsedStatusBarFragment。 MiuiCollapsedStatusBarFragment 的布局是 R.layout.miui_status_bar。看下 miui_status_bar.xml 内容:
```xml
<com.android.systemui.statusbar.phone.MiuiPhoneStatusBarView
    android:id="@+id/status_bar"
    android:layout_width="match_parent"
    android:layout_height="@dimen/status_bar_height"
    android:accessibilityPaneTitle="@string/status_bar"
    android:descendantFocusability="afterDescendants"
    android:focusable="false"
    android:orientation="vertical">

    <com.android.systemui.statusbar.phone.BatteryIndicator
        android:id="@+id/battery_indicator"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_gravity="top"
        android:scaleType="fitXY"
        android:src="@drawable/battery_indicator"
        android:visibility="gone" />

    <ImageView
        android:id="@+id/notification_lights_out"
        android:layout_width="@dimen/status_bar_icon_size"
        android:layout_height="match_parent"
        android:paddingStart="@dimen/status_bar_padding_start"
        android:paddingBottom="2dp"
        android:scaleType="center"
        android:src="@drawable/ic_sysbar_lights_out_dot_small"
        android:visibility="gone" />

    <LinearLayout
        android:id="@+id/status_bar_contents"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:gravity="center_vertical"
        android:orientation="horizontal"
        android:paddingStart="@dimen/status_bar_padding_start"
        android:paddingTop="@dimen/status_bar_padding_top"
        android:paddingEnd="@dimen/status_bar_padding_end">

        <FrameLayout
            android:id="@+id/phone_status_bar_left_container"
            android:layout_width="wrap_content"
            android:layout_height="match_parent">

            <include layout="@layout/heads_up_status_bar_layout" />

            <LinearLayout
                android:id="@+id/status_bar_left_side"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:clipChildren="false">

                <FrameLayout
                    android:id="@+id/prompt_container"
                    android:layout_width="wrap_content"
                    android:layout_height="match_parent" />

                <ViewStub
                    android:id="@+id/operator_name"
                    android:layout_width="wrap_content"
                    android:layout_height="match_parent"
                    android:layout="@layout/operator_name" />

                <com.android.systemui.statusbar.policy.MiuiClock
                    android:id="@+id/clock"
                    android:layout_width="wrap_content"
                    android:layout_height="match_parent"
                    android:gravity="left|center_vertical|center_horizontal|center|start"
                    android:singleLine="true"
                    android:textAppearance="@style/TextAppearance.StatusBar.Clock"
                    systemui:MiuiAmPmStyle="0"
                    systemui:MiuiClockMode="0"
                    systemui:MiuiStatusBarClock="true" />

                <com.android.systemui.statusbar.views.NetworkSpeedSplitter
                    android:id="@+id/drip_network_speed_splitter"
                    android:layout_width="wrap_content"
                    android:layout_height="match_parent"
                    android:gravity="left|center_vertical|center_horizontal|center|start"
                    android:singleLine="true"
                    android:textAppearance="@style/TextAppearance.StatusBar.Clock"
                    android:visibility="gone" />

                <com.android.systemui.statusbar.views.NetworkSpeedView
                    android:id="@+id/drip_network_speed_view"
                    android:layout_width="wrap_content"
                    android:layout_height="match_parent"
                    android:gravity="left|center_vertical|center_horizontal|center|start"
                    android:singleLine="true"
                    android:textAppearance="@style/TextAppearance.StatusBar.Clock"
                    android:visibility="gone" />

                <com.android.systemui.statusbar.AlphaOptimizedFrameLayout
                    android:id="@+id/drip_left_status_icon_area"
                    android:layout_width="wrap_content"
                    android:layout_height="match_parent"
                    android:clipChildren="false"
                    android:visibility="gone">

                    <com.android.systemui.statusbar.phone.MiuiDripLeftStatusIconContainer
                        android:id="@+id/drip_left_statusIcons"
                        android:layout_width="wrap_content"
                        android:layout_height="match_parent"
                        android:gravity="center_vertical"
                        android:orientation="horizontal" />
                </com.android.systemui.statusbar.AlphaOptimizedFrameLayout>

                <com.android.systemui.statusbar.AlphaOptimizedFrameLayout
                    android:id="@+id/drip_notification_icon_area"
                    android:layout_width="0dp"
                    android:layout_height="match_parent"
                    android:layout_weight="1"
                    android:clipChildren="false"
                    android:orientation="horizontal"
                    android:visibility="gone" />
            </LinearLayout>
        </FrameLayout>

        <android.widget.Space
            android:id="@+id/cutout_space_view"
            android:layout_width="0dp"
            android:layout_height="match_parent"
            android:gravity="center" />

        <com.android.systemui.statusbar.AlphaOptimizedFrameLayout
            android:id="@+id/fullscreen_notification_icon_area"
            android:layout_width="0dp"
            android:layout_height="match_parent"
            android:layout_weight="1"
            android:clipChildren="false"
            android:orientation="horizontal" />

        <com.android.systemui.statusbar.AlphaOptimizedFrameLayout
            android:id="@+id/centered_icon_area"
            android:layout_width="wrap_content"
            android:layout_height="match_parent"
            android:clipChildren="false"
            android:gravity="center"
            android:orientation="horizontal"
            android:visibility="gone" />

        <com.android.systemui.statusbar.views.MiuiClipEdgeViewLinearLayout
            android:id="@+id/system_icon_area"
            android:layout_width="wrap_content"
            android:layout_height="match_parent"
            android:gravity="right|center_vertical|center_horizontal|center|end"
            android:orientation="horizontal">

            <include layout="@layout/miui_system_icons" />
        </com.android.systemui.statusbar.views.MiuiClipEdgeViewLinearLayout>
    </LinearLayout>

    <ViewStub
        android:id="@+id/emergency_cryptkeeper_text"
        android:layout_width="wrap_content"
        android:layout_height="match_parent"
        android:layout="@layout/emergency_cryptkeeper_text" />
</com.android.systemui.statusbar.phone.MiuiPhoneStatusBarView>
```

通过 miui_status_bar 我们可以看出：
1. miui_status_bar.xml 根节点是 MiuiPhoneStatusBarView， MiuiPhoneStatusBarView 继承自 PhoneStatusBarView
2. 状态栏显示时间的控件不再是 Clock 而是 MiuiClock

显秒除了使用之前的方式之外，发现在 MiuiClock 类中还有一个 show_seconds 字段，用于显秒。这个可以在 SystemUI 的 Demo Mode 中开启。[Demo Mode for the Android System UI](https://android.googlesource.com/platform/frameworks/base/+/master/packages/SystemUI/docs/demo_mode.md)
打开终端，然后输入 su，给完 root 权限之后，输入 `am start -n com.android.systemui/com.android.systemui.DemoMode` 就可以进入这个隐藏页面。（更新：这种方法无效，因为虽然每秒都会刷新时间，但是不展示秒数的文本，所以看起来无用）

R.layout.miui_system_icons 是系统icon (网速，夜间模式，闹钟，vpn，hd，wifi，手机信号，电量 等等):
```xml
<com.android.systemui.statusbar.views.MiuiClipEdgeViewConstraintLayout
    android:id="@+id/system_icons"
    android:layout_width="wrap_content"
    android:layout_height="match_parent"
    android:gravity="center_vertical">

    <com.android.systemui.MiuiBatteryMeterView
        android:id="@+id/battery"
        android:layout_width="wrap_content"
        android:layout_height="match_parent"
        android:focusable="true"
        android:focusableInTouchMode="true"
        ns1:layout_constraintEnd_toEndOf="0" />

    <LinearLayout xmlns:ns2="http://schemas.android.com/apk/res-auto"
        android:id="@+id/no_use_id"
        android:layout_width="wrap_content"
        android:layout_height="match_parent"
        android:gravity="right|center_vertical|center_horizontal|center|end"
        ns2:layout_constraintEnd_toStartOf="@+id/battery">

        <com.android.systemui.statusbar.views.NetworkSpeedView
            android:id="@+id/fullscreen_network_speed_view"
            android:layout_width="wrap_content"
            android:layout_height="match_parent"
            android:gravity="right|center_vertical|center_horizontal|center|end"
            android:paddingStart="@dimen/fullscreen_network_speed_padding_start"
            android:singleLine="true"
            android:textAppearance="@style/TextAppearance.StatusBar.Clock"
            android:visibility="gone" />

        <com.android.systemui.statusbar.phone.MiuiStatusIconContainer
            android:id="@+id/drip_right_statusIcons"
            android:layout_width="wrap_content"
            android:layout_height="match_parent"
            android:gravity="center_vertical"
            android:orientation="horizontal"
            android:visibility="gone" />

        <com.android.systemui.statusbar.phone.MiuiStatusIconContainer
            android:id="@+id/statusIcons"
            android:layout_width="wrap_content"
            android:layout_height="match_parent"
            android:gravity="center_vertical"
            android:orientation="horizontal" />
    </LinearLayout>
</com.android.systemui.statusbar.views.MiuiClipEdgeViewConstraintLayout>
```

### 下拉状态栏(下拉通知界面)
下拉通知界面对应的Fragment是 QSFragment, 其布局为 `R.layout.qs_panel` :
```xml
<?xml version="1.0" encoding="utf-8"?>
<com.android.systemui.qs.QSContainerImpl xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/quick_settings_container"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:clipChildren="false"
    android:clipToPadding="false">

    <View
        android:id="@+id/quick_settings_status_bar_background"
        android:layout_width="match_parent"
        android:layout_height="48dp"
        android:background="@android:color/transparent"
        android:clipChildren="false"
        android:clipToPadding="false" />

    <View
        android:id="@+id/quick_settings_gradient_view"
        android:layout_width="match_parent"
        android:layout_height="126dp"
        android:layout_marginTop="48dp"
        android:background="@android:color/transparent"
        android:clipChildren="false"
        android:clipToPadding="false" />

    <com.android.systemui.qs.QSContent
        android:id="@+id/qs_content"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="@android:color/transparent"
        android:focusable="true"
        android:focusableInTouchMode="true"
        android:importantForAccessibility="no"
        android:orientation="vertical">

        <LinearLayout
            android:id="@+id/qs_footer_bundle"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:elevation="4dp"
            android:orientation="vertical">

            <include
                layout="@layout/quick_settings_brightness_dialog"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_marginTop="@dimen/qs_brightness_top_margin" />

            <ImageView
                android:id="@+id/qs_expand_indicator"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_gravity="center_horizontal" />
        </LinearLayout>

        <View
            android:id="@+id/quick_settings_background"
            android:layout_width="match_parent"
            android:layout_height="0dp"
            android:background="@drawable/panel_round_corner_bg" />

        <com.android.systemui.qs.NonInterceptingScrollView
            android:id="@+id/expanded_qs_scroll_view"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:elevation="4dp"
            android:importantForAccessibility="no">

            <com.android.systemui.qs.QSPanel
                android:id="@+id/quick_settings_panel"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:accessibilityTraversalBefore="@android:id/edit"
                android:background="@android:color/transparent"
                android:focusable="true">

                <include layout="@layout/miui_qs_footer" />

                <include
                    android:id="@+id/divider"
                    layout="@layout/qs_media_divider" />
            </com.android.systemui.qs.QSPanel>
        </com.android.systemui.qs.NonInterceptingScrollView>

        <com.android.systemui.qs.QuickQSPanel
            android:id="@+id/quick_qs_panel"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:background="@android:color/transparent"
            android:clipChildren="false"
            android:clipToPadding="false"
            android:elevation="4dp"
            android:focusable="true"
            android:importantForAccessibility="yes" />
    </com.android.systemui.qs.QSContent>

    <include layout="@layout/miui_quick_status_bar_expanded_header" />

    <include
        android:id="@+id/qs_detail"
        layout="@layout/qs_detail" />

    <include
        android:id="@+id/qs_customize"
        layout="@layout/qs_customize_panel"
        android:visibility="gone" />
</com.android.systemui.qs.QSContainerImpl>
```

下拉界面在 `R.layout.miui_quick_status_bar_expanded_header` 所对应的布局中。
`R.layout.miui_quick_status_bar_expanded_header`:
```xml
<?xml version="1.0" encoding="utf-8"?>
<com.android.systemui.qs.MiuiNotificationShadeHeader xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/header"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:background="@android:color/transparent"
    android:baselineAligned="false"
    android:clickable="false"
    android:clipChildren="false"
    android:clipToPadding="false" />
```
通过打印 `MiuiNotificationShadeHeader` 内容，可以看到下拉界面中的时间,日期等界面在 `MiuiQSHeaderView` 中，`MiuiQSHeaderView` 继承自 RelativeLayout, 它最后一个子view就是右上角的设置icon。所以通过动态add的方式，将天气信息添加进去。