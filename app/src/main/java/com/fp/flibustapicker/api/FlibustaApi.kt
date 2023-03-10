package com.fp.flibustapicker.api

import android.os.Build
import androidx.annotation.RequiresApi
import com.fp.flibustapicker.MainActivity.Companion.getActivity
import com.fp.flibustapicker.helpers.DownloadSpeedCounter
import com.fp.flibustapicker.models.BookModel
import com.fp.flibustapicker.utils.FileUtils
import okhttp3.*
import org.jsoup.Jsoup
import java.io.IOException
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

class FlibustaApi {
    private val client = OkHttpClient
        .Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(180, TimeUnit.SECONDS)
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
            "$BASE_URL/booksearch?ask=" + URLEncoder.encode(bookName, "utf-8")
        ).get()

        URLEncoder.encode(bookName, "utf-8")

        doc.select("div#main").select("li").select("a")
            .forEach { booksInSearch ->
                if (booksInSearch.attr("href").contains("/b/", ignoreCase = true)) {
                    val bookArticle = Jsoup.parse(booksInSearch.html()).text()
                    val bookLink = booksInSearch.attr("href")

                    val bookModel = BookModel(
                        bookArticle,
                        bookLink.filter { it.isDigit() }.toInt()
                    )

                    result.add(bookModel)
                }
            }

        return result
    }

    fun downloadBook(
        downloadLink: String
    ) {
        val request = Request.Builder()
            .url("$BASE_URL$downloadLink")
            .build()

        val regexpExtractExtension = "([^\\/]+\$)".toRegex()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            val extensionResolver = when (regexpExtractExtension.find(downloadLink)?.value!!) {
                "fb2" -> "fb2.zip"
                "epub" -> "fb2.epub"
                "mobi" -> "fb2.mobi"
                "rtf" -> "fb2.rtf"
                "pdf" -> "pdf"
                "txt" -> "fb2.txt"
                else -> "fb2.zip"
            }

            @RequiresApi(Build.VERSION_CODES.Q)
            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
                    FileUtils().writeFile(
                        downloadLink.filter { it.isDigit() },
                        extensionResolver,
                        response.body!!
                    )
                }
            }
        })
    }

    companion object {
        const val BASE_URL = "http://proxi.flibusta.is/"
    }
}