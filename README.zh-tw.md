*其他語言版本: [English](README.md), [中文](README.zh-tw.md).*

# ClockSlider
帶有圓形Slider的自定義時鐘。

<div style="dispaly:flex">
    <img src="https://user-images.githubusercontent.com/25738593/140450809-b648e1bd-28fb-4062-b185-543317401214.gif" width="32%">
</div>

<div style="dispaly:flex">
    <img src="https://user-images.githubusercontent.com/25738593/140451559-6793f4a1-3221-4809-b43b-bf6fd9e36a93.jpg" width="32%">
	<img src="https://user-images.githubusercontent.com/25738593/140451562-cd77f2c5-86e9-4321-8983-9b2df8fb877e.jpg" width="32%">
</div>

## 支援Android版本
- Android 4.0 Jelly Bean(API level 16)或更高。

## Gradle
```groovy
allprojects {
    repositories {
    ...
    
    maven { url 'https://jitpack.io' }
    }
}
```

```groovy
dependencies {
    implementation 'com.github.a1573595:ClockSlider:1.0.0'
}
```

## 用法
在布局中定義ClockSlider。
```xml
<com.a1573595.clockslider.ClockSlider
    android:id="@+id/clockSlider"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:cc_endHour="7"
    app:cc_fillColor="?attr/colorPrimary"
    app:cc_startHour="10"
    app:cc_tickTextColor="?android:attr/textColorPrimary"
    app:cc_startIconResource="@drawable/ic_moon"
    app:cc_endIconResource="@drawable/ic_sun" />
```

設定監聽器。
```kotlin
binding.clockSlider.setOnTimeChangedListener(object : ClockSlider.OnTimeChangedListener {
    override fun onStartChanged(hour: Int, minute: Int) {
        ...
    }

    override fun onEndChanged(hour: Int, minute: Int) {
        ...
    }
})
```

## 參考
[HGCircularSlider](https://github.com/HamzaGhazouani/HGCircularSlider)

[CircleAlarmTimerView](https://github.com/yingLanNull/CircleAlarmTimerView)

[speedometer](https://github.com/ibrahimsn98/speedometer)
