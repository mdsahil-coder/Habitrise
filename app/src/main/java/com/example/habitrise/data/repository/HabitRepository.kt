package com.example.habitrise.data.repository

import com.example.habitrise.data.local.HabitRiseDatabase
import com.example.habitrise.data.model.ExerciseSession
import com.example.habitrise.data.model.FinanceSession
import com.example.habitrise.data.model.Habit
import com.example.habitrise.data.model.HabitCompletion
import com.example.habitrise.data.model.JournalEntry
import com.example.habitrise.data.model.PrayerRecord
import com.example.habitrise.data.model.ReadingSession
import com.example.habitrise.util.DateUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class HabitStreakInfo(
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val totalCompletions: Int = 0,
    val weeklyConsistencyRate: Float = 0f,
    val monthlyConsistencyRate: Float = 0f
)

data class OverallStreakStats(
    val todayCompletedCount: Int = 0,
    val totalActiveHabitsCount: Int = 0,
    val todayCompletionPercentage: Int = 0,
    val bestOverallStreak: Int = 0,
    val totalSessionsCount: Int = 0,
    val mostConsistentHabitName: String = "",
    val needsAttentionHabitName: String = ""
)

class HabitRepository(private val database: HabitRiseDatabase) {
    private val habitDao = database.habitDao()
    private val completionDao = database.habitCompletionDao()
    private val prayerDao = database.prayerDao()
    private val sessionDao = database.sessionDao()
    private val journalDao = database.journalDao()

    val activeHabits: Flow<List<Habit>> = habitDao.getActiveHabits()
    val allHabits: Flow<List<Habit>> = habitDao.getAllHabits()
    val allCompletions: Flow<List<HabitCompletion>> = completionDao.getAllCompletions()

    suspend fun ensureDefaultHabits() = withContext(Dispatchers.IO) {
        HabitRiseDatabase.seedDefaultHabits(habitDao)
    }

    fun getHabitById(id: Long): Flow<Habit?> = habitDao.getHabitById(id)

    suspend fun insertHabit(habit: Habit): Long = withContext(Dispatchers.IO) {
        habitDao.insertHabit(habit)
    }

    suspend fun updateHabit(habit: Habit) = withContext(Dispatchers.IO) {
        habitDao.updateHabit(habit)
    }

    suspend fun deleteHabit(habit: Habit) = withContext(Dispatchers.IO) {
        habitDao.deleteHabit(habit)
    }

    suspend fun deleteHabitById(id: Long) = withContext(Dispatchers.IO) {
        habitDao.deleteHabitById(id)
    }

    suspend fun archiveHabit(id: Long, archived: Boolean) = withContext(Dispatchers.IO) {
        habitDao.setArchived(id, archived)
    }

    fun getCompletionsForDate(date: String): Flow<List<HabitCompletion>> {
        return completionDao.getCompletionsForDate(date)
    }

    fun getCompletionsForHabit(habitId: Long): Flow<List<HabitCompletion>> {
        return completionDao.getCompletionsForHabit(habitId)
    }

    fun getCompletionsBetween(startDate: String, endDate: String): Flow<List<HabitCompletion>> {
        return completionDao.getCompletionsBetween(startDate, endDate)
    }

    suspend fun toggleHabitCompletion(habitId: Long, date: String): Boolean = withContext(Dispatchers.IO) {
        val existing = completionDao.getCompletion(habitId, date)
        if (existing != null) {
            completionDao.deleteCompletion(habitId, date)
            false
        } else {
            completionDao.insertCompletion(HabitCompletion(habitId = habitId, date = date))
            true
        }
    }

