package test

import com.rollncode.basement.utility.*
import org.junit.*
import java.util.*

/**
 *
 * @author Tregub Artem tregub.artem@gmail.com
 * @since 2017.09.30
 */
class TimeHelperTest {

    @Test
    fun isItWeekendTest() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)

        calendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY)
        Assert.assertFalse(TimeHelper.isItWeekend(calendar.timeInMillis))

        calendar.add(Calendar.DAY_OF_WEEK, 1)
        Assert.assertTrue(TimeHelper.isItWeekend(calendar.timeInMillis))

        calendar.add(Calendar.DAY_OF_WEEK, 1)
        Assert.assertTrue(TimeHelper.isItWeekend(calendar.timeInMillis))

        calendar.add(Calendar.DAY_OF_WEEK, 1)
        Assert.assertFalse(TimeHelper.isItWeekend(calendar.timeInMillis))
    }

    @Test
    fun isItSameWeekendTest() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)

        calendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY)
        val friday = calendar.timeInMillis

        calendar.reset(true).add(Calendar.DAY_OF_WEEK, 1)
        val saturday = calendar.timeInMillis

        Assert.assertFalse(TimeHelper.isItSameWeekend(friday, saturday))
        Assert.assertFalse(TimeHelper.isItSameWeekend(saturday, friday))

        calendar.reset(false).add(Calendar.DAY_OF_WEEK, 1)
        val sunday = calendar.timeInMillis

        Assert.assertTrue(TimeHelper.isItSameWeekend(saturday, sunday))
        Assert.assertFalse(TimeHelper.isItSameWeekend(sunday, saturday))

        calendar.add(Calendar.DAY_OF_WEEK, 1)
        val monday = calendar.timeInMillis

        Assert.assertFalse(TimeHelper.isItSameWeekend(sunday, monday))
        Assert.assertFalse(TimeHelper.isItSameWeekend(sunday, monday))

        calendar.add(Calendar.WEEK_OF_YEAR, 1)
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
        val futureSaturday = calendar.timeInMillis

        Assert.assertFalse(TimeHelper.isItSameWeekend(saturday, futureSaturday))
        Assert.assertFalse(TimeHelper.isItSameWeekend(sunday, futureSaturday))
    }

    @Test
    fun isItEndOfMonthTest() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)

        calendar.add(Calendar.DAY_OF_MONTH, -1)
        Assert.assertTrue(TimeHelper.isItEndOfMonth(calendar.timeInMillis))

        calendar.add(Calendar.DAY_OF_MONTH, -1)
        Assert.assertTrue(TimeHelper.isItEndOfMonth(calendar.timeInMillis))

        calendar.add(Calendar.DAY_OF_MONTH, -1)
        Assert.assertFalse(TimeHelper.isItEndOfMonth(calendar.timeInMillis))

        calendar.add(Calendar.DAY_OF_MONTH, -1)
        Assert.assertFalse(TimeHelper.isItEndOfMonth(calendar.timeInMillis))
    }

    @Test
    fun isItSameEndOfMonthTest() {
        val calendar = Calendar.getInstance()

        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val nextDay = calendar.timeInMillis

        calendar.reset(true).add(Calendar.DAY_OF_MONTH, -1)
        val lastDay = calendar.timeInMillis

        Assert.assertFalse(TimeHelper.isItSameEndOfMonth(lastDay, nextDay))
        Assert.assertFalse(TimeHelper.isItSameEndOfMonth(nextDay, lastDay))

        calendar.reset(false).add(Calendar.DAY_OF_MONTH, -1)
        val previousDay = calendar.timeInMillis

        Assert.assertTrue(TimeHelper.isItSameEndOfMonth(previousDay, lastDay))
        Assert.assertFalse(TimeHelper.isItSameEndOfMonth(lastDay, previousDay))

        calendar.add(Calendar.DAY_OF_MONTH, -1)
        val prePreviousDay = calendar.timeInMillis

        Assert.assertFalse(TimeHelper.isItSameEndOfMonth(prePreviousDay, lastDay))
        Assert.assertFalse(TimeHelper.isItSameEndOfMonth(lastDay, prePreviousDay))

        Assert.assertFalse(TimeHelper.isItSameEndOfMonth(prePreviousDay, previousDay))
        Assert.assertFalse(TimeHelper.isItSameEndOfMonth(previousDay, prePreviousDay))

        calendar.add(Calendar.DAY_OF_MONTH, -1)
        val prePrePreviousDay = calendar.timeInMillis

        Assert.assertFalse(TimeHelper.isItSameEndOfMonth(prePrePreviousDay, prePreviousDay))
    }
}

private fun Calendar.reset(startOfDay: Boolean): Calendar {
    if (startOfDay) {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)

    } else {
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
    }
    return this
}