package com.example.swpp.tododapp.taskdetail

import androidx.test.filters.MediumTest
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.example.swpp.tododapp.R
import com.example.swpp.tododapp.data.Task
import org.junit.Test
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.example.swpp.tododapp.ServiceLocator
import com.example.swpp.tododapp.data.source.FakeAndroidTestRepository
import com.example.swpp.tododapp.data.source.TasksRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import kotlinx.coroutines.test.runTest
import org.hamcrest.core.IsNot.not


@MediumTest
@RunWith(AndroidJUnit4::class)
class TaskDetailFragmentTest {
    private lateinit var repository: TasksRepository

    @Before
    fun initRepository() {
        repository = FakeAndroidTestRepository()
        ServiceLocator.tasksRepository = repository
    }

    @After
    fun cleanupDb() = runTest {
        ServiceLocator.resetRepository()
    }

    @Test
    fun activeTaskDetails_DisplayedInUi() = runTest {
        // GIVEN - Add active task to the DB
        val task = Task("Task Title", "Task Description", false, "details-active")
        repository.saveTask(task)
        // WHEN - Details fragment launched to display task
        val args = TaskDetailFragmentArgs(task.id).toBundle()
        launchFragmentInContainer<TaskDetailFragment>(args, R.style.AppTheme).use {
            // THEN - The exact title, description and unchecked status are visible
            onView(withId(R.id.task_detail_title_text)).check(matches(isDisplayed()))
                .check(matches(withText("Task Title")))
            onView(withId(R.id.task_detail_description_text)).check(matches(isDisplayed()))
                .check(matches(withText("Task Description")))
            onView(withId(R.id.task_detail_complete_checkbox)).check(matches(isDisplayed()))
                .check(matches(not(isChecked())))
        }
    }
    }
