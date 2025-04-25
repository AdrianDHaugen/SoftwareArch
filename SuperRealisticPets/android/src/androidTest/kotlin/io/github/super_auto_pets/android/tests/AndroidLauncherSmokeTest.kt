package io.github.super_auto_pets.android.tests

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
//import androidx.test.rule.GrantPermissionRule
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import io.github.super_auto_pets.android.AndroidLauncher
//import io.github.super_auto_pets.android.R
//import io.github.super_auto_pets.android.firebase.FirebaseHighscoreService
import org.hamcrest.CoreMatchers.containsString
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class AndroidLauncherSmokeTest {
    @Test
    fun launcherStartsUp() {
        // simply launching the AndroidLauncher should not crash
        ActivityScenario.launch(AndroidLauncher::class.java)
    }
}
