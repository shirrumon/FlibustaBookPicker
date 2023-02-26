package com.fp.flibustapicker.helpers

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Notification
import android.app.NotificationManager
import android.content.Context.NOTIFICATION_SERVICE
import android.graphics.Color
import android.util.Log
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import com.fp.flibustapicker.MainActivity.Companion.applicationContext
import com.fp.flibustapicker.R
import okhttp3.MediaType
import okhttp3.ResponseBody
import okhttp3.internal.notify
import okio.*
import java.io.IOException

class DownloadSpeedCounter(
    private val responseBody: ResponseBody,
    private val activity: Activity
) : ResponseBody() {
    private val mChannel = NotificationChannelCompat.Builder("Ch_1", NotificationManagerCompat.IMPORTANCE_DEFAULT).apply {
        setName("channel name") // Must set! Don't remove
        setDescription("channel description")
        setLightsEnabled(true)
        setLightColor(Color.RED)
    }.build()

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

            override fun read(sink: Buffer, byteCount: Long): Long {
                var bytesRead = 0L
                try {
                    bytesRead = super.read(sink, byteCount)
                    totalBytesRead += if (bytesRead != -1L) bytesRead else 0
                    activity.runOnUiThread(Runnable {
                        progressBar(responseBody.contentLength(), totalBytesRead)
                    })

                    count += 1
                    if (bytesRead == -1L) {
                        //Log.e("end", count.toString())
                    }

                } catch (e: Exception) {
                    throw e
                }
                return bytesRead
            }
        }

    @SuppressLint("MissingPermission")
    fun progressBar(fullSize: Long, bytesRead: Long) {
        NotificationManagerCompat.from(activity).createNotificationChannel(mChannel)
        val percentage = bytesRead / (fullSize / 100)
        val notification: Notification = NotificationCompat.Builder(activity, "Ch_1")
            .setSmallIcon(R.drawable.baseline_notifications_24)
            .setContentTitle("Downloading from Flibusta")
            .setContentText("${percentage}/${100}")
            .setProgress(100, percentage.toInt(), false)
            .build()

        if (percentage != 101L) {
            NotificationManagerCompat.from(activity).notify(1, notification)
        }
//        } else {
//            val notificationEnded: Notification = NotificationCompat.Builder(activity, "Ch_1")
//                .setSmallIcon(R.drawable.baseline_notifications_24)
//                .setContentTitle("Completed!")
//                .setContentText("")
//                .setContentIntent(null)
//                .clearActions()
//                .setProgress(0, 0, false)
//                .build()
//
//            NotificationManagerCompat.from(activity).notify(2, notificationEnded)
//        }
    }
}
