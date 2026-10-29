package com.example.habitrise.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitrise.data.local.HabitRiseDatabase
import com.example.habitrise.data.model.ExerciseSession
import com.example.habitrise.data.model.FinanceSession
import com.example.habitrise.data.model.Habit
import com.example.habitrise.data.model.HabitCompletion
import com.example.habitrise.data.model.JournalEntry
import com.example.habitrise.data.model.PrayerRecord
import com.example.habitrise.data.model.ReadingSession
import com.example.habitrise.data.repository.HabitRepository
import com.example.habitrise.data.repository.HabitStreakInfo
import com.example.habitrise.data.repository.OverallStreakStats
import com.example.habitrise.notification.NotificationHelper
import com.example.habitrise.notification.ReminderScheduler
import com.example.habitrise.util.DateUtils
import com.example.habitrise.util.ThemeMode
import com.example.habitrise.util.UserPreferencesRepository
import com.example.habitrise.util.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class MainTab {
    HOME, TRACKER, INSIGHTS, LEARN, PROFILE
}

data class HabitUiState(
    val currentTab: MainTab = MainTab.HOME,
    val selectedHabitDetailId: Long? = null,
    val habits: List<Habit> = emptyList(),
    val todayDate: String = DateUtils.getTodayDate(),
    val selectedDate: String = DateUtils.getTodayDate(),
    val todayCompletions: Set<Long> = emptySet(), // Set of habit IDs completed today
    val selectedDateCompletions: Set<Long> = emptySet(),
    val streaksMap: Map<Long, HabitStreakInfo> = emptyMap(),
    val overallStats: OverallStreakStats = OverallStreakStats(),
    val userSettings: UserSettings = UserSettings(),
    val currentPrayerRecord: PrayerRecord? = null,
    val currentJournalEntry: JournalEntry? = null,
    val totalReadingMinutes: Int = 0,
    val totalPagesRead: Int = 0,
    val totalExerciseMinutes: Int = 0,
    val totalExerciseCount: Int = 0,
    val totalFinanceMinutes: Int = 0,
    val totalFinanceCount: Int = 0,
    val isAddEditHabitDialogOpen: Boolean = false,
    val habitToEdit: Habit? = null,
    val habitToDelete: Habit? = null,
    val isResetConfirmOpen: Boolean = false,
    val isExportDialogOpen: Boolean = false,
    val exportedJsonString: String = "",
    val activeLogSessionHabit: Habit? = null // For logging reading, exercise, or finance session
)

class HabitViewModel(application: Application) : AndroidViewModel(application) {
    private val database = HabitRiseDatabase.getDatabase(application)
    private val repository = HabitRepository(database)
    private val preferencesRepository = UserPreferencesRepository(application)

    private val _uiState = MutableStateFlow(HabitUiState())
    val uiState: StateFlow<HabitUiState> = _uiState.asStateFlow()

    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage: SharedFlow<String> = _snackbarMessage.asSharedFlow()

    init {
        NotificationHelper.createNotificationChannel(application)
        viewModelScope.launch {
            repository.ensureDefaultHabits()
        }
        observeData()
    }

    private fun observeData() {
        // Collect Habits & today completions
        viewModelScope.launch {
            combine(
                repository.activeHabits,
                repository.getCompletionsForDate(_uiState.value.todayDate),
                preferencesRepository.userSettingsFlow
            ) { habits, todayCompletionsList, settings ->
                val completedIds = todayCompletionsList.map { it.habitId }.toSet()
                Triple(habits, completedIds, settings)
            }.collectLatest { (habits, completedIds, settings) ->
                _uiState.value = _uiState.value.copy(
                    habits = habits,
                    todayCompletions = completedIds,
                    userSettings = settings
                )
                refreshStreaksAndStats(habits)
            }
        }

        // Observe prayer records for selected date
        viewModelScope.launch {
            repository.getPrayerRecord(_uiState.value.selectedDate).collectLatest { record ->
                _uiState.value = _uiState.value.copy(currentPrayerRecord = record)
            }
        }

        // Observe journal entries for selected date
        viewModelScope.launch {
            repository.getJournalEntryForDate(_uiState.value.selectedDate).collectLatest { entry ->
                _uiState.value = _uiState.value.copy(currentJournalEntry = entry)
            }
        }

        // Observe session aggregates
        viewModelScope.launch {
            repository.getTotalReadingMinutes().collectLatest { mins ->
                _uiState.value = _uiState.value.copy(totalReadingMinutes = mins)
            }
        }
        viewModelScope.launch {
            repository.getTotalPagesRead().collectLatest { pages ->
                _uiState.value = _uiState.value.copy(totalPagesRead = pages)
            }
        }
        viewModelScope.launch {
            repository.getTotalExerciseMinutes().collectLatest { mins ->
                _uiState.value = _uiState.value.copy(totalExerciseMinutes = mins)
            }
        }
        viewModelScope.launch {
            repository.getTotalExerciseSessionsCount().collectLatest { count ->
                _uiState.value = _uiState.value.copy(totalExerciseCount = count)
            }
        }
        viewModelScope.launch {
            repository.getTotalFinanceMinutes().collectLatest { mins ->
                _uiState.value = _uiState.value.copy(totalFinanceMinutes = mins)
            }
        }
        viewModelScope.launch {
            repository.getTotalFinanceSessionsCount().collectLatest { count ->
                _uiState.value = _uiState.value.copy(totalFinanceCount = count)
            }
        }
    }

