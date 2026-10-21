package com.yezhennan.exp04

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Test
    fun testEditInfoFlow() {
        ActivityScenario.launch(MainActivity::class.java).use {

            onView(withId(R.id.btnEdit))
                .check(matches(isDisplayed()))
                .perform(click())

            onView(withId(R.id.etName)).perform(replaceText("王五"))
            onView(withId(R.id.etPhone)).perform(replaceText("99999999"))
            onView(withId(R.id.rbFemale)).perform(click())
            onView(withId(R.id.btnSave)).perform(click())

            // 回到 MainActivity 后应显示更新后的信息
            onView(withId(R.id.tvName)).check(matches(withText("姓名：王五")))
            onView(withId(R.id.tvGender)).check(matches(withText("性别：女")))
            onView(withId(R.id.tvPhone)).check(matches(withText("电话：99999999")))
        }
    }

    @Test
    fun testMainElementsDisplayed() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.tvName)).check(matches(isDisplayed()))
            onView(withId(R.id.tvGender)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPhone)).check(matches(isDisplayed()))
            onView(withId(R.id.tvHighScore)).check(matches(isDisplayed()))
        }
    }
}