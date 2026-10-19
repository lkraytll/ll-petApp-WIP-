package com.example.languagelearning.data

import com.example.languagelearning.data.local.GoalInfo
import com.example.languagelearning.data.local.UserInfo
import com.example.languagelearning.data.local.WeeklyStudyTime
import com.example.languagelearning.data.tts.AudioPlayer
import org.junit.Assert.assertEquals
import org.junit.Test

class DataLocalAndTtsTest {

    @Test
    fun testUserInfoDataClass() {
        val user = UserInfo(id = 42, name = "Ali Ingush", email = "ali@example.com")
        assertEquals(42, user.id)
        assertEquals("Ali Ingush", user.name)
        assertEquals("ali@example.com", user.email)
    }

    @Test
    fun testGoalInfoDataClass() {
        val goal = GoalInfo(targetDays = 6, completedDays = 2, streakDays = 6)
        assertEquals(6, goal.targetDays)
        assertEquals(2, goal.completedDays)
        assertEquals(6, goal.streakDays)
    }

    @Test
    fun testWeeklyStudyTimeDataClass() {
        val weekly = WeeklyStudyTime(
            mon = 100, tue = 200, wed = 300,
            thu = 400, fri = 500, sat = 600, sun = 700
        )
        assertEquals(100, weekly.mon)
        assertEquals(200, weekly.tue)
        assertEquals(300, weekly.wed)
        assertEquals(400, weekly.thu)
        assertEquals(500, weekly.fri)
        assertEquals(600, weekly.sat)
        assertEquals(700, weekly.sun)
    }

    @Test
    fun testAudioPlayerStopAndRelease() {
        val player = AudioPlayer()
        player.stop()
        player.release()
    }
}
