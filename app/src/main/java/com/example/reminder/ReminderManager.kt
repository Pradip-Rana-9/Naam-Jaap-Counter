package com.example.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.data.database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object ReminderManager {

    const val REQUEST_CODE_GENERAL_DAILY = 1001
    const val REQUEST_CODE_MORNING_DAILY = 1002
    const val REQUEST_CODE_EVENING_DAILY = 1003

    fun parseTimeString(timeStr: String?, defaultHour: Int, defaultMinute: Int): Pair<Int, Int> {
        if (timeStr.isNullOrBlank()) return Pair(defaultHour, defaultMinute)
        val trimmed = timeStr.trim()

        // 1. Try 12-hour AM/PM format (e.g., "06:00 AM", "8:00 PM", "6:00 am")
        try {
            val sdf12 = SimpleDateFormat("hh:mm a", Locale.US)
            val date = sdf12.parse(trimmed)
            if (date != null) {
                val cal = Calendar.getInstance().apply { time = date }
                return Pair(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))
            }
        } catch (_: Exception) {
        }

        // 2. Try 24-hour or colon-separated format (e.g., "20:00", "06:30")
        try {
            val parts = trimmed.split(":")
            if (parts.size >= 2) {
                val hourPart = parts[0].trim().toIntOrNull()
                val minDigits = parts[1].trim().takeWhile { it.isDigit() }.toIntOrNull()
                if (hourPart != null && minDigits != null) {
                    val isPM = trimmed.uppercase().contains("PM")
                    val isAM = trimmed.uppercase().contains("AM")
                    val finalHour = when {
                        isPM && hourPart < 12 -> hourPart + 12
                        isAM && hourPart == 12 -> 0
                        else -> hourPart
                    }
                    return Pair(finalHour.coerceIn(0, 23), minDigits.coerceIn(0, 59))
                }
            }
        } catch (_: Exception) {
        }

        return Pair(defaultHour, defaultMinute)
    }

    fun scheduleDailyReminder(
        context: Context,
        hour: Int,
        minute: Int,
        requestCode: Int = REQUEST_CODE_GENERAL_DAILY,
        title: String? = null,
        body: String? = null,
        reminderType: String = "GENERAL"
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            action = "com.example.reminder.ACTION_JAAP_ALARM"
            putExtra("REMINDER_TYPE", reminderType)
            putExtra("REMINDER_HOUR", hour)
            putExtra("REMINDER_MINUTE", minute)
            putExtra("REQUEST_CODE", requestCode)
            if (title != null) putExtra("NOTIFICATION_TITLE", title)
            if (body != null) putExtra("NOTIFICATION_BODY", body)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                }
            } else {
                alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
                )
            }
            Log.d("ReminderManager", "Scheduled reminder (type=$reminderType, req=$requestCode) for ${calendar.time}")
        } catch (e: SecurityException) {
            try {
                alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
                )
            } catch (ex: Exception) {
                Log.w("ReminderManager", "SecurityException scheduling alarm: ${ex.message}")
            }
        } catch (e: Exception) {
            Log.w("ReminderManager", "Exception scheduling alarm: ${e.message}")
        }
    }

    fun cancelReminder(context: Context, requestCode: Int = REQUEST_CODE_GENERAL_DAILY) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            action = "com.example.reminder.ACTION_JAAP_ALARM"
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        try {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
            Log.d("ReminderManager", "Cancelled reminder (req=$requestCode)")
        } catch (e: Exception) {
            Log.w("ReminderManager", "Error cancelling reminder $requestCode: ${e.message}")
        }
    }

    fun cancelAllDailyReminders(context: Context) {
        cancelReminder(context, REQUEST_CODE_GENERAL_DAILY)
        cancelReminder(context, REQUEST_CODE_MORNING_DAILY)
        cancelReminder(context, REQUEST_CODE_EVENING_DAILY)
    }

    fun triggerTestNotification(context: Context, title: String? = null, body: String? = null) {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            action = "com.example.reminder.ACTION_JAAP_TEST"
            if (title != null) putExtra("NOTIFICATION_TITLE", title)
            if (body != null) putExtra("NOTIFICATION_BODY", body)
        }
        context.sendBroadcast(intent)
    }

    fun cancelAllLegacyVratReminders(context: Context) {
        try {
            for (eventId in 0..100) {
                cancelReminder(context, 2000 + eventId)
            }
        } catch (e: Exception) {
            Log.w("ReminderManager", "Error clearing legacy vrat reminders: ${e.message}")
        }
    }

    fun rescheduleIfEnabled(context: Context) {
        cancelAllLegacyVratReminders(context)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getDatabase(context)
                val settings = db.jaapDao().getUserSettingsDirect()
                if (settings == null) {
                    Log.d("ReminderManager", "No saved user settings found on reboot, skipping reminder reschedule.")
                    return@launch
                }

                // 1. General Daily Reminder
                if (settings.reminderEnabled) {
                    val (hour, min) = parseTimeString(settings.reminderTime, 20, 0)
                    scheduleDailyReminder(
                        context = context,
                        hour = hour,
                        minute = min,
                        requestCode = REQUEST_CODE_GENERAL_DAILY,
                        title = "Daily Mantra Chanting 🧘",
                        body = "Take a sacred moment to chant your daily Malas and connect with divinity! 🌸",
                        reminderType = "GENERAL"
                    )
                    Log.d("ReminderManager", "Restored general daily reminder for $hour:$min after boot.")
                } else {
                    cancelReminder(context, REQUEST_CODE_GENERAL_DAILY)
                }

                // 2. Morning Reminder
                if (settings.morningReminderEnabled) {
                    val (mHour, mMin) = parseTimeString(settings.morningReminderTime, 6, 0)
                    scheduleDailyReminder(
                        context = context,
                        hour = mHour,
                        minute = mMin,
                        requestCode = REQUEST_CODE_MORNING_DAILY,
                        title = "🌅 Morning Jaap Sadhana",
                        body = "Start your sacred day with divine chanting and inner peace. 🌸",
                        reminderType = "MORNING"
                    )
                    Log.d("ReminderManager", "Restored morning reminder for $mHour:$mMin after boot.")
                } else {
                    cancelReminder(context, REQUEST_CODE_MORNING_DAILY)
                }

                // 3. Evening Reminder
                if (settings.eveningReminderEnabled) {
                    val (eHour, eMin) = parseTimeString(settings.eveningReminderTime, 20, 0)
                    scheduleDailyReminder(
                        context = context,
                        hour = eHour,
                        minute = eMin,
                        requestCode = REQUEST_CODE_EVENING_DAILY,
                        title = "🌙 Evening Jaap Sadhana",
                        body = "Complete your daily Malas and connect with the Supreme Divine. 🕉️",
                        reminderType = "EVENING"
                    )
                    Log.d("ReminderManager", "Restored evening reminder for $eHour:$eMin after boot.")
                } else {
                    cancelReminder(context, REQUEST_CODE_EVENING_DAILY)
                }
            } catch (e: Exception) {
                Log.e("ReminderManager", "Error restoring reminders on boot: ${e.message}", e)
            }
        }
    }
}
