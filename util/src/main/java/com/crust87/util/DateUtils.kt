package com.crust87.util

import android.content.Context

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {

    private const val MONTH_IN_SECOND = (60 * 60 * 24 * 28).toLong()
    private const val DAY_IN_SECOND = (60 * 60 * 24).toLong()
    private const val HOUR_IN_SECOND = (60 * 60).toLong()
    private const val MINUTE_IN_SECOND = 60.toLong()

    private lateinit var formatInDate: SimpleDateFormat
    private lateinit var formatInDateTime: SimpleDateFormat
    private lateinit var textJustNow: String
    private lateinit var textMin: String
    private lateinit var textHrs: String
    private lateinit var textDays: String

    fun initialize(context: Context) {
        textJustNow = context.getString(R.string.time_just_now)
        textMin = context.getString(R.string.time_min)
        textHrs = context.getString(R.string.time_hrs)
        textDays = context.getString(R.string.time_day)

        formatInDate = SimpleDateFormat(context.getString(R.string.time_date), Locale.ENGLISH)
        formatInDateTime = SimpleDateFormat(context.getString(R.string.time_date_time), Locale.ENGLISH)
    }

    fun convertDate(timestamp: Long, currentInMilli: Long = System.currentTimeMillis()): String {
        val date = Date(timestamp)

        val deltaSecond = (currentInMilli - timestamp) / 1000

        return if (deltaSecond > MONTH_IN_SECOND) {
            formatInDate.format(date)
        } else if (deltaSecond > DAY_IN_SECOND) {
            String.format(textDays, deltaSecond / DAY_IN_SECOND)
        } else if (deltaSecond > HOUR_IN_SECOND) {
            String.format(textHrs, deltaSecond / HOUR_IN_SECOND)
        } else if (deltaSecond > MINUTE_IN_SECOND) {
            String.format(textMin, deltaSecond / MINUTE_IN_SECOND)
        } else {
            textJustNow
        }
    }

    fun convertDateTime(timestamp: Long): String {
        val date = Date(timestamp)
        return formatInDateTime.format(date)
    }
}
