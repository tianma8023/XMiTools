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
