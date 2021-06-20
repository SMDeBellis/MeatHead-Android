package com.sdrockstarstudios.meatheadandroid;

import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import static androidx.test.espresso.intent.Intents.intended;
import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static androidx.test.espresso.matcher.ViewMatchers.*;


import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4ClassRunner.class)
public class MainMenuTest {
    @Rule
    public ActivityScenarioRule<MainMenuActivity> mActivityTestRule = new ActivityScenarioRule<>(MainMenuActivity.class);

    @Rule
    public IntentsTestRule<MainMenuActivity> mMainMenuActivityIntentsTestRule = new IntentsTestRule<>(MainMenuActivity.class);

    @Test
    public void mainMenuScreenHasAWorkoutsButton(){
        ViewInteraction workoutsButton = onView(allOf(withText("Workouts"), isDisplayed()));
        workoutsButton.check(matches(isDisplayed()));
    }

    @Test
    public void workoutsButtonLaunchesWorkoutLogMenuActivityIntent(){
        ViewInteraction workoutsButton = onView(allOf(withText("Workouts"), isDisplayed()));
        workoutsButton.perform(click());

        intended(hasComponent(WorkoutLogMenuActivity.class.getName()));
    }

    @Test
    public void mainMenuScreenHasAStatsButton(){
        ViewInteraction statsButton = onView(allOf(withText("Statistics"), isDisplayed()));
        statsButton.check(matches(isDisplayed()));
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
