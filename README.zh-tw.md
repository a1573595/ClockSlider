*其他語言版本: [English](README.md), [中文](README.zh-tw.md).*

# ClockSlider
帶有圓形Slider的自定義時鐘。

<div style="dispaly:flex">
    <img src="https://user-images.githubusercontent.com/25738593/140450809-b648e1bd-28fb-4062-b185-543317401214.gif" width="32%">
</div>

<div style="dispaly:flex">
    <img src="https://user-images.githubusercontent.com/25738593/140847972-c87b47a1-4623-4394-b7ed-5d55997888ec.jpg" width="32%">
	<img src="https://user-images.githubusercontent.com/25738593/140847974-819b9980-3867-4e1a-b8e7-0e6693f50417.jpg" width="32%">
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
    app:cc_endIconResource="@drawable/oval_white"
    app:cc_fillColor="?attr/colorPrimary"
    app:cc_metricMode="clock"
    app:cc_startHour="10"
    app:cc_startIconResource="@drawable/oval_white"
    app:cc_tickTextColor="?android:attr/textColorPrimary" />
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

## 屬性
| 屬性 | 類別 | 默認值 | 說明 |
| :------| :------ | :------ | :------ |
| cc_borderWidth | dimension | 72f | 邊框寬度 |
| cc_metricTextSize | dimension | 130f | 度量文字大小 |
| cc_borderColor | color | #CCCCCC | 邊框顏色 |
| cc_fillColor | color | #FFFF00 | 邊框填充顏色 |
| cc_tickTextColor | color | #000000 | 刻度文字顏色 |
| cc_startIconResource | reference | android.R.drawable.btn_star_big_on | 起始圖示資源 |
| cc_endIconResource | reference | android.R.drawable.btn_star_big_off | 結束圖示資源 |
| cc_is24HR | boolean | false | 12或24小時 |
| cc_startHour | float | 0f | 開始時間 |
| cc_endHour | float | 0f | 結束時間 |
| cc_metricMode | enum | counter | 度量文字模式 |

## 參考
[HGCircularSlider](https://github.com/HamzaGhazouani/HGCircularSlider)

[CircleAlarmTimerView](https://github.com/yingLanNull/CircleAlarmTimerView)

[speedometer](https://github.com/ibrahimsn98/speedometer)
