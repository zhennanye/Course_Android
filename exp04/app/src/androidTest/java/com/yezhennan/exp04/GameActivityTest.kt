package com.yezhennan.exp04

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GameActivityTest {

    @Test
    fun testGameViewDisplayed() {
        ActivityScenario.launch(GameActivity::class.java).use {
            onView(withId(R.id.tvScore)).check(matches(isDisplayed()))
            onView(withId(R.id.tvHighScore)).check(matches(isDisplayed()))
            onView(withId(R.id.btnRestart)).check(matches(isDisplayed()))
            onView(withId(R.id.gridCards)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun testRestartButton() {
        ActivityScenario.launch(GameActivity::class.java).use {
            onView(withId(R.id.btnRestart)).perform(click())
            onView(withId(R.id.tvScore)).check(matches(isDisplayed()))
        }
    }
}