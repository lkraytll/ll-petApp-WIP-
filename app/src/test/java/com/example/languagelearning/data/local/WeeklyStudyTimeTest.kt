package com.example.languagelearning.data.local

import org.junit.Assert.assertEquals
import org.junit.Test

class WeeklyStudyTimeTest {

    @Test
    fun testWeeklyStudyTimeInitialization() {
        val weekly = WeeklyStudyTime(
            mon = 420,
            tue = 600,
            wed = 330,
            thu = 900,
            fri = 270,
            sat = 720,
            sun = 390
        )

        assertEquals(420, weekly.mon)
        assertEquals(600, weekly.tue)
        assertEquals(330, weekly.wed)
        assertEquals(900, weekly.thu)
        assertEquals(270, weekly.fri)
        assertEquals(720, weekly.sat)
        assertEquals(390, weekly.sun)
    }

    @Test
    fun testGoalInfoLockStateLogic() {
        val lockedGoal = GoalInfo(targetDays = 6, completedDays = 1, streakDays = 6)
        val isLocked = lockedGoal.completedDays < lockedGoal.targetDays
        assertEquals(true, isLocked)

        val unlockedGoal = GoalInfo(targetDays = 6, completedDays = 6, streakDays = 6)
        val isUnlocked = unlockedGoal.completedDays < unlockedGoal.targetDays
        assertEquals(false, isUnlocked)
    }
}
