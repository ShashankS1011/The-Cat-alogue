package com.example.thecat_alogue

import java.util.Calendar

object CatUtils {

    @JvmStatic
    @JvmOverloads
    fun getTimeGreeting(hour: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)): Int {
        return when (hour) {
            in 5..11 -> R.string.good_morning
            in 12..16 -> R.string.good_afternoon
            in 17..20 -> R.string.good_evening
            else -> R.string.hello_night_owl
        }
    }

    @JvmStatic
    fun formatShareMessage(url: String): String {
        return "Check out this cat!\n$url"
    }
}
