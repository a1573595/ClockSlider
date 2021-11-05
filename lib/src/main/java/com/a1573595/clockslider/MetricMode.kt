package com.a1573595.clockslider

enum class MetricMode {
    COUNTER,
    CLOCK;

    companion object {
        fun find(value: Int): MetricMode = values().firstOrNull { it.ordinal == value } ?: COUNTER
    }
}