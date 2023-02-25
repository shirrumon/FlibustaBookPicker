package com.fp.flibustapicker.api

import android.app.Activity
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.fp.flibustapicker.MainActivity.Companion.applicationContext
import com.fp.flibustapicker.helpers.DownloadSpeedCounter
import com.fp.flibustapicker.models.BookModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.jsoup.Jsoup
import java.util.concurrent.TimeUnit

class FlibustaApi {
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
                    .body(DownloadSpeedCounter(response, applicationContext() as Activity))
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
}