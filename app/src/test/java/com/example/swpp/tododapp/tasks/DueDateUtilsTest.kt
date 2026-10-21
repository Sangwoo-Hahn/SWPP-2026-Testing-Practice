package com.example.swpp.tododapp.tasks

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

/** Expected strings come from the feature intent and independent calendar arithmetic. */
class DueDateUtilsTest {
    @Test
    fun dueDateLabel_completedWithoutDueDate_returnsClear() {
        // GIVEN - Exact dates and completion state from the reviewed specification
        val dueDate = null
        val today = LocalDate.parse("2026-10-21")
        // WHEN - Build the label
        val result = dueDateLabel(dueDate, today, true)
        // THEN - Compare to a literal oracle, never another production calculation
        assertEquals("CLEAR", result)
    }

    @Test
    fun dueDateLabel_activeWithoutDueDate_returnsEmpty() {
        // GIVEN - Exact dates and completion state from the reviewed specification
        val dueDate = null
        val today = LocalDate.parse("2026-10-21")
        // WHEN - Build the label
        val result = dueDateLabel(dueDate, today, false)
        // THEN - Compare to a literal oracle, never another production calculation
        assertEquals("", result)
    }

    @Test
    fun dueDateLabel_wholeMonthAhead_returnsD31() {
        // GIVEN - Exact dates and completion state from the reviewed specification
        val dueDate = LocalDate.parse("2026-11-21")
        val today = LocalDate.parse("2026-10-21")
        // WHEN - Build the label
        val result = dueDateLabel(dueDate, today, false)
        // THEN - Compare to a literal oracle, never another production calculation
        assertEquals("D-31", result)
    }

    @Test
    fun dueDateLabel_wholeMonthPast_returnsDPlus30() {
        // GIVEN - Exact dates and completion state from the reviewed specification
        val dueDate = LocalDate.parse("2026-09-21")
        val today = LocalDate.parse("2026-10-21")
        // WHEN - Build the label
        val result = dueDateLabel(dueDate, today, false)
        // THEN - Compare to a literal oracle, never another production calculation
        assertEquals("D+30", result)
    }

    @Test
    fun dueDateLabel_today_returnsDDay() {
        // GIVEN - Exact dates and completion state from the reviewed specification
        val dueDate = LocalDate.parse("2026-10-21")
        val today = LocalDate.parse("2026-10-21")
        // WHEN - Build the label
        val result = dueDateLabel(dueDate, today, false)
        // THEN - Compare to a literal oracle, never another production calculation
        assertEquals("D-Day", result)
    }

    @Test
    fun dueDateLabel_yesterday_returnsDPlus1() {
        // GIVEN - Exact dates and completion state from the reviewed specification
        val dueDate = LocalDate.parse("2026-10-20")
        val today = LocalDate.parse("2026-10-21")
        // WHEN - Build the label
        val result = dueDateLabel(dueDate, today, false)
        // THEN - Compare to a literal oracle, never another production calculation
        assertEquals("D+1", result)
    }

    @Test
    fun dueDateLabel_tomorrow_returnsD1() {
        // GIVEN - Exact dates and completion state from the reviewed specification
        val dueDate = LocalDate.parse("2026-10-22")
        val today = LocalDate.parse("2026-10-21")
        // WHEN - Build the label
        val result = dueDateLabel(dueDate, today, false)
        // THEN - Compare to a literal oracle, never another production calculation
        assertEquals("D-1", result)
    }

    @Test
    fun dueDateLabel_yearBoundaryFuture_returnsD1() {
        // GIVEN - Exact dates and completion state from the reviewed specification
        val dueDate = LocalDate.parse("2027-01-01")
        val today = LocalDate.parse("2026-12-31")
        // WHEN - Build the label
        val result = dueDateLabel(dueDate, today, false)
        // THEN - Compare to a literal oracle, never another production calculation
        assertEquals("D-1", result)
    }

    @Test
    fun dueDateLabel_yearBoundaryPast_returnsDPlus1() {
        // GIVEN - Exact dates and completion state from the reviewed specification
        val dueDate = LocalDate.parse("2026-12-31")
        val today = LocalDate.parse("2027-01-01")
        // WHEN - Build the label
        val result = dueDateLabel(dueDate, today, false)
        // THEN - Compare to a literal oracle, never another production calculation
        assertEquals("D+1", result)
    }

