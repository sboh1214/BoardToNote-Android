package com.unitech.boardtonote.activity


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import com.unitech.boardtonote.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class CameraTest
{

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(CameraActivity::class.java)

    @Rule
    @JvmField
    var mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.CAMERA")!!

    @Test
    fun cameraTest()
    {
        val appCompatImageButton = onView(
                allOf(withId(R.id.Button_Picture), withContentDescription("Shutter Button"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(`is`("android.widget.RelativeLayout")),
                                        1),
                                1),
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

        val appCompatEditText = onView(
                allOf(withId(R.id.Edit_Title), withText("190829-100741"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.Toolbar_Edit),
                                        0),
                                0),
                        isDisplayed()))
        appCompatEditText.perform(replaceText("test"))

        val appCompatEditText2 = onView(
                allOf(withId(R.id.Edit_Title), withText("test"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.Toolbar_Edit),
                                        0),
                                0),
                        isDisplayed()))
        appCompatEditText2.perform(closeSoftKeyboard())

        val appCompatEditText3 = onView(
                allOf(withId(R.id.Edit_Title), withText("test"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.Toolbar_Edit),
                                        0),
                                0),
                        isDisplayed()))
        appCompatEditText3.perform(pressImeActionButton())

        val editText = onView(
                allOf(withId(R.id.Edit_Title), withText("test"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.Toolbar_Edit),
                                        1),
                                0),
                        isDisplayed()))
        editText.check(matches(withText("test")))

        val actionMenuItemView = onView(
                allOf(withId(R.id.Menu_Crop), withContentDescription("Share"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.Toolbar_Edit),
                                        3),
                                0),
                        isDisplayed()))
        actionMenuItemView.perform(click())

        val actionMenuItemView2 = onView(
                allOf(withId(R.id.Menu_Crop), withContentDescription("Share"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.Toolbar_Edit),
                                        3),
                                0),
                        isDisplayed()))
        actionMenuItemView2.perform(click())

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

        val tabView = onView(
                allOf(withContentDescription("Cloud"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(`is`("com.google.android.material.tabs.TabLayout")),
                                        0),
                                1),
                        isDisplayed()))
        tabView.perform(click())

        val tabView2 = onView(
                allOf(withContentDescription("Local"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(`is`("com.google.android.material.tabs.TabLayout")),
                                        0),
                                0),
                        isDisplayed()))
        tabView2.perform(click())
    }

    private fun childAtPosition(
            parentMatcher: Matcher<View>, position: Int): Matcher<View>
    {

        return object : TypeSafeMatcher<View>()
        {
            override fun describeTo(description: Description)
            {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean
            {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
