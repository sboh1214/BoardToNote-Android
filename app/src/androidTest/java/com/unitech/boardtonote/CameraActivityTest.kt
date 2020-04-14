package com.unitech.boardtonote


import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import com.unitech.boardtonote.activity.CameraActivity
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4ClassRunner::class)
class CameraActivityTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(CameraActivity::class.java)

    @Rule
    @JvmField
    var mGrantPermissionRule: GrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.CAMERA")

    @Test
    fun cameraActivityTest() {
        val appCompatImageButton = onView(
                allOf(withId(R.id.Button_Picture), withContentDescription("Shutter Button"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()))
        appCompatImageButton.perform(click())

        val imageView = onView(
                allOf(withId(R.id.Image_OriPic),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.Frame_Edit),
                                        0),
                                0),
                        isDisplayed()))
        imageView.check(matches(isDisplayed()))

        val editText = onView(
                allOf(withId(R.id.Edit_Title),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.Toolbar_Edit),
                                        1),
                                0),
                        isDisplayed()))
        editText.check(matches(isDisplayed()))

        val appCompatImageButton2 = onView(
                allOf(withContentDescription("Navigate up"),
                        childAtPosition(
                                allOf(withId(R.id.Toolbar_Edit),
                                        childAtPosition(
                                                withClassName(`is`("com.google.android.material.appbar.AppBarLayout")),
                                                0)),
                                2),
                        isDisplayed()))
        appCompatImageButton2.perform(click())

        val recyclerView = onView(
                allOf(withId(R.id.Recycler_Local),
                        childAtPosition(
                                withClassName(`is`("android.widget.LinearLayout")),
                                0)))
        recyclerView.perform(RecyclerViewActions.actionOnItemAtPosition<ViewHolder>(0, click()))

        val appCompatImageButton3 = onView(
                allOf(withContentDescription("Navigate up"),
                        childAtPosition(
                                allOf(withId(R.id.Toolbar_Edit),
                                        childAtPosition(
                                                withClassName(`is`("com.google.android.material.appbar.AppBarLayout")),
                                                0)),
                                2),
                        isDisplayed()))
        appCompatImageButton3.perform(click())

        val appCompatImageButton4 = onView(
                allOf(withId(R.id.Button_Main_More), withContentDescription("More Button"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(`is`("androidx.cardview.widget.CardView")),
                                        0),
                                0),
                        isDisplayed()))
        appCompatImageButton4.perform(click())

        val appCompatButton = onView(
                allOf(withId(R.id.Button_Delete), withText("Delete"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.design_bottom_sheet),
                                        0),
                                5),
                        isDisplayed()))
        appCompatButton.perform(click())

        val recyclerView2 = onView(
                allOf(withId(R.id.Recycler_Local),
                        childAtPosition(
                                withParent(withId(R.id.pager)),
                                0),
                        isDisplayed()))
        recyclerView2.check(matches(isDisplayed()))
    }

    private fun childAtPosition(
            parentMatcher: Matcher<View>, position: Int): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
