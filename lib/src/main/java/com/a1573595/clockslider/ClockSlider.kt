package com.a1573595.clockslider

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.*
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.res.use
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.*

class ClockSlider @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    companion object {
        private const val MAX_SCALE = 11
        private const val MIX_SCALE = 0

        private const val MAJOR_TICK_SIZE = 80f
        private const val MINOR_TICK_SIZE = 50f
        private const val MAJOR_TICK_WIDTH = 6f
        private const val MINOR_TICK_WIDTH = 3f
        private const val TICK_MARGIN = 30f
        private const val TICK_TEXT_MARGIN = 15f

        private const val MIN_ANGLE = 90f
        private const val MAX_ANGLE = -240f
        private const val START_ANGLE = -90f
        private const val SWEEP_ANGLE = 360f
    }

    @Dimension
    private var _borderWidth = 72f
        set(value) {
            field = value
            _iconWidth = value / 2
            indicatorBorderPaint.strokeWidth = value
            indicatorFillPaint.strokeWidth = value
        }

    @Dimension
    private var _iconWidth: Float = _borderWidth / 2

    @Dimension
    private var _metricTextSizeWidth: Float = 130f
        set(value) {
            field = value
            metricPaint.textSize = value
        }

    @ColorInt
    private var _borderColor = Color.LTGRAY
        set(value) {
            field = value
            indicatorBorderPaint.color = value
            majorTickPaint.color = value
            minorTickPaint.color = value
        }

    @ColorInt
    private var _fillColor = Color.YELLOW
        set(value) {
            field = value
            indicatorFillPaint.color = value
            metricPaint.color = value
        }

    @ColorInt
    private var _tickTextColor = Color.BLACK
        set(value) {
            field = value
            tickTextPaint.color = value
        }

    @DrawableRes
    private var _startIconResource: Int = android.R.drawable.btn_star_big_on
        set(value) {
            field = value
            startIcon = ResourcesCompat.getDrawable(resources, value, context.theme)
        }

    @DrawableRes
    private var _endIconResource: Int = android.R.drawable.btn_star_big_off
        set(value) {
            field = value
            endIcon = ResourcesCompat.getDrawable(resources, value, context.theme)
        }

    private var metricMode: MetricMode = MetricMode.COUNTER

    private var indicatorBorderRect = RectF()

    private val textBoundsRect = Rect()

    private val indicatorBorderPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        color = _borderColor
        strokeWidth = _borderWidth
        strokeCap = Paint.Cap.ROUND
    }

    private val indicatorFillPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        color = _fillColor
        strokeWidth = _borderWidth
        strokeCap = Paint.Cap.ROUND
    }

    private val majorTickPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        color = _borderColor
        strokeWidth = MAJOR_TICK_WIDTH
        strokeCap = Paint.Cap.BUTT
    }

    private val minorTickPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        color = _borderColor
        strokeWidth = MINOR_TICK_WIDTH
        strokeCap = Paint.Cap.BUTT
    }

    private val tickTextPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color = _tickTextColor
        textSize = 40f
    }

    private val metricPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color = _fillColor
        textSize = _metricTextSizeWidth
    }

    private var startIcon: Drawable? = null
    private var endIcon: Drawable? = null

    private val decimalFormat = DecimalFormat("##0.0hr")

    private val centerX get() = width / 2f
    private val centerY get() = height / 2f

    private var isInStartIcon: Boolean = false
    private var isInEndIcon: Boolean = false

    private var currentStartRadian: Float = 0f
    private var currentEndRadian: Float = 0f
    private var radius: Float = 0f
    private var preRadian: Float = 0f

    private var _angleOfAnHour = 30

    private var is24HR = false
        set(value) {
            field = value
            _angleOfAnHour = if (is24HR) 15 else 30
        }

    var startHours: Float = 0f
        set(value) {
            field = if (is24HR) value % 24 else value % 12
            currentStartRadian = hoursToAdian(field)
            invalidate()
        }

    var endHours: Float = 0f
        set(value) {
            field = if (is24HR) value % 24 else value % 12
            currentEndRadian = hoursToAdian(field)
            invalidate()
        }

    interface OnTimeChangedListener {
        fun onStartChanged(hour: Int, minute: Int)

        fun onEndChanged(hour: Int, minute: Int)
    }

    private var timeChangedListener: OnTimeChangedListener? = null

    init {
        decimalFormat.roundingMode = RoundingMode.HALF_UP

        obtainStyledAttributes(attrs, defStyleAttr)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        indicatorBorderRect.set(
            _borderWidth / 2,
            _borderWidth / 2,
            height.coerceAtMost(width) - _borderWidth / 2,
            height.coerceAtMost(width) - _borderWidth / 2
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val height = MeasureSpec.getSize(heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)

        radius = (height.coerceAtMost(width) - _borderWidth) / 2

        setMeasuredDimension(height.coerceAtMost(width), height.coerceAtMost(width))
    }

    override fun onDraw(canvas: Canvas) {
        renderBorder(canvas)
        renderBorderFill(canvas)
        renderBorderStart(canvas)
        renderBorderEnd(canvas)
        renderMajorTicks(canvas)
        renderMinorTicks(canvas)
        renderPeriodText(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action and event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                if (inStartCircleButton(event.x, event.y) && isEnabled) {
                    isInStartIcon = true
                    preRadian = getRadian(event.x, event.y)

                    parent.requestDisallowInterceptTouchEvent(true)
                    return true
                } else if (inEndCircleButton(event.x, event.y) && isEnabled) {
                    isInEndIcon = true
                    preRadian = getRadian(event.x, event.y)

                    parent.requestDisallowInterceptTouchEvent(true)
                    return true
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (isInStartIcon && isEnabled) {
                    val tempRadian = getRadian(event.x, event.y)
                    currentStartRadian = getCurrentRadian(currentStartRadian, tempRadian)

                    startHours = radianToHours(currentStartRadian)
                    return true
                } else if (isInEndIcon && isEnabled) {
                    val tempRadian = getRadian(event.x, event.y)
                    currentEndRadian = getCurrentRadian(currentEndRadian, tempRadian)

                    endHours = radianToHours(currentEndRadian)
                    return true
                }
            }
            MotionEvent.ACTION_UP -> {
                if (isInStartIcon) {
                    isInStartIcon = false

                    timeChangedListener?.onStartChanged(
                        startHours.toInt(),
                        getHoursMinute(startHours)
                    )
                    return true
                } else if (isInEndIcon) {
                    isInEndIcon = false

                    timeChangedListener?.onEndChanged(
                        endHours.toInt(),
                        getHoursMinute(endHours)
                    )
                    return true
                }

                performClick()
            }
        }
        return false
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    fun setOnTimeChangedListener(listener: OnTimeChangedListener) {
        timeChangedListener = listener
    }

    private fun obtainStyledAttributes(attrs: AttributeSet?, defStyleAttr: Int) {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.ClockSlider,
            defStyleAttr,
            0
        ).use {
            _borderWidth = it.getDimension(
                R.styleable.ClockSlider_cc_borderWidth,
                _borderWidth
            )
            _metricTextSizeWidth = it.getDimension(
                R.styleable.ClockSlider_cc_metricTextSize,
                _metricTextSizeWidth
            )
            _borderColor = it.getColor(
                R.styleable.ClockSlider_cc_borderColor,
                _borderColor
            )
            _fillColor = it.getColor(
                R.styleable.ClockSlider_cc_fillColor,
                _fillColor
            )
            _tickTextColor = it.getColor(
                R.styleable.ClockSlider_cc_tickTextColor,
                _tickTextColor
            )
            _startIconResource = it.getResourceId(
                R.styleable.ClockSlider_cc_startIconResource,
                _startIconResource
            )
            _endIconResource = it.getResourceId(
                R.styleable.ClockSlider_cc_endIconResource,
                _endIconResource
            )
            is24HR = it.getBoolean(R.styleable.ClockSlider_cc_is24HR, is24HR)
            startHours = it.getFloat(R.styleable.ClockSlider_cc_startHour, startHours)
            endHours = it.getFloat(R.styleable.ClockSlider_cc_endHour, endHours)
            metricMode = MetricMode.find(
                it.getInt(
                    R.styleable.ClockSlider_cc_metricMode,
                    metricMode.ordinal
                )
            )
        }
    }

    private fun renderBorder(canvas: Canvas) {
        canvas.drawArc(
            indicatorBorderRect,
            START_ANGLE,
            SWEEP_ANGLE,
            false,
            indicatorBorderPaint
        )
    }

    private fun renderBorderFill(canvas: Canvas) {
        canvas.drawArc(
            indicatorBorderRect,
            START_ANGLE + (startHours * _angleOfAnHour),
            when {
                endHours >= startHours -> (endHours - startHours) * _angleOfAnHour
                is24HR -> (endHours + 24 - startHours) * _angleOfAnHour
                else -> (endHours + 12 - startHours) * _angleOfAnHour
            },
            false,
            indicatorFillPaint
        )
    }

    private fun renderBorderStart(canvas: Canvas) {
        startIcon?.apply {
            val hour = if (is24HR) startHours / 2 else startHours

            setBounds(
                (centerX - _iconWidth + (centerX - _borderWidth / 2) * cos(mapTextToAngle(hour).toRadian())).toInt(),
                (centerY - _iconWidth - (centerY - _borderWidth / 2) * sin(mapTextToAngle(hour).toRadian())).toInt(),
                (centerX + _iconWidth + (centerX - _borderWidth / 2) * cos(mapTextToAngle(hour).toRadian())).toInt(),
                (centerY + _iconWidth - (centerY - _borderWidth / 2) * sin(mapTextToAngle(hour).toRadian())).toInt()
            )
            draw(canvas)

            getHoursMinute(startHours)
        }
    }

    private fun renderBorderEnd(canvas: Canvas) {
        endIcon?.apply {
            val hour = if (is24HR) endHours / 2 else endHours

            setBounds(
                (centerX - _iconWidth + (centerX - _borderWidth / 2) * cos(mapTextToAngle(hour).toRadian())).toInt(),
                (centerY - _iconWidth - (centerY - _borderWidth / 2) * sin(mapTextToAngle(hour).toRadian())).toInt(),
                (centerX + _iconWidth + (centerX - _borderWidth / 2) * cos(mapTextToAngle(hour).toRadian())).toInt(),
                (centerY + _iconWidth - (centerY - _borderWidth / 2) * sin(mapTextToAngle(hour).toRadian())).toInt()
            )

            draw(canvas)
        }
    }

    private fun renderMajorTicks(canvas: Canvas) {
        val scale = MIX_SCALE..MAX_SCALE

        scale.forEach {
            canvas.drawLine(
                centerX + (centerX - _borderWidth - MAJOR_TICK_SIZE) * cos(mapTextToAngle(it.toFloat()).toRadian()),
                centerY - (centerY - _borderWidth - MAJOR_TICK_SIZE) * sin(mapTextToAngle(it.toFloat()).toRadian()),
                centerX + (centerX - _borderWidth - TICK_MARGIN) * cos(mapTextToAngle(it.toFloat()).toRadian()),
                centerY - (centerY - _borderWidth - TICK_MARGIN) * sin(mapTextToAngle(it.toFloat()).toRadian()),
                majorTickPaint
            )

            canvas.drawTextCentred(
                "${if (is24HR) it * 2 else if (it == 0) 12 else it}",
                centerX + (centerX - _borderWidth - MAJOR_TICK_SIZE - TICK_MARGIN - TICK_TEXT_MARGIN) * cos(
                    mapTextToAngle(it.toFloat()).toRadian()
                ),
                centerY - (centerY - _borderWidth - MAJOR_TICK_SIZE - TICK_MARGIN - TICK_TEXT_MARGIN) * sin(
                    mapTextToAngle(it.toFloat()).toRadian()
                ),
                tickTextPaint
            )
        }
    }

    private fun renderMinorTicks(canvas: Canvas) {
        val scale = MIX_SCALE..MAX_SCALE + 1

        var i = 0f
        while (i <= scale.last) {
            i += 0.2f

            canvas.drawLine(
                centerX + (centerX - _borderWidth - MINOR_TICK_SIZE) * cos(mapTextToAngle(i).toRadian()),
                centerY - (centerY - _borderWidth - MINOR_TICK_SIZE) * sin(mapTextToAngle(i).toRadian()),
                centerX + (centerX - _borderWidth - TICK_MARGIN) * cos(mapTextToAngle(i).toRadian()),
                centerY - (centerY - _borderWidth - TICK_MARGIN) * sin(mapTextToAngle(i).toRadian()),
                minorTickPaint
            )
        }
    }

    private fun renderPeriodText(canvas: Canvas) {
        val start = startHours.toInt() * 60 + getHoursMinute(startHours)
        val end = endHours.toInt() * 60 + getHoursMinute(endHours)
        val time = when {
            end >= start -> end - start
            is24HR -> end - start + 24 * 60
            else -> end - start + 12 * 60
        }

        canvas.drawTextCentred(
            if (metricMode == MetricMode.COUNTER)
                decimalFormat.format(time / 60 + ((time % 60).toFloat() / 100)) else "%02d:%02d".format(
                time / 60,
                time % 60
            ),
            width / 2f,
            height / 2f,
            metricPaint
        )
    }

    private fun mapTextToAngle(time: Float): Float {
        return (MIN_ANGLE + ((MAX_ANGLE - MIN_ANGLE) / (MAX_SCALE - MIX_SCALE)) * (time - MIX_SCALE))
    }

    private fun inStartCircleButton(x: Float, y: Float): Boolean {
        val r: Float = radius - _borderWidth / 2
        val x2 = (centerX + r * sin(currentStartRadian.toDouble())).toFloat()
        val y2 = (centerY - r * cos(currentStartRadian.toDouble())).toFloat()
        return sqrt(((x - x2) * (x - x2) + (y - y2) * (y - y2)).toDouble()) < (_borderWidth)
    }

    private fun inEndCircleButton(x: Float, y: Float): Boolean {
        val r: Float = radius - _borderWidth / 2
        val x2 = (centerX + r * sin(currentEndRadian.toDouble())).toFloat()
        val y2 = (centerY - r * cos(currentEndRadian.toDouble())).toFloat()
        return sqrt(((x - x2) * (x - x2) + (y - y2) * (y - y2)).toDouble()) < (_borderWidth)
    }

    private fun getRadian(x: Float, y: Float): Float {
        var alpha = atan(((x - centerX) / (centerY - y)).toDouble()).toFloat()
        if (x > centerX && y > centerY) {
            alpha += Math.PI.toFloat()
        } else if (x < centerX && y > centerY) {
            alpha += Math.PI.toFloat()
        } else if (x < centerX && y < centerY) {
            alpha = (2 * Math.PI + alpha).toFloat()
        }
        return alpha
    }

    private fun getCurrentRadian(currentRadian: Float, tmpRadian: Float): Float {
        var currentRadian = currentRadian
        if (preRadian > Math.toRadians(270.0) && tmpRadian < Math.toRadians(90.0)) {
            preRadian -= (2 * Math.PI).toFloat()
        } else if (preRadian < Math.toRadians(90.0) && tmpRadian > Math.toRadians(270.0)) {
            preRadian = (tmpRadian + (tmpRadian - 2 * Math.PI) - preRadian).toFloat()
        }

        currentRadian += tmpRadian - preRadian
        preRadian = tmpRadian

        if (currentRadian > 2 * Math.PI) {
            currentRadian -= (2 * Math.PI).toFloat()
        }
        if (currentRadian < 0) {
            currentRadian += (2 * Math.PI).toFloat()
        }

        return currentRadian
    }

    private fun radianToHours(radian: Float): Float {
        val degree = Math.toDegrees(radian.toDouble()) % 360
        return (degree / _angleOfAnHour).toFloat()
    }

    private fun hoursToAdian(hours: Float): Float {
        val degree = hours * _angleOfAnHour
        return Math.toRadians(degree.toDouble()).toFloat()
    }

    private fun getHoursMinute(hours: Float): Int {
        var minute = (hours * 60 % 60).toInt()
        val level = 5

        for (i in level..(minute + level) step level) {
            if (minute < i) {
                minute = i - level
                break
            }
        }

        return minute
    }

    private fun Float.toRadian(): Float {
        return this * (PI / 180).toFloat()
    }

    private fun Canvas.drawTextCentred(text: String, cx: Float, cy: Float, paint: Paint) {
        paint.getTextBounds(text, 0, text.length, textBoundsRect)
        drawText(
            text,
            cx - textBoundsRect.exactCenterX(),
            cy - textBoundsRect.exactCenterY(),
            paint
        )
    }
}