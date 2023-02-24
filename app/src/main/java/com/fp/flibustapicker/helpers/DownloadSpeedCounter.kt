package com.fp.flibustapicker.helpers

import android.app.Activity
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.fp.flibustapicker.R
import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.*
import java.io.IOException

class DownloadSpeedCounter(
    private val responseBody: ResponseBody,
    private val activity: Activity
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

                   // Log.e("end", "Total: ${responseBody.contentLength()}; ByteCount: $byteCount; Iterations: ${responseBody.contentLength() / byteCount}")
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

    fun progressBar(fullSize: Long, bytesRead: Long) {
//        val percentage = bytesRead / (fullSize / 100)
//        val progressBar = activity.findViewById<ProgressBar>(R.id.progressBarSecondary)
//        val progressBarWrapper = activity.findViewById<LinearLayout>(R.id.progressBarWrapper)
//        val progressBarText = activity.findViewById<TextView>(R.id.textViewPrimary)
//
//        progressBarWrapper.visibility = View.VISIBLE
//
//        if(percentage == 100L) {
//            progressBarWrapper.visibility = View.GONE
//            return
//        } else {
//            progressBar.secondaryProgress = percentage.toInt()
//            progressBarText.text = "Complete $percentage% of 100"
//        }
    }
}
