package com.fp.flibustapicker.api

import android.app.Activity
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import com.fp.flibustapicker.MainActivity.Companion.applicationContext
import com.fp.flibustapicker.helpers.DownloadSpeedCounter
import com.fp.flibustapicker.models.BookModel
import com.fp.flibustapicker.viewModels.NotificationsViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import okhttp3.*
import org.jsoup.Jsoup
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.concurrent.TimeUnit

class FlibustaApi(val notificationsViewModel: NotificationsViewModel) {
    private val baseUrl = "https://flibusta.site"
    private val client = OkHttpClient
        .Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(2800, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addNetworkInterceptor { chain ->
            val originalResponse = chain.proceed(chain.request())
            val originalBody = originalResponse.body
            originalBody!!.let { response ->
                originalResponse.newBuilder()
                    .body(DownloadSpeedCounter(response, applicationContext() as Activity, notificationsViewModel))
                    .build()
            }
        }
        .build()

    fun findBooksByName(bookName: String): List<BookModel> {
        val result = mutableListOf<BookModel>()

        val doc = Jsoup.connect(
            "$baseUrl/booksearch?ask=" + bookName.replace(
                "\\s".toRegex(),
                "+"
            )
        ).get()

        doc.select("div#main").select("li").select("a")
            .forEach { booksInSearch ->
                if (booksInSearch.attr("href").contains("/b/", ignoreCase = true)) {
                    val bookArticle = Jsoup.parse(booksInSearch.html()).text()
                    val bookLink = booksInSearch.attr("href")

                    val bookModel = BookModel(bookArticle)
                    val docInside = Jsoup.connect(
                        baseUrl + bookLink
                    ).get()
                    docInside.select("div.g-network_literature").select("a")
                        .forEach { bookPage ->
                            if (bookPage.attr("href").contains("/b/", ignoreCase = true)) {
                                val regex = "([^\\/]+\$)".toRegex()
                                when (regex.find(bookPage.attr("href"))?.value) {
                                    "fb2" -> bookModel.fbLink = bookLink + "/fb2"
                                    "txt" -> bookModel.txtLink = bookLink + "/txt"
                                    "pdf" -> bookModel.pdfLink = bookLink + "/pdf"
                                    "epub" -> bookModel.epubLink = bookLink + "/epub"
                                    "mobi" -> bookModel.mobiLink = bookLink + "/mobi"
                                    "rtf" -> bookModel.rtfLink = bookLink + "/rtf"
                                }
                            }
                        }

                    result.add(bookModel)
                }
            }

        return result
    }

    fun downloadFb2(bookId: String, activity: Activity, extension: String) {
        val request = Request.Builder()
            .url("$baseUrl$bookId/$extension")
            .build()

        Log.d("Current url", "$baseUrl$bookId/$extension")
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            @RequiresApi(Build.VERSION_CODES.Q)
            override fun onResponse(call: Call, response: Response) {
                val actualExtension = when(extension) {
                    "fb2" -> "fb2.zip"
                    "mobi" -> "fb2.mobi"
                    "epub" -> "fb2.epub"
                    else -> {
                        "pdf"
                    }
                }

                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
                    writeFile(
                        bookId.filter { it.isDigit() },
                        actualExtension,
                        activity,
                        response.body!!
                    )
                }
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun writeFile(
        fileName: String,
        extension: String,
        activity: Activity,
        body: ResponseBody
    ) {
//        val dir = File(activity.filesDir, "external_files")
//        if (!dir.exists()) {
//            dir.mkdir()
//        }
//        val filename: String = "$fileName.$extension" //bookId.filter { it.isDigit() }
//        val downloadedFile = File(dir, filename)
//        downloadedFile.createNewFile()

        var dir: File? = null
        dir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS + "/Books")
                    .toString()
            )
        } else {
            File(Environment.getExternalStorageDirectory().toString() + "/Books")
        }

        if (!dir.exists()) {
            val success = dir.mkdirs()
            if (!success) {
                dir = null
            }
        } else {
            val filename = "$fileName.$extension"
            val downloadedFile = File(dir, filename)
            downloadedFile.createNewFile()

            val inputStream = body.byteStream()
            val fileReader = ByteArray(4096)
            var sizeOfDownloaded = 0
            val fos: OutputStream = FileOutputStream(downloadedFile)

            do {
                val read = inputStream.read(fileReader)
                if (read != -1) {
                    fos.write(fileReader, 0, read)
                    sizeOfDownloaded += read
                }
            } while (read != -1)

            fos.flush()
            fos.close()

            //FileProvider.getUriForFile(activity, "com.example.fileprovider", dir)
        }
    }
}