    private fun refreshStreaksAndStats(habits: List<Habit>) {
        viewModelScope.launch {
            val streaks = mutableMapOf<Long, HabitStreakInfo>()
            for (h in habits) {
                streaks[h.id] = repository.calculateStreak(h.id)
            }
            val overall = repository.getOverallStats(_uiState.value.todayDate)
            _uiState.value = _uiState.value.copy(
                streaksMap = streaks,
                overallStats = overall
            )
        }
    }

    fun selectTab(tab: MainTab) {
        _uiState.value = _uiState.value.copy(currentTab = tab, selectedHabitDetailId = null)
    }

    fun openHabitDetail(habitId: Long) {
        _uiState.value = _uiState.value.copy(selectedHabitDetailId = habitId)
    }

    fun closeHabitDetail() {
        _uiState.value = _uiState.value.copy(selectedHabitDetailId = null)
    }

    fun setSelectedDate(date: String) {
        _uiState.value = _uiState.value.copy(selectedDate = date)
        viewModelScope.launch {
            val list = repository.getCompletionsForDate(date)
            list.collectLatest { completions ->
                val set = completions.map { it.habitId }.toSet()
                _uiState.value = _uiState.value.copy(selectedDateCompletions = set)
            }
        }
    }

    fun toggleHabitCompletion(habitId: Long, date: String = _uiState.value.todayDate) {
        viewModelScope.launch {
            val markedComplete = repository.toggleHabitCompletion(habitId, date)
            val isHindi = _uiState.value.userSettings.isHindi
            val msg = if (markedComplete) {
                if (isHindi) "बहुत खूब! आदत पूरी हुई।" else "Great job! Habit completed."
            } else {
                if (isHindi) "आदत लंबित के रूप में चिह्नित।" else "Habit marked as pending."
            }
            _snackbarMessage.emit(msg)
            refreshStreaksAndStats(_uiState.value.habits)
        }
    }

    fun updatePrayer(
        fajr: Boolean? = null,
        dhuhr: Boolean? = null,
        asr: Boolean? = null,
        maghrib: Boolean? = null,
        isha: Boolean? = null
    ) {
        viewModelScope.launch {
            repository.updatePrayerStatus(
                date = _uiState.value.selectedDate,
                fajr = fajr,
                dhuhr = dhuhr,
                asr = asr,
                maghrib = maghrib,
                isha = isha
            )
            refreshStreaksAndStats(_uiState.value.habits)
        }
    }

    fun openAddHabitDialog() {
        _uiState.value = _uiState.value.copy(isAddEditHabitDialogOpen = true, habitToEdit = null)
    }

    fun openEditHabitDialog(habit: Habit) {
        _uiState.value = _uiState.value.copy(isAddEditHabitDialogOpen = true, habitToEdit = habit)
    }

    fun closeAddEditHabitDialog() {
        _uiState.value = _uiState.value.copy(isAddEditHabitDialogOpen = false, habitToEdit = null)
    }

    fun saveHabit(habit: Habit) {
        viewModelScope.launch {
            if (habit.id == 0L) {
                val newId = repository.insertHabit(habit)
                if (habit.reminderEnabled) {
                    ReminderScheduler.scheduleReminder(getApplication(), habit.copy(id = newId))
                }
            } else {
                repository.updateHabit(habit)
                if (habit.reminderEnabled) {
                    ReminderScheduler.scheduleReminder(getApplication(), habit)
                } else {
                    ReminderScheduler.cancelReminder(getApplication(), habit.id)
                }
            }
            closeAddEditHabitDialog()
            refreshStreaksAndStats(_uiState.value.habits)
        }
    }

    fun promptDeleteHabit(habit: Habit) {
        _uiState.value = _uiState.value.copy(habitToDelete = habit)
    }

    fun dismissDeleteDialog() {
        _uiState.value = _uiState.value.copy(habitToDelete = null)
    }

    fun confirmDeleteHabit() {
        val habit = _uiState.value.habitToDelete ?: return
        viewModelScope.launch {
            ReminderScheduler.cancelReminder(getApplication(), habit.id)
            repository.deleteHabit(habit)
            _uiState.value = _uiState.value.copy(
                habitToDelete = null,
                selectedHabitDetailId = if (_uiState.value.selectedHabitDetailId == habit.id) null else _uiState.value.selectedHabitDetailId
            )
            refreshStreaksAndStats(_uiState.value.habits)
        }
    }

