package com.example.thecat_alogue

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
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

    @JvmStatic
    @JvmOverloads
    fun downloadImage(context: Context, url: String?, catId: String? = null) {
        if (url.isNullOrEmpty()) {
            Toast.makeText(context, "Invalid image URL", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val extension = when {
                url.endsWith(".gif", ignoreCase = true) -> "gif"
                url.endsWith(".png", ignoreCase = true) -> "png"
                url.endsWith(".webp", ignoreCase = true) -> "webp"
                else -> "jpg"
            }

            val fileName = "cat_${catId ?: System.currentTimeMillis()}.$extension"

            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager
            if (downloadManager != null) {
                val request = DownloadManager.Request(Uri.parse(url)).apply {
                    setTitle("Downloading Cat Picture")
                    setDescription("Saving cat image ($fileName) to Downloads...")
                    setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                    setAllowedOverMetered(true)
                    setAllowedOverRoaming(true)
                }
                downloadManager.enqueue(request)
                Toast.makeText(context, context.getString(R.string.downloading_image), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Download manager unavailable", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(
                context,
                context.getString(R.string.download_failed, e.localizedMessage ?: "Unknown error"),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}

