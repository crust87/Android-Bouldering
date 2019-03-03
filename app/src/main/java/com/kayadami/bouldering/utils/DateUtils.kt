package com.kayadami.bouldering.utils

import android.content.Context

import com.kayadami.bouldering.R

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {

    private val MONTH_IN_MILLI = (60 * 60 * 24 * 28).toLong()
    private val DAY_IN_MILLI = (60 * 60 * 24).toLong()
    private val HOUR_IN_MILLI = (60 * 60).toLong()
    private val MINUTE_IN_MILLI = 60.toLong()

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

    fun convertDate(timestamp: Long): String {
        val date = Date(timestamp)
        val current = Date()

        val dateInMilli = date.time
        val currentInMilli = current.time

        val deltaMilli = (currentInMilli - dateInMilli) / 1000

        return if (deltaMilli > MONTH_IN_MILLI) {
            formatInDate.format(date)
        } else if (deltaMilli > DAY_IN_MILLI) {
            String.format(textDays, deltaMilli / DAY_IN_MILLI)
        } else if (deltaMilli > HOUR_IN_MILLI) {
            String.format(textHrs, deltaMilli / HOUR_IN_MILLI)
        } else if (deltaMilli > MINUTE_IN_MILLI) {
            String.format(textMin, deltaMilli / MINUTE_IN_MILLI)
        } else {
            textJustNow
        }
    }

    fun convertDateTime(timestamp: Long): String {
        val date = Date(timestamp)
        return formatInDateTime.format(date)
    }
}
