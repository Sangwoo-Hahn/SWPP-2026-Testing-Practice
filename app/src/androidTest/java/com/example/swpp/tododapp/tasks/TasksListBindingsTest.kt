package com.example.swpp.tododapp.tasks

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.swpp.tododapp.R
import com.example.swpp.tododapp.ServiceLocator
import com.example.swpp.tododapp.data.Task
import com.example.swpp.tododapp.data.source.FakeAndroidTestRepository
import java.time.LocalDate
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assume.assumeTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/** Exercises the real task row and binding on Android, with a controlled repository. */
@MediumTest
@RunWith(AndroidJUnit4::class)
class TasksListBindingsTest {
    private lateinit var repository: FakeAndroidTestRepository
    private lateinit var today: LocalDate

    @Before
    fun setUp() {
        repository = FakeAndroidTestRepository()
        ServiceLocator.tasksRepository = repository
        today = LocalDate.now()
    }

    @After
    fun tearDown() {
        ServiceLocator.resetRepository()
    }

    private fun task(completed: Boolean = false, due: LocalDate? = null) =
        Task("Spec task", "Binding verification", completed, "due-spec", due)

    private fun withRow(initial: Task, action: (FragmentScenario<TasksFragment>) -> Unit) {
        runBlocking { repository.saveTask(initial) }
        launchFragmentInContainer<TasksFragment>(Bundle(), R.style.AppTheme).use { scenario ->
            onView(withId(R.id.title_text)).check(matches(withText("Spec task")))
            action(scenario)
        }
    }

    private fun bind(scenario: FragmentScenario<TasksFragment>, current: Task) {
        scenario.onFragment {
            val label = it.requireView().findViewById<TextView>(R.id.due_date_text)
            setDueDateLabel(label, current)
        }
    }

    private fun assertLabel(expected: String) {
        // Avoid a date-boundary race; never skip based on the observed label or bug.
        assumeTrue("Device date changed during the test", today == LocalDate.now())
        onView(withId(R.id.due_date_text)).check(matches(withText(expected)))
        if (expected.isNotEmpty()) {
            onView(withId(R.id.due_date_text)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun setDueDateLabel_completedWithoutDueDate_displaysClear() {
        // GIVEN - A completed undated task in the repository
        withRow(task(completed = true)) {
            // WHEN - The real row is bound by the fragment
            // THEN - Completion is shown
            assertLabel("CLEAR")
        }
    }

    @Test
    fun setDueDateLabel_activeWithoutDueDate_hidesEmptyLabel() {
        // GIVEN - An active task with no due date
        withRow(task()) {
            // WHEN - The row is first bound
            // THEN - No label text is present (no layout-visibility policy is imposed)
            assertLabel("")
        }
    }

    @Test
    fun setDueDateLabel_dueToday_displaysDDay() {
        // GIVEN - A task due on the captured device date
        withRow(task(due = today)) {
            // WHEN - The real row is bound
            // THEN - It is due today
            assertLabel("D-Day")
        }
    }

    @Test
    fun setDueDateLabel_wholeMonthAhead_displaysD31() {
        // GIVEN - Exactly 31 calendar days, independent of today's month length
        withRow(task(due = today.plusDays(31))) {
            // WHEN - The real row is bound
            // THEN - Whole months must not disappear from the count
            assertLabel("D-31")
        }
    }

    @Test
    fun setDueDateLabel_uncheckWithoutDueDate_clearsPreviousClear() {
        // GIVEN - The row displays CLEAR for a completed undated task
        withRow(task(completed = true)) {
            assertLabel("CLEAR")
            // WHEN - Espresso unchecks the real checkbox
            onView(withId(R.id.complete_checkbox)).perform(click())
            // THEN - The previous CLEAR text is removed
            assertLabel("")
        }
    }

    @Test
    fun setDueDateLabel_checkWithoutDueDate_displaysClear() {
        // GIVEN - An active undated task has no label
        withRow(task()) {
            assertLabel("")
            // WHEN - Espresso checks the real checkbox
            onView(withId(R.id.complete_checkbox)).perform(click())
            // THEN - Completion is displayed
            assertLabel("CLEAR")
        }
    }

    @Test
    fun setDueDateLabel_removeDueDate_clearsPreviousDday() {
        // GIVEN - A row showing tomorrow's D-day label
        withRow(task(due = today.plusDays(1))) { scenario ->
            assertLabel("D-1")
            // WHEN - The same TextView is rebound with the due date removed
            bind(scenario, task())
            // THEN - The previous D-day text is cleared
            assertLabel("")
        }
    }

    @Test
    fun setDueDateLabel_addDueDate_displaysDday() {
        // GIVEN - A row for an active undated task
        withRow(task()) { scenario ->
            assertLabel("")
            // WHEN - The same view receives a due date
            bind(scenario, task(due = today.plusDays(1)))
            // THEN - The new label is displayed
            assertLabel("D-1")
        }
    }

    @Test
    fun setDueDateLabel_uncheckWithDueDate_restoresDday() {
        // GIVEN - A completed dated task shows CLEAR
        withRow(task(completed = true, due = today.plusDays(1))) {
            assertLabel("CLEAR")
            // WHEN - Espresso unchecks the task
            onView(withId(R.id.complete_checkbox)).perform(click())
            // THEN - Its due-date label replaces CLEAR
            assertLabel("D-1")
        }
    }

    @Test
    fun setDueDateLabel_yesterday_displaysDPlus1() {
        // GIVEN - A task due one calendar day ago
        withRow(task(due = today.minusDays(1))) {
            // WHEN - The row is bound
            // THEN - It is one day overdue
            assertLabel("D+1")
        }
    }

    @Test
    fun setDueDateLabel_tomorrow_displaysD1() {
        // GIVEN - A task due one calendar day in the future
        withRow(task(due = today.plusDays(1))) {
            // WHEN - The row is bound
            // THEN - One day remains
            assertLabel("D-1")
        }
    }

    @Test
    fun setDueDateLabel_repeatedEmptyBinding_remainsEmpty() {
        // GIVEN - A recycled row has stale text
        withRow(task()) { scenario ->
            scenario.onFragment {
                it.requireView().findViewById<TextView>(R.id.due_date_text).apply {
                    text = "stale"
                    visibility = View.VISIBLE
                }
            }
            repeat(2) {
                // WHEN - The empty-label task is bound repeatedly to the same view
                bind(scenario, task())
                // THEN - Each binding removes any previous text
                assertLabel("")
            }
        }
    }

    @Test
    fun setDueDateLabel_completedWithDueDate_displaysClear() {
        // GIVEN - A completed task also has a future due date
        withRow(task(completed = true, due = today.plusDays(1))) {
            // WHEN - The row is bound
            // THEN - Completion has priority over D-1
            assertLabel("CLEAR")
        }
    }

    @Test
    fun setDueDateLabel_checkWithDueDate_replacesDdayWithClear() {
        // GIVEN - An active dated task shows D-1
        withRow(task(due = today.plusDays(1))) {
            assertLabel("D-1")
            // WHEN - Espresso checks the real checkbox
            onView(withId(R.id.complete_checkbox)).perform(click())
            // THEN - CLEAR replaces the previous label
            assertLabel("CLEAR")
        }
    }
}
