package com.fp.flibustapicker.api

import android.app.Activity
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import com.fp.flibustapicker.MainActivity.Companion.applicationContext
import com.fp.flibustapicker.MainActivity.Companion.getActivity
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

class FlibustaApi {
    private val baseUrl = "https://flibusta.club"
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
                    .body(DownloadSpeedCounter(response, getActivity()!!))
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
                    docInside.select("div.b_download").select("span")
                        .forEach { downloadLink ->
                            val eventRegex = "'(.*?)'".toRegex()
                            val regex = "([^\\/]+\$)".toRegex()
                            val downloadBookLinkClear = eventRegex.find(downloadLink.attr("onclick"))?.value?.replace("'", "")
                            val stringToFind = downloadBookLinkClear?.let { string ->
                                regex.find(
                                    string
                                )?.value
                            }
                            if(stringToFind != null) {
                                Log.d("e", stringToFind)
                                when (stringToFind) {
                                    "?format=fb2.zip" -> bookModel.fbLink = downloadBookLinkClear
                                    "?format=txt.zip" -> bookModel.txtLink = downloadBookLinkClear
                                    "?format=pdf" -> bookModel.pdfLink = downloadBookLinkClear
                                    "?format=epub" -> bookModel.epubLink = downloadBookLinkClear
                                    "?format=mobi" -> bookModel.mobiLink = downloadBookLinkClear
                                    "?format=rtf.zip" -> bookModel.rtfLink = downloadBookLinkClear
                                }
                            }
                        }

                    //Log.d("e", bookModel.toString())
                    result.add(bookModel)
                }
            }

        return result
    }

    fun downloadBook(
        downloadLink: String,
        activity: Activity
    ) {
        val request = Request.Builder()
            .url("$baseUrl$downloadLink")
            .build()

        val regexpExtractExtension = "[^=]*$".toRegex()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            @RequiresApi(Build.VERSION_CODES.Q)
            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
                    writeFile(
                        downloadLink.filter { it.isDigit() },
                        regexpExtractExtension.find(downloadLink)?.value!!,
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
        body: ResponseBody
    ) {
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
            val filename = "$fileName.html"
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
        }
    }
}