package com.fp.flibustapicker.helpers

import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.fp.flibustapicker.R
import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.*
import java.io.IOException

class DownloadSpeedCounter(
    private val responseBody: ResponseBody,
    activity: Activity
) : ResponseBody() {
    @RequiresApi(Build.VERSION_CODES.O)
    private val channel = NotificationChannel(
        "ch#1",
        "downloadsNotify",
        NotificationManager.IMPORTANCE_DEFAULT
    ).apply {
        lightColor = Color.BLUE
        enableLights(true)
    }
    private val notificationManager: NotificationManagerCompat =
        NotificationManagerCompat.from(activity)

    private val notificationBuilder: NotificationCompat.Builder =
        NotificationCompat.Builder(activity, "ch#1")

    private val bufferedSource = initSource(responseBody.source()).buffer()
    var count = 0

    override fun contentLength(): Long = responseBody.contentLength()

    override fun contentType(): MediaType? =
        responseBody.contentType()

    override fun source(): BufferedSource {
        return bufferedSource
    }

    @Throws(IOException::class)
    private fun initSource(source: Source): Source =
        object : ForwardingSource(source) {
            var totalBytesRead: Long = 0L

            @RequiresApi(Build.VERSION_CODES.O)
            @SuppressLint("MissingPermission")
            override fun read(sink: Buffer, byteCount: Long): Long {
                var bytesRead = 0L
                try {
                    bytesRead = super.read(sink, byteCount)
                    totalBytesRead += if (bytesRead != -1L) bytesRead else 0
                    val percentage = totalBytesRead / (responseBody.contentLength() / 100)
                    notificationManager.createNotificationChannel(channel)
                    notificationManager.notify(
                        3, notificationBuilder
                            .setSmallIcon(R.drawable.baseline_notifications_24)
                            .setContentTitle("Downloading from Flibusta")
                            .setContentText("${percentage}/${100}")
                            .setProgress(100, percentage.toInt(), false).build()
                    )

                    count += 1
                    if (bytesRead == -1L) {
                        notificationManager.cancel(3)
                        notificationManager.notify(
                            4, notificationBuilder
                                .setSmallIcon(R.drawable.baseline_notifications_24)
                                .setContentTitle("Completed!")
                                .setContentText("")
                                .setContentIntent(null)
                                .clearActions()
                                .setProgress(0, 0, false).build()
                        )
                    }

                } catch (e: Exception) {
                    throw e
                }
                return bytesRead
            }
        }
}