    suspend fun calculateStreak(habitId: Long): HabitStreakInfo = withContext(Dispatchers.IO) {
        val completions = completionDao.getCompletionsForHabitDirect(habitId)
        if (completions.isEmpty()) {
            return@withContext HabitStreakInfo()
        }

        val completedDatesSet = completions.map { it.date }.toSet()
        val sortedDates = completedDatesSet.sortedDescending()

        val today = DateUtils.getTodayDate()
        val yesterday = DateUtils.getDaysAgo(1)

        // Calculate Current Streak
        var currentStreak = 0
        var checkDate = if (completedDatesSet.contains(today)) today else yesterday
        if (completedDatesSet.contains(checkDate)) {
            var daysBack = if (checkDate == today) 0 else 1
            while (true) {
                val target = DateUtils.getDaysAgo(daysBack)
                if (completedDatesSet.contains(target)) {
                    currentStreak++
                    daysBack++
                } else {
                    break
                }
            }
        }

        // Calculate Best Streak
        var bestStreak = 0
        var tempStreak = 0
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        var prevCal: Calendar? = null

        // Sort ascending to find longest chain
        val ascendingDates = completedDatesSet.sorted()
        for (dateStr in ascendingDates) {
            val cal = Calendar.getInstance().apply { time = sdf.parse(dateStr)!! }
            if (prevCal == null) {
                tempStreak = 1
            } else {
                val diffDays = ((cal.timeInMillis - prevCal.timeInMillis) / (1000 * 60 * 60 * 24)).toInt()
                if (diffDays == 1) {
                    tempStreak++
                } else if (diffDays > 1) {
                    tempStreak = 1
                }
            }
            if (tempStreak > bestStreak) {
                bestStreak = tempStreak
            }
            prevCal = cal
        }
        if (currentStreak > bestStreak) {
            bestStreak = currentStreak
        }

        // Weekly consistency: completions in past 7 days / 7
        val past7Days = DateUtils.getPastDays(7)
        val weeklyHits = past7Days.count { completedDatesSet.contains(it) }
        val weeklyRate = (weeklyHits.toFloat() / 7f) * 100f

        // Monthly consistency: completions in past 30 days / 30
        val past30Days = DateUtils.getPastDays(30)
        val monthlyHits = past30Days.count { completedDatesSet.contains(it) }
        val monthlyRate = (monthlyHits.toFloat() / 30f) * 100f

        HabitStreakInfo(
            currentStreak = currentStreak,
            bestStreak = bestStreak,
            totalCompletions = completions.size,
            weeklyConsistencyRate = weeklyRate,
            monthlyConsistencyRate = monthlyRate
        )
    }

    suspend fun getOverallStats(todayDate: String): OverallStreakStats = withContext(Dispatchers.IO) {
        val habits = habitDao.getActiveHabits().first()
        val todayCompletions = completionDao.getCompletionsForDateDirect(todayDate)
        val allCompletions = completionDao.getAllCompletionsDirect()

        val activeHabitsCount = habits.size
        val todayCount = todayCompletions.size
        val pct = if (activeHabitsCount > 0) ((todayCount.toFloat() / activeHabitsCount) * 100).toInt() else 0

        var bestStreakOverall = 0
        var maxCompletionsHabit = ""
        var maxCompletionsCount = -1

        var minCompletionsHabit = ""
        var minCompletionsCount = Int.MAX_VALUE

        for (habit in habits) {
            val info = calculateStreak(habit.id)
            if (info.bestStreak > bestStreakOverall) {
                bestStreakOverall = info.bestStreak
            }
            if (info.totalCompletions > maxCompletionsCount) {
                maxCompletionsCount = info.totalCompletions
                maxCompletionsHabit = habit.name
            }
            if (info.totalCompletions < minCompletionsCount) {
                minCompletionsCount = info.totalCompletions
                minCompletionsHabit = habit.name
            }
        }

        OverallStreakStats(
            todayCompletedCount = todayCount,
            totalActiveHabitsCount = activeHabitsCount,
            todayCompletionPercentage = pct,
            bestOverallStreak = bestStreakOverall,
            totalSessionsCount = allCompletions.size,
            mostConsistentHabitName = if (maxCompletionsCount > 0) maxCompletionsHabit else (habits.firstOrNull()?.name ?: "None"),
            needsAttentionHabitName = if (habits.isNotEmpty()) minCompletionsHabit else "None"
        )
    }

    // Prayer Operations
    fun getPrayerRecord(date: String): Flow<PrayerRecord?> = prayerDao.getPrayerRecordForDate(date)

    suspend fun updatePrayerStatus(
        date: String,
        fajr: Boolean? = null,
        dhuhr: Boolean? = null,
        asr: Boolean? = null,
        maghrib: Boolean? = null,
        isha: Boolean? = null
    ) = withContext(Dispatchers.IO) {
        val current = prayerDao.getPrayerRecordForDateDirect(date) ?: PrayerRecord(date = date)
        val updated = current.copy(
            fajr = fajr ?: current.fajr,
            dhuhr = dhuhr ?: current.dhuhr,
            asr = asr ?: current.asr,
            maghrib = maghrib ?: current.maghrib,
            isha = isha ?: current.isha,
            updatedAt = System.currentTimeMillis()
        )
        prayerDao.insertOrUpdate(updated)

        // If at least one prayer or all prayers are completed, also update Namaz habit completion!
        val namazHabit = habitDao.getAllHabits().first().find { it.iconKey == "prayer" || it.name.contains("Namaz", ignoreCase = true) }
        if (namazHabit != null) {
            if (updated.completedCount >= 1) {
                completionDao.insertCompletion(HabitCompletion(habitId = namazHabit.id, date = date))
            } else {
                completionDao.deleteCompletion(namazHabit.id, date)
            }
        }
    }

    // Session Operations
    fun getReadingSessions(habitId: Long): Flow<List<ReadingSession>> = sessionDao.getReadingSessionsForHabit(habitId)
    fun getTotalReadingMinutes(): Flow<Int> = sessionDao.getTotalReadingMinutes()
    fun getTotalPagesRead(): Flow<Int> = sessionDao.getTotalPagesRead()

