package com.satjanut.news.extension

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.chrono.BaseChronology
import org.joda.time.chrono.BuddhistChronology
import org.joda.time.format.DateTimeFormat
import org.ocpsoft.prettytime.PrettyTime
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone


fun String.convertToDateTime(
    pattern: String = "yyyy-MM-dd'T'HH:mm:ss'Z",
    locale: Locale = Locale("th", "TH"),
    zone: DateTimeZone? = DateTimeZone.UTC,
    chronology: BaseChronology = BuddhistChronology.getInstance()
): DateTime? = runCatching {
    DateTimeFormat.forPattern(pattern)
        .withLocale(locale)
        .withChronology(chronology)
        .run {
            if (zone != null) withZone(zone)
            else this
        }
        .parseDateTime(this)
}.getOrDefault(null)

fun String?.toDateTimeString(): String {
    return this?.convertToDateTime(
        locale = Locale.US
    )?.let {
        val calendar = Calendar.getInstance()
        calendar.set(
            it.year,
            it.monthOfYear - 1,
            it.dayOfMonth,
            it.hourOfDay,
            it.minuteOfHour,
            it.secondOfMinute
        )
        calendar.timeZone = TimeZone.getTimeZone("GMT")
        PrettyTime().format(calendar)
    }.orEmpty()
}
