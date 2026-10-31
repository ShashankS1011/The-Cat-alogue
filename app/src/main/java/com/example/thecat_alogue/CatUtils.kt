package com.example.thecat_alogue

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Calendar

/**
 * Utility functions for greeting messages, media downloads, and sharing.
 * Image download and favourites enhancements co-authored with @Butteryjam.
 */
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
    fun shareImageWithFileProvider(context: Context, url: String?) {
        if (url.isNullOrEmpty()) {
            Toast.makeText(context, "Invalid cat image URL", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val downloadedFile = Glide.with(context.applicationContext)
                    .asFile()
                    .load(url)
                    .submit()
                    .get()

                val sharedDir = File(context.cacheDir, "images").apply { mkdirs() }
                val sharedFile = File(sharedDir, "cat_shared_${System.currentTimeMillis()}.jpg")
                downloadedFile.copyTo(sharedFile, overwrite = true)

                val contentUri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    sharedFile
                )

                withContext(Dispatchers.Main) {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "image/*"
                        putExtra(Intent.EXTRA_STREAM, contentUri)
                        putExtra(Intent.EXTRA_TEXT, formatShareMessage(url))
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share_via)))
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, formatShareMessage(url))
                    }
                    context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share_via)))
                }
            }
        }
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
