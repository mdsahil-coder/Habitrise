package com.example.habitrise.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {
    private val isoFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val displayFormatEn = SimpleDateFormat("EEEE, MMMM d", Locale.US)
    private val shortDisplayFormatEn = SimpleDateFormat("MMM d", Locale.US)
    private val monthYearFormatEn = SimpleDateFormat("MMMM yyyy", Locale.US)

    fun getTodayDate(): String {
        return isoFormat.format(Date())
    }

    fun parseDate(dateStr: String): Date {
        return try {
            isoFormat.parse(dateStr) ?: Date()
        } catch (e: Exception) {
            Date()
        }
    }

    fun formatDateForDisplay(dateStr: String, isHindi: Boolean = false): String {
        return try {
            val date = parseDate(dateStr)
            val cal = Calendar.getInstance().apply { time = date }
            val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
            val dayOfMonth = cal.get(Calendar.DAY_OF_MONTH)
            val month = cal.get(Calendar.MONTH)

            if (isHindi) {
                val hindiDays = arrayOf("", "रविवार", "सोमवार", "मंगलवार", "बुधवार", "गुरुवार", "शुक्रवार", "शनिवार")
                val hindiMonths = arrayOf("जनवरी", "फ़रवरी", "मार्च", "अप्रैल", "मई", "जून", "जुलाई", "अगस्त", "सितंबर", "अक्टूबर", "नवंबर", "दिसंबर")
                "${hindiDays[dayOfWeek]}, $dayOfMonth ${hindiMonths[month]}"
            } else {
                displayFormatEn.format(date)
            }
        } catch (e: Exception) {
            dateStr
        }
    }

    fun getShortDateForDisplay(dateStr: String): String {
        return try {
            val date = parseDate(dateStr)
            shortDisplayFormatEn.format(date)
        } catch (e: Exception) {
            dateStr
        }
    }

    fun getMonthYearDisplay(year: Int, month: Int, isHindi: Boolean = false): String {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, 1)
        }
        return if (isHindi) {
            val hindiMonths = arrayOf("जनवरी", "फ़रवरी", "मार्च", "अप्रैल", "मई", "जून", "जुलाई", "अगस्त", "सितंबर", "अक्टूबर", "नवंबर", "दिसंबर")
            "${hindiMonths[month]} $year"
        } else {
            monthYearFormatEn.format(cal.time)
        }
    }

    fun getDayOfWeekLabel(dateStr: String, isHindi: Boolean = false): String {
        val date = parseDate(dateStr)
        val cal = Calendar.getInstance().apply { time = date }
        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        return if (isHindi) {
            val hindiShortDays = arrayOf("", "रवि", "सोम", "मंगल", "बुध", "गुरु", "शुक्र", "शनि")
            hindiShortDays[dayOfWeek]
        } else {
            val enShortDays = arrayOf("", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
            enShortDays[dayOfWeek]
        }
    }

    fun getDaysAgo(daysAgo: Int): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -daysAgo)
        return isoFormat.format(cal.time)
    }

    fun getPastDays(count: Int): List<String> {
        val list = mutableListOf<String>()
        val cal = Calendar.getInstance()
        for (i in (count - 1) downTo 0) {
            val c = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, -i)
            }
            list.add(isoFormat.format(c.time))
        }
        return list
    }

    fun getDaysInMonth(year: Int, month: Int): List<String> {
        val list = mutableListOf<String>()
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, 1)
        }
        val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (d in 1..maxDays) {
            cal.set(Calendar.DAY_OF_MONTH, d)
            list.add(isoFormat.format(cal.time))
        }
        return list
    }

    fun getGreeting(isHindi: Boolean = false): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when {
            hour < 12 -> if (isHindi) "शुभ प्रभात!" else "Good Morning!"
            hour < 17 -> if (isHindi) "शुभ दोपहर!" else "Good Afternoon!"
            else -> if (isHindi) "शुभ संध्या!" else "Good Evening!"
        }
    }
}
