*Read this in other languages: [English](README.md), [中文](README.zh-tw.md).*

# ClockSlider
A custom clock view with a circular slider.

<div style="dispaly:flex">
    <img src="https://user-images.githubusercontent.com/25738593/140450809-b648e1bd-28fb-4062-b185-543317401214.gif" width="32%">
</div>

<div style="dispaly:flex">
    <img src="https://user-images.githubusercontent.com/25738593/140847972-c87b47a1-4623-4394-b7ed-5d55997888ec.jpg" width="32%">
	<img src="https://user-images.githubusercontent.com/25738593/140847974-819b9980-3867-4e1a-b8e7-0e6693f50417.jpg" width="32%">
</div>

## Supported Android Versions
- Android 4.0 Jelly Bean(API level 16) or higher.

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
    implementation 'com.github.a1573595:ClockSlider:v1.0.1'
}
```

## Usage
Define ClockSlider on your xml.
```xml
<com.a1573595.clockslider.ClockSlider
    android:id="@+id/clockSlider"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:cc_endHour="7"
    app:cc_endIconResource="@drawable/oval_white"
    app:cc_fillColor="?attr/colorPrimary"
    app:cc_metricMode="clock"
    app:cc_startHour="10"
    app:cc_startIconResource="@drawable/oval_white"
    app:cc_tickTextColor="?android:attr/textColorPrimary" />
```

Set listener.
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

## Attribute
| Attribute | Type | Default | Description |
| :------| :------ | :------ | :------ |
| cc_borderWidth | dimension | 72f | border width |
| cc_metricTextSize | dimension | 130f | metric text size |
| cc_borderColor | color | #CCCCCC | border color |
| cc_fillColor | color | #FFFF00 | border fill color |
| cc_tickTextColor | color | #000000 | tick text color |
| cc_startIconResource | reference | android.R.drawable.btn_star_big_on | start icon resource |
| cc_endIconResource | reference | android.R.drawable.btn_star_big_off | end icon resource |
| cc_is24HR | boolean | false | 12HR or 24HR |
| cc_startHour | float | 0f | start hour |
| cc_endHour | float | 0f | end hour |
| cc_metricMode | enum | counter | metric text mode |

## Reference
[HGCircularSlider](https://github.com/HamzaGhazouani/HGCircularSlider)

[CircleAlarmTimerView](https://github.com/yingLanNull/CircleAlarmTimerView)

[speedometer](https://github.com/ibrahimsn98/speedometer)
