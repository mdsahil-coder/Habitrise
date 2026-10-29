package com.example.habitrise.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.habitrise.data.model.ExerciseSession
import com.example.habitrise.data.model.FinanceSession
import com.example.habitrise.data.model.Habit
import com.example.habitrise.data.model.HabitCompletion
import com.example.habitrise.data.model.JournalEntry
import com.example.habitrise.data.model.PrayerRecord
import com.example.habitrise.data.model.ReadingSession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Habit::class,
        HabitCompletion::class,
        PrayerRecord::class,
        ReadingSession::class,
        ExerciseSession::class,
        FinanceSession::class,
        JournalEntry::class
    ],
    version = 1,
    exportSchema = false
)
abstract class HabitRiseDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun habitCompletionDao(): HabitCompletionDao
    abstract fun prayerDao(): PrayerDao
    abstract fun sessionDao(): SessionDao
    abstract fun journalDao(): JournalDao

    companion object {
        @Volatile
        private var INSTANCE: HabitRiseDatabase? = null

        fun getDatabase(context: Context): HabitRiseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HabitRiseDatabase::class.java,
                    "habitrise_database"
                )
                    .addCallback(DatabaseCallback(context.applicationContext))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        val DEFAULT_HABITS = listOf(
            Habit(
                id = 1,
                name = "Namaz Padhna",
                nameHindi = "नमाज़ पढ़ना",
                description = "Track daily prayers according to your personal routine with individual prayer checklist.",
                descriptionHindi = "अपनी व्यक्तिगत दिनचर्या के अनुसार दैनिक 5 नमाज़ें पूरी करें।",
                category = "Spiritual Growth",
                iconKey = "prayer",
                colorHex = "#1B4332",
                scheduleType = "DAILY",
                targetDaysPerWeek = 7,
                targetDurationMinutes = 0,
                targetMetricGoal = "5 Daily Prayers",
                reminderEnabled = false,
                reminderHour = 5,
                reminderMinute = 30
            ),
            Habit(
                id = 2,
                name = "Fajr Se Din Shuru Karna",
                nameHindi = "फ़ज्र से दिन शुरू करना",
                description = "Build a consistent, peaceful morning routine starting early at Fajr.",
                descriptionHindi = "सुबह फ़ज्र के साथ शांत और अनुशासित दिन की शुरुआत करें।",
                category = "Morning Routine",
                iconKey = "sunrise",
                colorHex = "#D4A373",
                scheduleType = "DAILY",
                targetDaysPerWeek = 7,
                targetDurationMinutes = 0,
                targetMetricGoal = "Early Morning Start",
                reminderEnabled = false,
                reminderHour = 5,
                reminderMinute = 45
            ),
            Habit(
                id = 3,
                name = "Exercise Aur Gym",
                nameHindi = "एक्सरसाइज और जिम",
                description = "Consistent physical activity, gym workouts, or healthy exercise to stay strong.",
                descriptionHindi = "शारीरिक शक्ति और स्वास्थ्य के लिए नियमित व्यायाम या जिम।",
                category = "Health and Fitness",
                iconKey = "fitness",
                colorHex = "#2D6A4F",
                scheduleType = "DAILY",
                targetDaysPerWeek = 5,
                targetDurationMinutes = 30,
                targetMetricGoal = "30-45 mins workout",
                reminderEnabled = false,
                reminderHour = 17,
                reminderMinute = 30
            ),
            Habit(
                id = 4,
                name = "Book Reading",
                nameHindi = "किताब पढ़ना",
                description = "Develop a regular reading habit for continuous learning and focus.",
                descriptionHindi = "ज्ञान, शांति और एकाग्रता के लिए प्रतिदिन किताबें पढ़ें।",
                category = "Learning and Knowledge",
                iconKey = "book",
                colorHex = "#3A5A40",
                scheduleType = "DAILY",
                targetDaysPerWeek = 7,
                targetDurationMinutes = 20,
                targetMetricGoal = "20 mins / 10 pages",
                reminderEnabled = false,
                reminderHour = 21,
                reminderMinute = 0
            ),
            Habit(
                id = 5,
                name = "Finance Par Kaam Karna",
                nameHindi = "फाइनेंस पर काम करना",
                description = "Build financial knowledge, study budgeting, saving, and productive financial habits.",
                descriptionHindi = "बचत, बजट और वित्तीय ज्ञान को सीखने और बेहतर करने के लिए समय दें।",
                category = "Financial Education",
                iconKey = "finance",
                colorHex = "#588157",
                scheduleType = "DAILY",
                targetDaysPerWeek = 5,
                targetDurationMinutes = 15,
                targetMetricGoal = "Study concepts & budget",
                reminderEnabled = false,
                reminderHour = 20,
                reminderMinute = 0
            )
        )

        private class DatabaseCallback(private val context: Context) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                CoroutineScope(Dispatchers.IO).launch {
                    val database = getDatabase(context)
                    seedDefaultHabits(database.habitDao())
                }
            }
        }

        suspend fun seedDefaultHabits(habitDao: HabitDao) {
            val count = habitDao.getHabitCount()
            if (count == 0) {
                habitDao.insertHabits(DEFAULT_HABITS)
            }
        }
    }
}
