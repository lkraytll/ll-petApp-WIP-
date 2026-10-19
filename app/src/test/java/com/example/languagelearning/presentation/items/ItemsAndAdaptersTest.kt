package com.example.languagelearning.presentation.items

import org.junit.Assert.assertEquals
import org.junit.Test

class ItemsAndAdaptersTest {

    @Test
    fun testDayItemDataClass() {
        val item = DayItem(id = 1, dayName = "Mon", dayNumber = 15, isSelected = true)
        assertEquals(1, item.id)
        assertEquals("Mon", item.dayName)
        assertEquals(15, item.dayNumber)
        assertEquals(true, item.isSelected)
    }

    @Test
    fun testTaskItemDataClass() {
        val item = TaskItem(id = 1, title = "Complete Lesson 1", lesson = "Ingush 1", isComplete = true)
        assertEquals(1, item.id)
        assertEquals("Complete Lesson 1", item.title)
        assertEquals("Ingush 1", item.lesson)
        assertEquals(true, item.isComplete)
    }
}
