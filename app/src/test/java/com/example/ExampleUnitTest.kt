package com.example

import com.example.habitrise.data.model.Habit
import com.example.habitrise.data.model.PrayerRecord
import com.example.habitrise.util.DateUtils
import com.example.habitrise.util.Localization
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testPrayerRecordCompletionCalculation() {
        val emptyRecord = PrayerRecord(date = "2026-10-29")
        assertEquals(0, emptyRecord.completedCount)
        assertFalse(emptyRecord.isAllCompleted)

        val partialRecord = PrayerRecord(date = "2026-10-29", fajr = true, dhuhr = true, asr = true)
        assertEquals(3, partialRecord.completedCount)
        assertFalse(partialRecord.isAllCompleted)

        val fullRecord = PrayerRecord(
            date = "2026-10-29",
            fajr = true,
            dhuhr = true,
            asr = true,
            maghrib = true,
            isha = true
        )
        assertEquals(5, fullRecord.completedCount)
        assertTrue(fullRecord.isAllCompleted)
    }

    @Test
    fun testDateUtilsTodayFormat() {
        val today = DateUtils.getTodayDate()
        assertTrue(today.matches(Regex("\\d{4}-\\d{2}-\\d{2}")))
    }

    @Test
    fun testLocalizationBilingual() {
        val enHome = Localization.get("nav_home", isHindi = false)
        val hiHome = Localization.get("nav_home", isHindi = true)
        assertEquals("Home", enHome)
        assertEquals("होम", hiHome)

        val enStreak = Localization.get("current_streak", isHindi = false)
        val hiStreak = Localization.get("current_streak", isHindi = true)
        assertEquals("Current Streak", enStreak)
        assertEquals("लगातार पूरे किए गए दिन", hiStreak)
    }
}
