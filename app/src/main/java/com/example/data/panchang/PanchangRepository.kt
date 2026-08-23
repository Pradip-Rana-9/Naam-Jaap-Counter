package com.example.data.panchang

import com.example.ui.components.DevotionalEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class PanchangRepository {

    private val allEntities: List<PanchangEventEntity> = PanchangDataStore.ALL_EVENTS

    fun getAllEvents(): List<PanchangEventEntity> = allEntities

    fun getUpcomingEvents(
        fromEpochMs: Long = System.currentTimeMillis(),
        limit: Int = 30,
        userTimezone: TimeZone = TimeZone.getDefault()
    ): List<DevotionalEvent> {
        val todayCal = Calendar.getInstance(userTimezone).apply {
            timeInMillis = fromEpochMs
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val todayStartMs = todayCal.timeInMillis

        val sdfIso = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).apply {
            timeZone = TimeZone.getTimeZone(PanchangDataStore.TIMEZONE_IST)
        }

        return allEntities
            .filter { entity ->
                val eventDateMs = try {
                    sdfIso.parse(entity.eventDate)?.time ?: entity.startTimestampUtc
                } catch (e: Exception) {
                    entity.startTimestampUtc
                }
                // Include if event is today or in the future
                eventDateMs >= todayStartMs || entity.endTimestampUtc >= fromEpochMs
            }
            .sortedBy { entity ->
                try {
                    sdfIso.parse(entity.eventDate)?.time ?: entity.startTimestampUtc
                } catch (e: Exception) {
                    entity.startTimestampUtc
                }
            }
            .take(limit)
            .map { PanchangDataStore.entityToDevotionalEvent(it, userTimezone) }
    }

    fun getEventById(eventId: String, userTimezone: TimeZone = TimeZone.getDefault()): DevotionalEvent? {
        val entity = allEntities.firstOrNull { it.eventId.equals(eventId, ignoreCase = true) }
            ?: return null
        return PanchangDataStore.entityToDevotionalEvent(entity, userTimezone)
    }

    fun getUpcomingEkadashi(fromEpochMs: Long = System.currentTimeMillis()): DevotionalEvent? {
        return getUpcomingEvents(fromEpochMs).firstOrNull { it.category == "EKADASHI" }
    }

    fun getUpcomingPurnima(fromEpochMs: Long = System.currentTimeMillis()): DevotionalEvent? {
        return getUpcomingEvents(fromEpochMs).firstOrNull { it.tithiEnglish.contains("Purnima", ignoreCase = true) || it.nameEnglish.contains("Purnima", ignoreCase = true) }
    }

    fun getEventsByCategory(
        category: String,
        fromEpochMs: Long = System.currentTimeMillis(),
        userTimezone: TimeZone = TimeZone.getDefault()
    ): List<DevotionalEvent> {
        val upcoming = getUpcomingEvents(fromEpochMs, limit = 50, userTimezone = userTimezone)
        return if (category == "ALL") upcoming else upcoming.filter { it.category == category }
    }

    fun getEventsForDate(dateIso: String, userTimezone: TimeZone = TimeZone.getDefault()): List<DevotionalEvent> {
        return allEntities
            .filter { it.eventDate == dateIso }
            .map { PanchangDataStore.entityToDevotionalEvent(it, userTimezone) }
    }
}
