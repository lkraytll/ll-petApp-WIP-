package com.example.languagelearning.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "user_preferences")

data class UserInfo(
    val id: Int,
    val name: String,
    val email: String,
)

data class GoalInfo(
    val targetDays: Int,
    val completedDays: Int,
    val streakDays: Int,
)

data class WeeklyStudyTime(
    val mon: Int,
    val tue: Int,
    val wed: Int,
    val thu: Int,
    val fri: Int,
    val sat: Int,
    val sun: Int,
)

data class UserLevelInfo(
    val xp: Int,
    val level: Int,
    val avatarUrl: String?,
)

@Singleton
open class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("jwt_token")
        private val USER_ID_KEY = intPreferencesKey("user_id")
        private val USER_NAME_KEY = stringPreferencesKey("user_name")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        private val GOAL_DAYS_KEY = intPreferencesKey("goal_days")
        private val COMPLETED_DAYS_KEY = intPreferencesKey("completed_days")
        private val STREAK_DAYS_KEY = intPreferencesKey("streak_days")
        private val STUDY_TIME_SECONDS_KEY = intPreferencesKey("study_time_seconds")
        private val USER_XP_KEY = intPreferencesKey("user_xp")
        private val AVATAR_URL_KEY = stringPreferencesKey("avatar_url")
        private val LAST_SAVED_WEEK_KEY = intPreferencesKey("last_saved_week")

        private val STUDY_TIME_MON = intPreferencesKey("study_time_mon")
        private val STUDY_TIME_TUE = intPreferencesKey("study_time_tue")
        private val STUDY_TIME_WED = intPreferencesKey("study_time_wed")
        private val STUDY_TIME_THU = intPreferencesKey("study_time_thu")
        private val STUDY_TIME_FRI = intPreferencesKey("study_time_fri")
        private val STUDY_TIME_SAT = intPreferencesKey("study_time_sat")
        private val STUDY_TIME_SUN = intPreferencesKey("study_time_sun")
    }

    open suspend fun saveUserInfo(
        userId: Int,
        userName: String,
        email: String,
    ) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId
            preferences[USER_NAME_KEY] = userName
            preferences[USER_EMAIL_KEY] = email
        }
    }
    open val tokenFlow: Flow<String?>
        get() = context.dataStore.data.map { preferences ->
            preferences[TOKEN_KEY]
        }

    open val userInfoFlow: Flow<UserInfo?>
        get() = context.dataStore.data.map { preferences ->
            val id = preferences[USER_ID_KEY] ?: return@map null
            val name = preferences[USER_NAME_KEY] ?: ""
            val email = preferences[USER_EMAIL_KEY] ?: ""
            UserInfo(id, name, email)
        }

    open val goalInfoFlow: Flow<GoalInfo>
        get() = context.dataStore.data.map { preferences ->
            GoalInfo(
                targetDays = preferences[GOAL_DAYS_KEY] ?: 6,
                completedDays = preferences[COMPLETED_DAYS_KEY] ?: 1,
                streakDays = preferences[STREAK_DAYS_KEY] ?: 6,
            )
        }

    open val studyTimeFlow: Flow<Int>
        get() = context.dataStore.data.map { preferences ->
            preferences[STUDY_TIME_SECONDS_KEY] ?: 0
        }

    open val weeklyStudyTimeFlow: Flow<WeeklyStudyTime>
        get() = context.dataStore.data.map { preferences ->
            WeeklyStudyTime(
                mon = preferences[STUDY_TIME_MON] ?: 0,
                tue = preferences[STUDY_TIME_TUE] ?: 0,
                wed = preferences[STUDY_TIME_WED] ?: 0,
                thu = preferences[STUDY_TIME_THU] ?: 0,
                fri = preferences[STUDY_TIME_FRI] ?: 0,
                sat = preferences[STUDY_TIME_SAT] ?: 0,
                sun = preferences[STUDY_TIME_SUN] ?: 0,
            )
        }

    open val userLevelFlow: Flow<UserLevelInfo>
        get() = context.dataStore.data.map { preferences ->
            val xp = preferences[USER_XP_KEY] ?: 100
            val level = (xp / 200) + 1
            val avatarUrl = preferences[AVATAR_URL_KEY]
            UserLevelInfo(xp = xp, level = level, avatarUrl = avatarUrl)
        }

    open suspend fun checkAndResetWeeklyProgress() {
        val cal = Calendar.getInstance()
        val currentWeekId = cal.get(Calendar.YEAR) * 100 + cal.get(Calendar.WEEK_OF_YEAR)

        context.dataStore.edit { preferences ->
            val lastWeekId = preferences[LAST_SAVED_WEEK_KEY] ?: currentWeekId
            if (currentWeekId > lastWeekId) {
                preferences[STUDY_TIME_MON] = 0
                preferences[STUDY_TIME_TUE] = 0
                preferences[STUDY_TIME_WED] = 0
                preferences[STUDY_TIME_THU] = 0
                preferences[STUDY_TIME_FRI] = 0
                preferences[STUDY_TIME_SAT] = 0
                preferences[STUDY_TIME_SUN] = 0
                preferences[LAST_SAVED_WEEK_KEY] = currentWeekId
            } else if (preferences[LAST_SAVED_WEEK_KEY] == null) {
                preferences[LAST_SAVED_WEEK_KEY] = currentWeekId
            }
        }
    }

    open suspend fun addXp(amount: Int) {
        if (amount <= 0) return
        context.dataStore.edit { preferences ->
            val currentXp = preferences[USER_XP_KEY] ?: 100
            preferences[USER_XP_KEY] = currentXp + amount
        }
    }

    open suspend fun saveAvatarUrl(url: String) {
        context.dataStore.edit { preferences ->
            preferences[AVATAR_URL_KEY] = url
        }
    }

    open suspend fun addStudyTimeSeconds(seconds: Int) {
        if (seconds <= 0) return
        val dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)

        context.dataStore.edit { preferences ->
            val total = preferences[STUDY_TIME_SECONDS_KEY] ?: 0
            preferences[STUDY_TIME_SECONDS_KEY] = total + seconds

            val key = when (dayOfWeek) {
                Calendar.MONDAY -> STUDY_TIME_MON
                Calendar.TUESDAY -> STUDY_TIME_TUE
                Calendar.WEDNESDAY -> STUDY_TIME_WED
                Calendar.THURSDAY -> STUDY_TIME_THU
                Calendar.FRIDAY -> STUDY_TIME_FRI
                Calendar.SATURDAY -> STUDY_TIME_SAT
                Calendar.SUNDAY -> STUDY_TIME_SUN
                else -> STUDY_TIME_THU
            }

            val current = preferences[key] ?: 0
            preferences[key] = current + seconds
        }
    }

    open suspend fun saveGoal(targetDays: Int) {
        context.dataStore.edit { preferences ->
            preferences[GOAL_DAYS_KEY] = targetDays
            val currentCompleted = preferences[COMPLETED_DAYS_KEY] ?: 1
            preferences[COMPLETED_DAYS_KEY] = currentCompleted
            val currentStreak = preferences[STREAK_DAYS_KEY] ?: 1
            preferences[STREAK_DAYS_KEY] = currentStreak
        }
    }

    open suspend fun saveSession(token: String, userId: Int, userName: String, email: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
            preferences[USER_ID_KEY] = userId
            preferences[USER_NAME_KEY] = userName
            preferences[USER_EMAIL_KEY] = email
        }
    }

    open suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    open suspend fun clearToken() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
