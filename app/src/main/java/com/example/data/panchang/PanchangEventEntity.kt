package com.example.data.panchang

data class PanchangEventEntity(
    val eventId: String,
    val canonicalName: String,
    val nameHindi: String,
    val nameEnglish: String,
    val category: String, // EKADASHI, VRAT, FESTIVAL
    val masaName: String,
    val paksha: String, // SHUKLA, KRISHNA
    val tithi: String, // EKADASHI, PURNIMA, AMAVASYA, TRAYODASHI, ASHTAMI, etc.
    val tithiHindi: String,
    val tithiEnglish: String,
    val eventDate: String, // ISO "YYYY-MM-DD"
    val startTimestampUtc: Long,
    val endTimestampUtc: Long,
    val paranaStart: String,
    val paranaEnd: String,
    val paranaTimeHindi: String,
    val paranaTimeEnglish: String,
    val fastingType: String,
    val shortDescHindi: String,
    val shortDescEnglish: String,
    val fullDescHindi: String = "",
    val fullDescEnglish: String = "",
    val allowedFoodHindi: String,
    val allowedFoodEnglish: String,
    val forbiddenFoodHindi: String,
    val forbiddenFoodEnglish: String,
    val gitaReference: String,
    val primaryMantraHindi: String,
    val primaryMantraEnglish: String,
    val tradition: String = "DRIK_VAISHNAVA_IST",
    val sourceReference: String = "Vedic Panchang / Indian Astronomical Ephemeris"
)