    suspend fun insertReadingSession(session: ReadingSession) = withContext(Dispatchers.IO) {
        sessionDao.insertReadingSession(session)
        if (session.habitId > 0) {
            completionDao.insertCompletion(HabitCompletion(habitId = session.habitId, date = session.date, durationMinutes = session.durationMinutes))
        }
    }

    suspend fun deleteReadingSession(session: ReadingSession) = withContext(Dispatchers.IO) {
        sessionDao.deleteReadingSession(session)
    }

    fun getExerciseSessions(habitId: Long): Flow<List<ExerciseSession>> = sessionDao.getExerciseSessionsForHabit(habitId)
    fun getTotalExerciseSessionsCount(): Flow<Int> = sessionDao.getTotalExerciseSessionsCount()
    fun getTotalExerciseMinutes(): Flow<Int> = sessionDao.getTotalExerciseMinutes()

    suspend fun insertExerciseSession(session: ExerciseSession) = withContext(Dispatchers.IO) {
        sessionDao.insertExerciseSession(session)
        if (session.habitId > 0) {
            completionDao.insertCompletion(HabitCompletion(habitId = session.habitId, date = session.date, durationMinutes = session.durationMinutes))
        }
    }

    suspend fun deleteExerciseSession(session: ExerciseSession) = withContext(Dispatchers.IO) {
        sessionDao.deleteExerciseSession(session)
    }

    fun getFinanceSessions(habitId: Long): Flow<List<FinanceSession>> = sessionDao.getFinanceSessionsForHabit(habitId)
    fun getTotalFinanceSessionsCount(): Flow<Int> = sessionDao.getTotalFinanceSessionsCount()
    fun getTotalFinanceMinutes(): Flow<Int> = sessionDao.getTotalFinanceMinutes()

    suspend fun insertFinanceSession(session: FinanceSession) = withContext(Dispatchers.IO) {
        sessionDao.insertFinanceSession(session)
        if (session.habitId > 0) {
            completionDao.insertCompletion(HabitCompletion(habitId = session.habitId, date = session.date, durationMinutes = session.durationMinutes))
        }
    }

    suspend fun deleteFinanceSession(session: FinanceSession) = withContext(Dispatchers.IO) {
        sessionDao.deleteFinanceSession(session)
    }

    // Journal Operations
    fun getAllJournalEntries(): Flow<List<JournalEntry>> = journalDao.getAllJournalEntries()
    fun getJournalEntryForDate(date: String): Flow<JournalEntry?> = journalDao.getJournalEntryForDate(date)

    suspend fun saveJournalEntry(entry: JournalEntry) = withContext(Dispatchers.IO) {
        journalDao.insertOrUpdateJournal(entry)
    }

    suspend fun deleteJournalEntry(entry: JournalEntry) = withContext(Dispatchers.IO) {
        journalDao.deleteJournalEntry(entry)
    }

    // Export & Reset Data
    suspend fun exportDataJson(): String = withContext(Dispatchers.IO) {
        val habits = habitDao.getAllHabits().first()
        val completions = completionDao.getAllCompletionsDirect()
        val journals = journalDao.getAllJournalEntries().first()
        val reading = sessionDao.getAllReadingSessions().first()
        val exercise = sessionDao.getAllExerciseSessions().first()
        val finance = sessionDao.getAllFinanceSessions().first()

        val root = JSONObject()
        root.put("app", "HabitRise")
        root.put("exportedAt", System.currentTimeMillis())

        val habitsArr = JSONArray()
        for (h in habits) {
            val obj = JSONObject().apply {
                put("id", h.id)
                put("name", h.name)
                put("category", h.category)
                put("goal", h.targetMetricGoal)
            }
            habitsArr.put(obj)
        }
        root.put("habits", habitsArr)

        val completionsArr = JSONArray()
        for (c in completions) {
            val obj = JSONObject().apply {
                put("habitId", c.habitId)
                put("date", c.date)
                put("timestamp", c.timestamp)
            }
            completionsArr.put(obj)
        }
        root.put("completions", completionsArr)

        val journalsArr = JSONArray()
        for (j in journals) {
            val obj = JSONObject().apply {
                put("date", j.date)
                put("wentWell", j.wentWell)
                put("madeDifficult", j.madeDifficult)
                put("improveTomorrow", j.improveTomorrow)
            }
            journalsArr.put(obj)
        }
        root.put("reflections", journalsArr)

        root.toString(2)
    }

    suspend fun resetAllData() = withContext(Dispatchers.IO) {
        completionDao.clearAllCompletions()
        prayerDao.clearAllPrayerRecords()
        sessionDao.clearReadingSessions()
        sessionDao.clearExerciseSessions()
        sessionDao.clearFinanceSessions()
        journalDao.clearAllJournalEntries()
        habitDao.clearAllHabits()
        HabitRiseDatabase.seedDefaultHabits(habitDao)
    }
}
