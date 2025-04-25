package io.github.super_auto_pets.android.tests

import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.firestore.FirebaseFirestore
import io.github.super_auto_pets.android.AndroidLauncher
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AndroidLauncherIntegrationTest {

    private lateinit var scenario: ActivityScenario<AndroidLauncher>

    @Before
    fun setUp() {
        scenario = ActivityScenario.launch(AndroidLauncher::class.java)
    }

    @After
    fun tearDown() {
        scenario.close()
    }

    @Test
    fun rotationDoesNotCrash() {
        scenario.recreate()
        assertEquals(
            "Should resume cleanly after recreate",
            Lifecycle.State.RESUMED,
            scenario.state
        )
    }

    @Test
    fun activityCanBeFinished() {
        // just exercise finish() so it never throws
        scenario.onActivity { it.finish() }
    }

    @Test
    fun decorViewExists() {
        scenario.onActivity { activity ->
            val decor = activity.window.decorView
            assertNotNull("Window decorView should exist", decor)
        }
    }

    @Test
    fun firestorePersistenceIsOn() {
        val persisted = FirebaseFirestore.getInstance()
            .firestoreSettings
            .isPersistenceEnabled    // still a warning, but not a failure
        assertTrue("Firestore persistence should be enabled", persisted)
    }
}