    fun archiveHabit(habitId: Long, archived: Boolean) {
        viewModelScope.launch {
            repository.archiveHabit(habitId, archived)
            refreshStreaksAndStats(_uiState.value.habits)
        }
    }

    // Sessions
    fun openLogSessionDialog(habit: Habit) {
        _uiState.value = _uiState.value.copy(activeLogSessionHabit = habit)
    }

    fun closeLogSessionDialog() {
        _uiState.value = _uiState.value.copy(activeLogSessionHabit = null)
    }

    fun logReadingSession(habitId: Long, bookTitle: String, pagesRead: Int, durationMins: Int, notes: String) {
        viewModelScope.launch {
            val session = ReadingSession(
                habitId = habitId,
                date = _uiState.value.selectedDate,
                bookTitle = bookTitle,
                pagesRead = pagesRead,
                durationMinutes = durationMins,
                notes = notes
            )
            repository.insertReadingSession(session)
            closeLogSessionDialog()
            refreshStreaksAndStats(_uiState.value.habits)
            _snackbarMessage.emit("Reading session saved!")
        }
    }

    fun logExerciseSession(habitId: Long, activityType: String, durationMins: Int, notes: String) {
        viewModelScope.launch {
            val session = ExerciseSession(
                habitId = habitId,
                date = _uiState.value.selectedDate,
                activityType = activityType,
                durationMinutes = durationMins,
                notes = notes
            )
            repository.insertExerciseSession(session)
            closeLogSessionDialog()
            refreshStreaksAndStats(_uiState.value.habits)
            _snackbarMessage.emit("Exercise session saved!")
        }
    }

    fun logFinanceSession(habitId: Long, topic: String, durationMins: Int, keyLesson: String) {
        viewModelScope.launch {
            val session = FinanceSession(
                habitId = habitId,
                date = _uiState.value.selectedDate,
                topic = topic,
                durationMinutes = durationMins,
                keyLesson = keyLesson
            )
            repository.insertFinanceSession(session)
            closeLogSessionDialog()
            refreshStreaksAndStats(_uiState.value.habits)
            _snackbarMessage.emit("Finance session saved!")
        }
    }

    fun saveJournal(wentWell: String, madeDifficult: String, improveTomorrow: String) {
        viewModelScope.launch {
            val entry = JournalEntry(
                date = _uiState.value.selectedDate,
                wentWell = wentWell,
                madeDifficult = madeDifficult,
                improveTomorrow = improveTomorrow
            )
            repository.saveJournalEntry(entry)
            val msg = if (_uiState.value.userSettings.isHindi) "चिंतन सुरक्षित रूप से सहेजा गया।" else "Reflection saved locally & securely."
            _snackbarMessage.emit(msg)
        }
    }

    // Settings
    fun setLanguage(code: String) {
        viewModelScope.launch {
            preferencesRepository.setLanguage(code)
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            preferencesRepository.setThemeMode(mode)
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setNotificationsEnabled(enabled)
        }
    }

    fun sendTestNotification() {
        NotificationHelper.showHabitNotification(
            getApplication(),
            9999,
            "HabitRise Daily Reminder",
            "Consistency grows with practice. Take a moment to complete your habits today!"
        )
    }

    fun openExportDialog() {
        viewModelScope.launch {
            val json = repository.exportDataJson()
            _uiState.value = _uiState.value.copy(isExportDialogOpen = true, exportedJsonString = json)
        }
    }

    fun closeExportDialog() {
        _uiState.value = _uiState.value.copy(isExportDialogOpen = false, exportedJsonString = "")
    }

    fun openResetDialog() {
        _uiState.value = _uiState.value.copy(isResetConfirmOpen = true)
    }

    fun closeResetDialog() {
        _uiState.value = _uiState.value.copy(isResetConfirmOpen = false)
    }

    fun confirmResetAllData() {
        viewModelScope.launch {
            repository.resetAllData()
            closeResetDialog()
            _snackbarMessage.emit("All data has been reset to defaults.")
        }
    }

    // Access to repository for screen-specific Flows
    fun getReadingSessionsForHabit(habitId: Long): Flow<List<ReadingSession>> = repository.getReadingSessions(habitId)
    fun getExerciseSessionsForHabit(habitId: Long): Flow<List<ExerciseSession>> = repository.getExerciseSessions(habitId)
    fun getFinanceSessionsForHabit(habitId: Long): Flow<List<FinanceSession>> = repository.getFinanceSessions(habitId)
    fun getCompletionsForHabit(habitId: Long): Flow<List<HabitCompletion>> = repository.getCompletionsForHabit(habitId)
    fun getCompletionsBetween(startDate: String, endDate: String): Flow<List<HabitCompletion>> = repository.getCompletionsBetween(startDate, endDate)
}
