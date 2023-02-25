package com.fp.flibustapicker.helpers

import android.annotation.SuppressLint
import android.app.Activity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.fp.flibustapicker.viewModels.NotificationsViewModel
import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.*
import java.io.IOException

class DownloadSpeedCounter(
    private val responseBody: ResponseBody,
    private val activity: Activity,
    val notificationsViewModel: NotificationsViewModel
) : ResponseBody() {
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
        val percentage = bytesRead / (fullSize / 100)

        val notificationBuilder: NotificationCompat.Builder = notificationsViewModel.notificationBuilder
        val notificationBuilder2: NotificationCompat.Builder = notificationsViewModel.notificationBuilder2
        val notificationManager: NotificationManagerCompat = notificationsViewModel.notificationManager

        if (percentage != 100L) {
            notificationManager.notify(
                3,
                notificationBuilder2
                    .setContentTitle("Downloading from Flibusta")
                    .setContentText("${percentage}/${100}")
                    .setProgress(100, percentage.toInt(), false).build()
            )
        } else {
            notificationManager.notify(
                3,
                notificationBuilder
                    .setContentTitle("Completed!")
                    .setContentText("")
                    .setContentIntent(null)
                    .clearActions()
                    .setProgress(0, 0, false).build()
            )
        }
    }
}
