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

        private val STUDY_TIME_MON = intPreferencesKey("study_time_mon")
        private val STUDY_TIME_TUE = intPreferencesKey("study_time_tue")
        private val STUDY_TIME_WED = intPreferencesKey("study_time_wed")
        private val STUDY_TIME_THU = intPreferencesKey("study_time_thu")
        private val STUDY_TIME_FRI = intPreferencesKey("study_time_fri")
        private val STUDY_TIME_SAT = intPreferencesKey("study_time_sat")
        private val STUDY_TIME_SUN = intPreferencesKey("study_time_sun")
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
                mon = preferences[STUDY_TIME_MON] ?: 420,
                tue = preferences[STUDY_TIME_TUE] ?: 600,
                wed = preferences[STUDY_TIME_WED] ?: 330,
                thu = preferences[STUDY_TIME_THU] ?: 900,
                fri = preferences[STUDY_TIME_FRI] ?: 270,
                sat = preferences[STUDY_TIME_SAT] ?: 720,
                sun = preferences[STUDY_TIME_SUN] ?: 390,
            )
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
