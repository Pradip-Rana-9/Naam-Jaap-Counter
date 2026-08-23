package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.SizeF
import android.widget.RemoteViews
import com.example.JaapApplication
import com.example.MainActivity
import com.example.R
import com.example.data.database.AppDatabase
import com.example.data.repository.JaapRepository
import com.example.util.SoundHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NaamJaapWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val pendingResult = goAsync()
        widgetScope.launch {
            try {
                val repository = getRepository(context)
                repository.ensureInitialData()
                for (appWidgetId in appWidgetIds) {
                    val views = buildResponsiveRemoteViews(context, appWidgetManager, appWidgetId, repository)
                    appWidgetManager.updateAppWidget(appWidgetId, views)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                pendingResult.finish()
            }
        }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle?
    ) {
        val pendingResult = goAsync()
        widgetScope.launch {
            try {
                val repository = getRepository(context)
                val views = buildResponsiveRemoteViews(context, appWidgetManager, appWidgetId, repository)
                appWidgetManager.updateAppWidget(appWidgetId, views)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                pendingResult.finish()
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action == ACTION_JAAP_TAP) {
            val pendingResult = goAsync()
            widgetScope.launch {
                try {
                    val repository = getRepository(context)
                    val settings = repository.getUserSettingsDirect()

                    // Perform haptic feedback immediately for responsive feel
                    if (settings?.hapticFeedbackEnabled == true) {
                        performHapticFeedback(context, isMalaComplete = false)
                    }

                    // Atomic increment in single source of truth Room repository
                    val isMalaCompleted = repository.incrementBead(sessionDurationMillis = 60_000L)

                    if (isMalaCompleted) {
                        if (settings?.hapticFeedbackEnabled == true) {
                            performHapticFeedback(context, isMalaComplete = true)
                        }
                        if (settings?.audioChimeEnabled == true) {
                            SoundHelper.playMalaCompleteTone()
                        }
                    }

                    // Update all widgets immediately
                    updateAllWidgetsDirect(context, repository)
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }

    companion object {
        const val ACTION_JAAP_TAP = "com.example.widget.ACTION_JAAP_TAP"
        private val widgetScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        private fun getRepository(context: Context): JaapRepository {
            return try {
                if (context.applicationContext is JaapApplication) {
                    (context.applicationContext as JaapApplication).repository
                } else {
                    val db = AppDatabase.getDatabase(context.applicationContext)
                    JaapRepository(db.jaapDao(), context.applicationContext)
                }
            } catch (e: Exception) {
                val db = AppDatabase.getDatabase(context.applicationContext)
                JaapRepository(db.jaapDao(), context.applicationContext)
            }
        }

        fun updateAllWidgets(context: Context) {
            widgetScope.launch {
                try {
                    val repository = getRepository(context)
                    updateAllWidgetsDirect(context, repository)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        private suspend fun updateAllWidgetsDirect(context: Context, repository: JaapRepository) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, NaamJaapWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
            if (appWidgetIds.isEmpty()) return

            for (appWidgetId in appWidgetIds) {
                val views = buildResponsiveRemoteViews(context, appWidgetManager, appWidgetId, repository)
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }

        private suspend fun buildResponsiveRemoteViews(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
            repository: JaapRepository
        ): RemoteViews {
            val dao = AppDatabase.getDatabase(context).jaapDao()
            val settings = dao.getUserSettingsDirect()
            val mantra = if (settings != null) dao.getMantraById(settings.selectedMantraId) else dao.getMantraById(1)
            val todayStr = repository.getTodayString()
            val todayProgress = dao.getDailyProgressDirect(todayStr)
            val streakPair = repository.calculateStreaks()

            val currentBead = settings?.currentBeadInIncompleteMala ?: 0
            val currentMala = settings?.currentMalaNumber ?: 1
            val mantraName = mantra?.textEnglish ?: "Radhe Radhe"
            val dailyGoalMalas = settings?.dailyGoalMalas ?: 10
            val completedMalasToday = todayProgress?.totalMalasCompleted ?: 0
            val streakDays = streakPair.first

            // Construct PendingIntents
            val tapIntent = Intent(context, NaamJaapWidgetProvider::class.java).apply {
                action = ACTION_JAAP_TAP
            }
            val tapPendingIntent = PendingIntent.getBroadcast(
                context,
                1001,
                tapIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val openAppIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val openAppPendingIntent = PendingIntent.getActivity(
                context,
                1002,
                openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Check dimensions for adaptive rendering
            val options = appWidgetManager.getAppWidgetOptions(appWidgetId)
            val minWidth = options?.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH) ?: 0
            val minHeight = options?.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT) ?: 0

            // If Android S+, construct multi-size responsive RemoteViews
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val smallViews = createSingleRemoteView(
                    context = context,
                    layoutRes = R.layout.widget_jaap_small,
                    isCompact = true,
                    currentBead = currentBead,
                    currentMala = currentMala,
                    mantraName = mantraName,
                    completedMalasToday = completedMalasToday,
                    dailyGoalMalas = dailyGoalMalas,
                    streakDays = streakDays,
                    tapPendingIntent = tapPendingIntent,
                    openAppPendingIntent = openAppPendingIntent
                )

                val mediumViews = createSingleRemoteView(
                    context = context,
                    layoutRes = R.layout.widget_jaap_medium,
                    isCompact = false,
                    currentBead = currentBead,
                    currentMala = currentMala,
                    mantraName = mantraName,
                    completedMalasToday = completedMalasToday,
                    dailyGoalMalas = dailyGoalMalas,
                    streakDays = streakDays,
                    tapPendingIntent = tapPendingIntent,
                    openAppPendingIntent = openAppPendingIntent
                )

                val largeViews = createSingleRemoteView(
                    context = context,
                    layoutRes = R.layout.widget_jaap_large,
                    isCompact = false,
                    currentBead = currentBead,
                    currentMala = currentMala,
                    mantraName = mantraName,
                    completedMalasToday = completedMalasToday,
                    dailyGoalMalas = dailyGoalMalas,
                    streakDays = streakDays,
                    tapPendingIntent = tapPendingIntent,
                    openAppPendingIntent = openAppPendingIntent
                )

                return RemoteViews(
                    mapOf(
                        SizeF(80f, 35f) to smallViews,
                        SizeF(130f, 110f) to mediumViews,
                        SizeF(220f, 70f) to largeViews
                    )
                )
            }

            // Fallback for pre-Android 12
            val layoutRes = when {
                minWidth >= 220 && minHeight >= 70 -> R.layout.widget_jaap_large
                minHeight >= 110 -> R.layout.widget_jaap_medium
                else -> R.layout.widget_jaap_small
            }
            val isCompact = (layoutRes == R.layout.widget_jaap_small)

            return createSingleRemoteView(
                context = context,
                layoutRes = layoutRes,
                isCompact = isCompact,
                currentBead = currentBead,
                currentMala = currentMala,
                mantraName = mantraName,
                completedMalasToday = completedMalasToday,
                dailyGoalMalas = dailyGoalMalas,
                streakDays = streakDays,
                tapPendingIntent = tapPendingIntent,
                openAppPendingIntent = openAppPendingIntent
            )
        }

        private fun createSingleRemoteView(
            context: Context,
            layoutRes: Int,
            isCompact: Boolean,
            currentBead: Int,
            currentMala: Int,
            mantraName: String,
            completedMalasToday: Int,
            dailyGoalMalas: Int,
            streakDays: Int,
            tapPendingIntent: PendingIntent,
            openAppPendingIntent: PendingIntent
        ): RemoteViews {
            val views = RemoteViews(context.packageName, layoutRes)

            // Mantra & Mala strings
            views.setTextViewText(R.id.widget_mantra_name, mantraName)
            views.setTextViewText(R.id.widget_mala_number, "Mala #$currentMala")

            // Right side stats (only in large layout)
            if (layoutRes == R.layout.widget_jaap_large) {
                views.setTextViewText(R.id.widget_today_malas, "$completedMalasToday / $dailyGoalMalas")
                views.setTextViewText(R.id.widget_streak_days, "$streakDays Days")
            }

            // Render crisp circular Mala visualization
            val bitmapSize = if (isCompact) 140 else 200
            val beadBitmap = WidgetBeadRenderer.renderBeadCircle(
                currentBead = currentBead,
                isCompact = isCompact,
                sizePx = bitmapSize
            )
            views.setImageViewBitmap(R.id.widget_bead_canvas, beadBitmap)

            // Setup Tap Intent on the center Jaap area
            views.setOnClickPendingIntent(R.id.widget_tap_area, tapPendingIntent)

            // Setup Open App Intent on header / settings buttons
            if (layoutRes == R.layout.widget_jaap_large || layoutRes == R.layout.widget_jaap_medium) {
                views.setOnClickPendingIntent(R.id.widget_btn_settings, openAppPendingIntent)
            }
            views.setOnClickPendingIntent(R.id.widget_icon_mala, openAppPendingIntent)
            if (layoutRes == R.layout.widget_jaap_large || layoutRes == R.layout.widget_jaap_small) {
                views.setOnClickPendingIntent(R.id.widget_left_container, openAppPendingIntent)
            }

            return views
        }

        private fun performHapticFeedback(context: Context, isMalaComplete: Boolean) {
            try {
                val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                    manager.defaultVibrator
                } else {
                    @Suppress("DEPRECATION")
                    context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                }

                if (isMalaComplete) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val pattern = longArrayOf(0, 70, 60, 110)
                        val amplitudes = intArrayOf(0, 180, 0, 255)
                        vibrator.vibrate(VibrationEffect.createWaveform(pattern, amplitudes, -1))
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator.vibrate(longArrayOf(0, 70, 60, 110), -1)
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(28, VibrationEffect.DEFAULT_AMPLITUDE))
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator.vibrate(28)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
