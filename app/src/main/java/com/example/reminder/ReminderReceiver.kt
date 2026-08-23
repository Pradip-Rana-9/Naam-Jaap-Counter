package com.example.reminder

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        Log.d("ReminderReceiver", "Received intent action: $action")

        if (action == Intent.ACTION_BOOT_COMPLETED ||
            action == "android.intent.action.QUICKBOOT_POWERON" ||
            action == "com.htc.intent.action.QUICKBOOT_POWERON" ||
            action == Intent.ACTION_MY_PACKAGE_REPLACED ||
            (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && action == AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED)
        ) {
            // Re-schedule reminder if enabled after device reboot or app upgrade
            Log.d("ReminderReceiver", "Device rebooted or package replaced: rescheduling reminders if enabled.")
            ReminderManager.rescheduleIfEnabled(context)
            return
        }

        val vratName = intent.getStringExtra("VRAT_NAME")
        val reminderType = intent.getStringExtra("REMINDER_TYPE") ?: "GENERAL"
        val hour = intent.getIntExtra("REMINDER_HOUR", -1)
        val minute = intent.getIntExtra("REMINDER_MINUTE", -1)
        val requestCode = intent.getIntExtra("REQUEST_CODE", 1001)

        val customTitle = intent.getStringExtra("NOTIFICATION_TITLE")
            ?: when {
                !vratName.isNullOrBlank() -> "🕉️ $vratName Vrat Reminder"
                reminderType == "MORNING" -> "🌅 Morning Jaap Sadhana"
                reminderType == "EVENING" -> "🌙 Evening Jaap Sadhana"
                else -> "Daily Mantra Chanting 🧘"
            }

        val customBody = intent.getStringExtra("NOTIFICATION_BODY")
            ?: when {
                !vratName.isNullOrBlank() -> "Today is sacred $vratName. Remember to maintain sadhana and chant holy mantras."
                reminderType == "MORNING" -> "Start your sacred day with divine chanting and inner peace. 🌸"
                reminderType == "EVENING" -> "Complete your daily Malas and connect with the Supreme Divine. 🕉️"
                else -> "Take a sacred moment to chant your daily Malas and connect with divinity! 🌸"
            }

        val notifId = if (requestCode > 0) requestCode else (System.currentTimeMillis() % 10000).toInt() + 1000
        showNotification(context, notifId, customTitle, customBody)

        // For one-shot setAndAllowWhileIdle daily alarms, re-arm for the next day
        if (hour >= 0 && minute >= 0 && (reminderType == "GENERAL" || reminderType == "MORNING" || reminderType == "EVENING")) {
            Log.d("ReminderReceiver", "Re-arming recurring daily alarm for next day: type=$reminderType, hour=$hour, min=$minute")
            ReminderManager.scheduleDailyReminder(
                context = context,
                hour = hour,
                minute = minute,
                requestCode = requestCode,
                title = customTitle,
                body = customBody,
                reminderType = reminderType
            )
        }
    }

    private fun showNotification(context: Context, notificationId: Int, title: String, message: String) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "jaap_reminder_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Daily Jaap Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders to perform your daily Jaap chanting"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val launchIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        notificationManager.notify(notificationId, builder.build())
    }
}
