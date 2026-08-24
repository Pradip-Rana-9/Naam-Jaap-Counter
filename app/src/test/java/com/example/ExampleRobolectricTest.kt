package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.database.AppDatabase
import com.example.data.entity.DailyProgress
import com.example.data.entity.UserSettings
import com.example.data.repository.JaapRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private lateinit var database: AppDatabase
    private lateinit var repository: JaapRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = JaapRepository(database.jaapDao(), context)
        runBlocking {
            repository.ensureInitialData()
        }
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testInitialState() = runBlocking {
        val settings = repository.getUserSettingsDirect()
        assertEquals(0, settings?.currentBeadInIncompleteMala)
        assertEquals(1, settings?.currentMalaNumber)
        assertEquals(0, settings?.currentSessionMalasCount)
    }

    @Test
    fun testTap1to107DoesNotCompleteMala() = runBlocking {
        for (i in 1..107) {
            val completed = repository.incrementBead()
            assertFalse("Bead $i should not complete mala", completed)
        }

        val settings = repository.getUserSettingsDirect()
        assertEquals(107, settings?.currentBeadInIncompleteMala)
        assertEquals(1, settings?.currentMalaNumber)
        assertEquals(0, settings?.currentSessionMalasCount)

        val today = repository.getTodayString()
        val progress = database.jaapDao().getDailyProgressDirect(today)
        assertEquals(107, progress?.totalBeadsCompleted)
        assertEquals(0, progress?.totalMalasCompleted)
    }

    @Test
    fun testTap108CompletesMalaAndPreparesNextMala() = runBlocking {
        // Tap 107 beads first
        for (i in 1..107) {
            repository.incrementBead()
        }

        // 108th tap must complete mala!
        val completedOn108 = repository.incrementBead()
        assertTrue("108th bead MUST complete mala", completedOn108)

        val settings = repository.getUserSettingsDirect()
        assertEquals(0, settings?.currentBeadInIncompleteMala)
        assertEquals(2, settings?.currentMalaNumber)
        assertEquals(1, settings?.currentSessionMalasCount)

        val today = repository.getTodayString()
        val progress = database.jaapDao().getDailyProgressDirect(today)
        assertEquals(108, progress?.totalBeadsCompleted)
        assertEquals(1, progress?.totalMalasCompleted)
    }

    @Test
    fun testNextMalaCountingContinuesCorrectly() = runBlocking {
        // Complete 1st mala (108 beads)
        for (i in 1..108) {
            repository.incrementBead()
        }

        // Tap 1 of 2nd mala
        val completed109 = repository.incrementBead()
        assertFalse(completed109)

        val settings = repository.getUserSettingsDirect()
        assertEquals(1, settings?.currentBeadInIncompleteMala)
        assertEquals(2, settings?.currentMalaNumber)
        assertEquals(1, settings?.currentSessionMalasCount)

        // Complete up to 216 beads (2 full malas)
        for (i in 110..216) {
            repository.incrementBead()
        }

        val finalSettings = repository.getUserSettingsDirect()
        assertEquals(0, finalSettings?.currentBeadInIncompleteMala)
        assertEquals(3, finalSettings?.currentMalaNumber)
        assertEquals(2, finalSettings?.currentSessionMalasCount)

        val today = repository.getTodayString()
        val progress = database.jaapDao().getDailyProgressDirect(today)
        assertEquals(216, progress?.totalBeadsCompleted)
        assertEquals(2, progress?.totalMalasCompleted)
    }

    @Test
    fun testConcurrentRapidTappingNeverLosesBeads() = runBlocking {
        // Launch 54 coroutines in parallel, each tapping 2 beads = exactly 108 beads total
        kotlinx.coroutines.coroutineScope {
            val jobs = List(54) {
                async(kotlinx.coroutines.Dispatchers.Default) {
                    repository.incrementBead()
                    repository.incrementBead()
                }
            }
            jobs.forEach { it.await() }
        }

        val settings = repository.getUserSettingsDirect()
        assertEquals("After 108 concurrent taps, current bead should reset to 0", 0, settings?.currentBeadInIncompleteMala)
        assertEquals("Mala number should increment to 2", 2, settings?.currentMalaNumber)
        assertEquals("Session malas should be 1", 1, settings?.currentSessionMalasCount)

        val today = repository.getTodayString()
        val progress = database.jaapDao().getDailyProgressDirect(today)
        assertEquals(108, progress?.totalBeadsCompleted)
        assertEquals(1, progress?.totalMalasCompleted)
    }

    @Test
    fun testReminderTimeParsing() {
        val (h1, m1) = com.example.reminder.ReminderManager.parseTimeString("06:30 AM", 6, 0)
        assertEquals(6, h1)
        assertEquals(30, m1)

        val (h2, m2) = com.example.reminder.ReminderManager.parseTimeString("08:45 PM", 20, 0)
        assertEquals(20, h2)
        assertEquals(45, m2)

        val (h3, m3) = com.example.reminder.ReminderManager.parseTimeString("12:15 PM", 12, 0)
        assertEquals(12, h3)
        assertEquals(15, m3)

        val (h4, m4) = com.example.reminder.ReminderManager.parseTimeString("12:00 AM", 0, 0)
        assertEquals(0, h4)
        assertEquals(0, m4)

        val (h5, m5) = com.example.reminder.ReminderManager.parseTimeString(null, 7, 30)
        assertEquals(7, h5)
        assertEquals(30, m5)
    }

    @Test
    fun testSoundHelperSafety() {
        com.example.util.SoundHelper.playConfirmationTone()
        com.example.util.SoundHelper.playMalaCompleteTone()
        com.example.util.SoundHelper.release()
    }

    @Test
    fun testResetCurrentMalaBeads() = runBlocking {
        // Count 67 beads
        for (i in 1..67) {
            repository.incrementBead()
        }

        var settings = repository.getUserSettingsDirect()
        assertEquals(67, settings?.currentBeadInIncompleteMala)

        // Reset current mala
        repository.resetCurrentMalaBeads()

        settings = repository.getUserSettingsDirect()
        assertEquals(0, settings?.currentBeadInIncompleteMala)
        assertEquals(1, settings?.currentMalaNumber)
    }

    @Test
    fun testProfileSetupCompletionPersistsAcrossRestarts() = runBlocking {
        // Initially, on fresh install, onboarding is false
        val initial = repository.getUserSettingsDirect()
        assertFalse(initial?.isOnboardingCompleted ?: true)

        // User fills profile setup and saves
        val updated = (initial ?: UserSettings()).copy(
            userName = "Arjun",
            userInitial = "A",
            avatarId = 3,
            dailyGoalMalas = 16,
            selectedMantraId = 1,
            morningReminderEnabled = true,
            morningReminderTime = "05:30 AM",
            eveningReminderEnabled = true,
            eveningReminderTime = "07:30 PM",
            authMethod = "GUEST",
            userEmail = "",
            isOnboardingCompleted = true
        )
        repository.saveFullUserSettings(updated)

        // Verify persisted in Room immediately
        val saved = repository.getUserSettingsDirect()
        assertTrue("isOnboardingCompleted must be true after saving profile", saved?.isOnboardingCompleted == true)
        assertEquals("Arjun", saved?.userName)
        assertEquals("A", saved?.userInitial)
        assertEquals(3, saved?.avatarId)
        assertEquals(16, saved?.dailyGoalMalas)
        assertEquals("05:30 AM", saved?.morningReminderTime)

        // Simulate app kill and reload: re-running ensureInitialData should NOT overwrite completed profile
        repository.ensureInitialData()
        val afterRestart = repository.getUserSettingsDirect()
        assertTrue("isOnboardingCompleted must remain true across app restart", afterRestart?.isOnboardingCompleted == true)
        assertEquals("Arjun", afterRestart?.userName)
        assertEquals(16, afterRestart?.dailyGoalMalas)
    }

    @Test
    fun testGuestProfileSetupCompletionAndAccountMigration() = runBlocking {
        // Complete as guest
        val guestSettings = UserSettings(
            userName = "Devotee Guest",
            userInitial = "D",
            avatarId = 2,
            dailyGoalMalas = 11,
            authMethod = "GUEST",
            userEmail = "",
            isOnboardingCompleted = true
        )
        repository.saveFullUserSettings(guestSettings)

        val directGuest = repository.getUserSettingsDirect()
        assertTrue("Guest profile completion must be true", directGuest?.isOnboardingCompleted == true)
        assertEquals("GUEST", directGuest?.authMethod)

        // Simulating subsequent restart
        repository.ensureInitialData()
        val afterRestartGuest = repository.getUserSettingsDirect()
        assertTrue("Guest profile completion must remain true on restart", afterRestartGuest?.isOnboardingCompleted == true)
        assertEquals(11, afterRestartGuest?.dailyGoalMalas)
    }

    @Test
    fun testWidgetLayoutInflationForAllSizes() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val dummyParent = android.widget.FrameLayout(context)

        // Test 1: Small Widget (2x1) layout inflation
        val smallViews = android.widget.RemoteViews(context.packageName, com.example.R.layout.widget_jaap_small)
        smallViews.setTextViewText(com.example.R.id.widget_mantra_name, "Radhe Radhe")
        smallViews.setTextViewText(com.example.R.id.widget_mala_number, "Mala #1")
        val smallInflated = smallViews.apply(context, dummyParent)
        org.junit.Assert.assertNotNull("Small widget view must inflate successfully", smallInflated)

        // Test 2: Medium Widget (2x2) layout inflation
        val mediumViews = android.widget.RemoteViews(context.packageName, com.example.R.layout.widget_jaap_medium)
        mediumViews.setTextViewText(com.example.R.id.widget_mantra_name, "Radhe Radhe")
        mediumViews.setTextViewText(com.example.R.id.widget_mala_number, "Mala #1")
        val mediumInflated = mediumViews.apply(context, dummyParent)
        org.junit.Assert.assertNotNull("Medium widget view must inflate successfully", mediumInflated)

        // Test 3: Large Widget (4x2) layout inflation
        val largeViews = android.widget.RemoteViews(context.packageName, com.example.R.layout.widget_jaap_large)
        largeViews.setTextViewText(com.example.R.id.widget_mantra_name, "Radhe Radhe")
        largeViews.setTextViewText(com.example.R.id.widget_mala_number, "Mala #1")
        largeViews.setTextViewText(com.example.R.id.widget_today_malas, "3 / 10")
        largeViews.setTextViewText(com.example.R.id.widget_streak_days, "7 Days")
        val largeInflated = largeViews.apply(context, dummyParent)
        org.junit.Assert.assertNotNull("Large widget view must inflate successfully without RemoteViews exceptions", largeInflated)
    }

    @Test
    fun testWidgetBeadRendererOutputsValidBitmaps() {
        // Compact render
        val compactBitmap = com.example.widget.WidgetBeadRenderer.renderBeadCircle(
            currentBead = 47,
            isCompact = true,
            sizePx = 140
        )
        org.junit.Assert.assertNotNull(compactBitmap)
        assertEquals(140, compactBitmap.width)
        assertEquals(140, compactBitmap.height)
        assertFalse(compactBitmap.isRecycled)

        // Standard/Large render
        val largeBitmap = com.example.widget.WidgetBeadRenderer.renderBeadCircle(
            currentBead = 108,
            isCompact = false,
            sizePx = 200
        )
        org.junit.Assert.assertNotNull(largeBitmap)
        assertEquals(200, largeBitmap.width)
        assertEquals(200, largeBitmap.height)
        assertFalse(largeBitmap.isRecycled)
    }

    @Test
    fun testGuestModeDetectionAndPreservation() = runBlocking {
        // Initial setup as Guest
        val guest = UserSettings(
            userName = "Radha Bhakt",
            userInitial = "R",
            avatarId = 1,
            dailyGoalMalas = 10,
            authMethod = "GUEST",
            userEmail = "",
            isOnboardingCompleted = true
        )
        repository.saveFullUserSettings(guest)

        // Increment beads in guest mode
        for (i in 1..108) {
            repository.incrementBead()
        }

        val guestSettings = repository.getUserSettingsDirect()
        assertEquals("GUEST", guestSettings?.authMethod)
        assertEquals(1, guestSettings?.currentSessionMalasCount)

        val today = repository.getTodayString()
        val guestProgress = database.jaapDao().getDailyProgressDirect(today)
        assertEquals(108, guestProgress?.totalBeadsCompleted)
        assertEquals(1, guestProgress?.totalMalasCompleted)

        // Now simulate user upgrading from Guest to Registered Account
        val registeredSettings = (guestSettings ?: UserSettings()).copy(
            authMethod = "EMAIL",
            userEmail = "radha@example.com",
            userName = "Radha Sharma"
        )
        repository.saveFullUserSettings(registeredSettings)

        // Verify data was NOT wiped during upgrade
        val upgradedSettings = repository.getUserSettingsDirect()
        assertEquals("EMAIL", upgradedSettings?.authMethod)
        assertEquals("radha@example.com", upgradedSettings?.userEmail)
        assertEquals("Radha Sharma", upgradedSettings?.userName)

        val preservedProgress = database.jaapDao().getDailyProgressDirect(today)
        assertEquals("Guest bead count must be preserved after registration", 108, preservedProgress?.totalBeadsCompleted)
        assertEquals("Guest mala count must be preserved after registration", 1, preservedProgress?.totalMalasCompleted)
    }

    @Test
    fun testAllLauncherDrawablesAndMipmapsCanInflate() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        // Test in-app logo
        val appLogo = androidx.core.content.ContextCompat.getDrawable(context, com.example.R.drawable.app_logo)
        org.junit.Assert.assertNotNull("app_logo must be loadable as drawable", appLogo)

        // Test launcher background and foreground
        val bg = androidx.core.content.ContextCompat.getDrawable(context, com.example.R.drawable.ic_launcher_background)
        org.junit.Assert.assertNotNull("ic_launcher_background must be loadable", bg)

        val fg = androidx.core.content.ContextCompat.getDrawable(context, com.example.R.drawable.ic_launcher_foreground)
        org.junit.Assert.assertNotNull("ic_launcher_foreground must be loadable", fg)

        // Test mipmaps
        val launcher = androidx.core.content.ContextCompat.getDrawable(context, com.example.R.mipmap.ic_launcher)
        org.junit.Assert.assertNotNull("ic_launcher mipmap must be loadable", launcher)

        val launcherRound = androidx.core.content.ContextCompat.getDrawable(context, com.example.R.mipmap.ic_launcher_round)
        org.junit.Assert.assertNotNull("ic_launcher_round mipmap must be loadable", launcherRound)
    }

    @Test
    fun testMainActivityLifecycleAndUiInflation() {
        val controller = org.robolectric.Robolectric.buildActivity(MainActivity::class.java)
        val activity = controller.create().start().resume().get()
        org.junit.Assert.assertNotNull("MainActivity must create and resume without crashing", activity)
        controller.pause().stop().destroy()
    }
}

