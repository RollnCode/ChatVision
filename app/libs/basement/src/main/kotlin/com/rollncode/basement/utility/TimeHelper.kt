package com.rollncode.basement.utility

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import com.rollncode.basement.interfaces.TestImplemented
import com.rollncode.basement.utility.extension.reset
import java.text.DateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 *
 * @author Tregub Artem tregub.artem@gmail.com
 * @since 2017.09.29
 */
@TestImplemented("test.TimeHelperTest")
object TimeHelper {

    private val calendar by lazy { Calendar.getInstance() }
    private val date by lazy { Date() }

    fun isItWeekend(timeInMillis: Long): Boolean {
        synchronized(calendar) {
            calendar.timeInMillis = timeInMillis
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

            return dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY
        }
    }

    fun isItSameWeekend(older: Long, newer: Long): Boolean {
        val difference = newer - older
        return difference > 0 && difference < TimeUnit.DAYS.toMillis(2) && isItWeekend(older) && isItWeekend(newer)
    }

    fun isItEndOfMonth(timeInMillis: Long): Boolean {
        synchronized(calendar) {
            calendar.timeInMillis = timeInMillis
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
            val lastDayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

            return lastDayOfMonth - dayOfMonth < 2
        }
    }

    fun isItSameEndOfMonth(older: Long, newer: Long): Boolean {
        val difference = newer - older
        return difference > 0 && difference < TimeUnit.DAYS.toMillis(2) && isItEndOfMonth(older) && isItEndOfMonth(newer)
    }

    fun format(time: Long, format: DateFormat): String {
        synchronized(date) {
            date.time = time
            return format.format(date)
        }
    }

    fun startTimePicker(context: Context, listener: OnTimeSetListener, time: Any?) {
        synchronized(calendar) {
            val timeInMillis = time as? Long
            calendar.timeInMillis = if (timeInMillis == null || timeInMillis == 0L) System.currentTimeMillis() else timeInMillis

            TimePickerDialog(context, listener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
        }
    }

    fun startDatePicker(context: Context, listener: OnDateSetListener, time: Any?) {
        synchronized(calendar) {
            val timeInMillis = time as? Long
            calendar.timeInMillis = if (timeInMillis == null || timeInMillis == 0L) System.currentTimeMillis() else timeInMillis

            DatePickerDialog(context, listener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    fun toTimeInMillis(year: Int = 2000, month: Int = 6, dayOfMonth: Int = 10, hourOfDay: Int = 12, minute: Int = 0): Long {
        synchronized(calendar) {
            calendar.reset(false)

            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)

            return calendar.timeInMillis
        }
    }
}