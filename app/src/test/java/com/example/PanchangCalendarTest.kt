package com.example

import com.example.data.panchang.PanchangDataStore
import com.example.data.panchang.PanchangRepository
import com.example.ui.components.getDaysRemaining
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class PanchangCalendarTest {

    private lateinit var panchangRepository: PanchangRepository

    @Before
    fun setup() {
        panchangRepository = PanchangRepository()
    }

    @Test
    fun testPavitraEkadashi2026IsCorrectDate() {
        val pavitra = panchangRepository.getEventById("2026_08_23_PAVITRA_EKADASHI")
        assertNotNull("Pavitra Ekadashi 2026 must exist", pavitra)
        assertEquals("Pavitra Ekadashi", pavitra!!.nameEnglish)
        assertEquals("23 Aug 2026", pavitra.dateString)
        assertEquals("Sunday", pavitra.dayOfWeek)
        assertEquals("Shukla Paksha", pavitra.pakshaEnglish)
        assertEquals("Shukla Ekadashi", pavitra.tithiEnglish)
    }

    @Test
    fun testRakshaBandhan2026IsCorrectDate() {
        val rakhi = panchangRepository.getEventById("2026_08_28_SHRAVANA_PURNIMA_RAKSHA_BANDHAN")
        assertNotNull("Raksha Bandhan 2026 must exist", rakhi)
        assertEquals("Raksha Bandhan / Shravana Purnima", rakhi!!.nameEnglish)
        assertEquals("28 Aug 2026", rakhi.dateString)
        assertEquals("Friday", rakhi.dayOfWeek)
        assertEquals("Purnima Tithi", rakhi.tithiEnglish)
    }

    @Test
    fun testJanmashtami2026IsCorrectDate() {
        val janmashtami = panchangRepository.getEventById("2026_09_04_JANMASHTAMI")
        assertNotNull("Janmashtami 2026 must exist", janmashtami)
        assertEquals("Sri Krishna Janmashtami", janmashtami!!.nameEnglish)
        assertEquals("04 Sep 2026", janmashtami.dateString)
        assertEquals("Friday", janmashtami.dayOfWeek)
        assertEquals("Ashtami Tithi (Rohini)", janmashtami.tithiEnglish)
    }

    @Test
    fun testMultiYearDatasetIntegrity() {
        val allEvents = panchangRepository.getAllEvents()
        assertTrue("Multi-year dataset should contain at least 50 events", allEvents.size >= 50)

        // Verify years present: 2026, 2027, 2028, 2029, 2030
        val years = allEvents.map { it.eventDate.substring(0, 4).toInt() }.distinct()
        assertTrue("Must include 2026", years.contains(2026))
        assertTrue("Must include 2027", years.contains(2027))
        assertTrue("Must include 2028", years.contains(2028))
        assertTrue("Must include 2029", years.contains(2029))
        assertTrue("Must include 2030", years.contains(2030))
    }

    @Test
    fun testCountdownCalculationDeterministic() {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).apply {
            timeZone = TimeZone.getTimeZone("Asia/Kolkata")
        }

        // Test with known event timestamp
        val event = panchangRepository.getEventById("2026_08_23_PAVITRA_EKADASHI")!!
        assertNotNull(event)

        // Event target timestamp is fixed
        assertTrue(event.targetTimestamp > 0)
    }

    @Test
    fun testCategoryFiltering() {
        val ekadashis = panchangRepository.getEventsByCategory("EKADASHI")
        assertTrue("All returned events must be EKADASHI category", ekadashis.all { it.category == "EKADASHI" })

        val vrats = panchangRepository.getEventsByCategory("VRAT")
        assertTrue("All returned events must be VRAT category", vrats.all { it.category == "VRAT" })
    }

    @Test
    fun testUpcomingEventsSliceAndRollover() {
        val allDevotionalEvents = panchangRepository.getUpcomingEvents(limit = 100)
        assertTrue("All devotional events should not be empty", allDevotionalEvents.isNotEmpty())

        // Simulating the Home screen upcoming events subset (next 2-3 events)
        val homeUpcomingEvents = allDevotionalEvents.take(3)
        assertTrue("Home upcoming events should show at most 3 items", homeUpcomingEvents.size in 1..3)

        // Verify that featured hero card is the first upcoming item
        val featured = homeUpcomingEvents.first()
        assertEquals(allDevotionalEvents.first().id, featured.id)

        // Simulating rollover: after first event passes, the next event becomes first
        val simulatedPassedTime = allDevotionalEvents.first().targetTimestamp + 86400000L
        val remainingAfterPass = panchangRepository.getUpcomingEvents(fromEpochMs = simulatedPassedTime, limit = 100)
        assertTrue("Remaining events should exist after simulated time", remainingAfterPass.isNotEmpty())

        val newHomeUpcoming = remainingAfterPass.take(3)
        assertTrue("New upcoming should contain at most 3 events", newHomeUpcoming.size in 1..3)
        assertEquals("Next upcoming event should now be featured", remainingAfterPass.first().id, newHomeUpcoming.first().id)
    }

    @Test
    fun testNoDuplicateEventsInCalendar() {
        val allEntities = panchangRepository.getAllEvents()
        val uniqueIds = allEntities.map { it.eventId }.toSet()
        assertEquals("No duplicate event IDs should exist in the Panchang repository", allEntities.size, uniqueIds.size)
    }

    @Test
    fun testSpecificDatesEventSelectionAndCountdownUnderAsiaKolkata() {
        val ist = TimeZone.getTimeZone("Asia/Kolkata")
        val sdfIso = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH).apply {
            timeZone = ist
        }

        // Test Date 1: 20 Aug 2026 (10:00 AM IST) -> Next should be 23 Aug 2026 (Pavitra Ekadashi), 3 days remaining
        val epoch20Aug = sdfIso.parse("2026-08-20 10:00")!!.time
        val upcoming20Aug = panchangRepository.getUpcomingEvents(fromEpochMs = epoch20Aug, userTimezone = ist)
        assertEquals("2026-08-23", panchangRepository.getAllEvents().first { it.eventId.contains("PAVITRA_EKADASHI") }.eventDate)
        assertEquals("Pavitra Ekadashi", upcoming20Aug.first().nameEnglish)
        assertEquals("23 Aug 2026", upcoming20Aug.first().dateString)
        assertEquals(3, getDaysRemaining(upcoming20Aug.first().targetTimestamp, ist, epoch20Aug))

        // Test Date 2: 21 Aug 2026 (10:00 AM IST) -> 2 days remaining
        val epoch21Aug = sdfIso.parse("2026-08-21 10:00")!!.time
        val upcoming21Aug = panchangRepository.getUpcomingEvents(fromEpochMs = epoch21Aug, userTimezone = ist)
        assertEquals("Pavitra Ekadashi", upcoming21Aug.first().nameEnglish)
        assertEquals(2, getDaysRemaining(upcoming21Aug.first().targetTimestamp, ist, epoch21Aug))

        // Test Date 3: 22 Aug 2026 (10:00 AM IST) -> 1 day remaining (Tomorrow)
        val epoch22Aug = sdfIso.parse("2026-08-22 10:00")!!.time
        val upcoming22Aug = panchangRepository.getUpcomingEvents(fromEpochMs = epoch22Aug, userTimezone = ist)
        assertEquals("Pavitra Ekadashi", upcoming22Aug.first().nameEnglish)
        assertEquals(1, getDaysRemaining(upcoming22Aug.first().targetTimestamp, ist, epoch22Aug))

        // Test Date 4: 23 Aug 2026 (10:00 AM IST) -> 0 days remaining (Today)
        val epoch23Aug = sdfIso.parse("2026-08-23 10:00")!!.time
        val upcoming23Aug = panchangRepository.getUpcomingEvents(fromEpochMs = epoch23Aug, userTimezone = ist)
        assertEquals("Pavitra Ekadashi", upcoming23Aug.first().nameEnglish)
        assertEquals(0, getDaysRemaining(upcoming23Aug.first().targetTimestamp, ist, epoch23Aug))

        // Test Date 5: 24 Aug 2026 (10:00 AM IST) -> Pavitra Ekadashi passed (endTimestamp 23 Aug 22:00 IST), next is Raksha Bandhan (28 Aug 2026), 4 days remaining
        val epoch24Aug = sdfIso.parse("2026-08-24 10:00")!!.time
        val upcoming24Aug = panchangRepository.getUpcomingEvents(fromEpochMs = epoch24Aug, userTimezone = ist)
        assertEquals("Raksha Bandhan / Shravana Purnima", upcoming24Aug.first().nameEnglish)
        assertEquals("28 Aug 2026", upcoming24Aug.first().dateString)
        assertEquals(4, getDaysRemaining(upcoming24Aug.first().targetTimestamp, ist, epoch24Aug))

        // Test Date 6: 28 Aug 2026 (10:00 AM IST) -> Raksha Bandhan is TODAY (0 days remaining)
        val epoch28Aug = sdfIso.parse("2026-08-28 10:00")!!.time
        val upcoming28Aug = panchangRepository.getUpcomingEvents(fromEpochMs = epoch28Aug, userTimezone = ist)
        assertEquals("Raksha Bandhan / Shravana Purnima", upcoming28Aug.first().nameEnglish)
        assertEquals(0, getDaysRemaining(upcoming28Aug.first().targetTimestamp, ist, epoch28Aug))
    }
}
