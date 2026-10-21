package com.example.swpp.tododapp.statistics

import com.example.swpp.tododapp.data.Task
import org.junit.Assert.assertEquals
import org.junit.Test

class StatisticsUtilsTest {

    @Test
    fun getActiveAndCompletedStats_two_return50f50f() {
        // GIVEN - One completed task and one active task
        val tasks = listOf(Task("Done", "Description", true, "done"),
            Task("Active", "Description", false, "active"))
        // WHEN - Compute the statistics
        val result = getActiveAndCompletedStats(tasks)
        // THEN - Both percentages are 50%
        assertEquals(50f, result.activeTasksPercent)
        assertEquals(50f, result.completedTasksPercent)
    }

    @Test
    fun getActiveAndCompletedStats_zero_return0f0f() {
        // GIVEN - No tasks
        val tasks = emptyList<Task>()
        // WHEN - Compute the statistics
        val result = getActiveAndCompletedStats(tasks)
        // THEN - Neither category contains a task
        assertEquals(0f, result.activeTasksPercent)
        assertEquals(0f, result.completedTasksPercent)
    }

    @Test
    fun getActiveAndCompletedStats_allCompleted_return0f100f() {
        // GIVEN - Two completed tasks
        val tasks = listOf(Task("First", "Description", true, "first"),
            Task("Second", "Description", true, "second"))
        // WHEN - Compute the statistics
        val result = getActiveAndCompletedStats(tasks)
        // THEN - Every task is completed
        assertEquals(0f, result.activeTasksPercent)
        assertEquals(100f, result.completedTasksPercent)
    }
}