    @Test
    fun dueDateLabel_wholeYearAhead_returnsD365() {
        // GIVEN - Exact dates and completion state from the reviewed specification
        val dueDate = LocalDate.parse("2027-10-21")
        val today = LocalDate.parse("2026-10-21")
        // WHEN - Build the label
        val result = dueDateLabel(dueDate, today, false)
        // THEN - Compare to a literal oracle, never another production calculation
        assertEquals("D-365", result)
    }

    @Test
    fun dueDateLabel_multipleMonthsAhead_returnsD63() {
        // GIVEN - Exact dates and completion state from the reviewed specification
        val dueDate = LocalDate.parse("2026-08-02")
        val today = LocalDate.parse("2026-05-31")
        // WHEN - Build the label
        val result = dueDateLabel(dueDate, today, false)
        // THEN - Compare to a literal oracle, never another production calculation
        assertEquals("D-63", result)
    }

    @Test
    fun dueDateLabel_leapDayForward_returnsD2() {
        // GIVEN - Exact dates and completion state from the reviewed specification
        val dueDate = LocalDate.parse("2024-03-01")
        val today = LocalDate.parse("2024-02-28")
        // WHEN - Build the label
        val result = dueDateLabel(dueDate, today, false)
        // THEN - Compare to a literal oracle, never another production calculation
        assertEquals("D-2", result)
    }

    @Test
    fun dueDateLabel_leapDayBackward_returnsDPlus2() {
        // GIVEN - Exact dates and completion state from the reviewed specification
        val dueDate = LocalDate.parse("2024-02-28")
        val today = LocalDate.parse("2024-03-01")
        // WHEN - Build the label
        val result = dueDateLabel(dueDate, today, false)
        // THEN - Compare to a literal oracle, never another production calculation
        assertEquals("D+2", result)
    }

    @Test
    fun dueDateLabel_leapDayAdjacent_returnsD1() {
        // GIVEN - Exact dates and completion state from the reviewed specification
        val dueDate = LocalDate.parse("2024-02-29")
        val today = LocalDate.parse("2024-02-28")
        // WHEN - Build the label
        val result = dueDateLabel(dueDate, today, false)
        // THEN - Compare to a literal oracle, never another production calculation
        assertEquals("D-1", result)
    }

    @Test
    fun dueDateLabel_nonLeapFebruary_returnsD1() {
        // GIVEN - Exact dates and completion state from the reviewed specification
        val dueDate = LocalDate.parse("2026-03-01")
        val today = LocalDate.parse("2026-02-28")
        // WHEN - Build the label
        val result = dueDateLabel(dueDate, today, false)
        // THEN - Compare to a literal oracle, never another production calculation
        assertEquals("D-1", result)
    }

    @Test
    fun dueDateLabel_monthBoundaryFuture_returnsD1() {
        // GIVEN - Exact dates and completion state from the reviewed specification
        val dueDate = LocalDate.parse("2026-11-01")
        val today = LocalDate.parse("2026-10-31")
        // WHEN - Build the label
        val result = dueDateLabel(dueDate, today, false)
        // THEN - Compare to a literal oracle, never another production calculation
        assertEquals("D-1", result)
    }

    @Test
    fun dueDateLabel_monthBoundaryPast_returnsDPlus1() {
        // GIVEN - Exact dates and completion state from the reviewed specification
        val dueDate = LocalDate.parse("2026-10-31")
        val today = LocalDate.parse("2026-11-01")
        // WHEN - Build the label
        val result = dueDateLabel(dueDate, today, false)
        // THEN - Compare to a literal oracle, never another production calculation
        assertEquals("D+1", result)
    }

    @Test
    fun dueDateLabel_completedWithDates_returnsClear() {
        // GIVEN - Completed tasks with past, current and future due dates
        val today = LocalDate.parse("2026-10-21")
        val dates = listOf("2026-10-20", "2026-10-21", "2026-10-22")
        dates.forEach { date ->
            // WHEN - Build each completed task's label
            val result = dueDateLabel(LocalDate.parse(date), today, true)
            // THEN - Completion takes priority in every case
            assertEquals("CLEAR", result)
        }
    }
}
