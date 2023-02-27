package com.fp.flibustapicker.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fp.flibustapicker.api.FlibustaApi
import com.fp.flibustapicker.api.FlibustaApi.Companion.BASE_URL
import com.fp.flibustapicker.models.BookModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

class SearchViewModel : ViewModel() {
    private val repository: FlibustaApi = FlibustaApi()

    fun searchBook(bookName: String): MutableLiveData<List<BookModel>> {
        val mutableResponse: MutableLiveData<List<BookModel>> = MutableLiveData()
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                mutableResponse.postValue(repository.findBooksByName(bookName))
            }
        }

        return mutableResponse
    }

    fun getBookPage(book: BookModel): MutableLiveData<BookModel> {
        val bookComplexity: MutableLiveData<BookModel> = MutableLiveData()
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                val docInside = Jsoup.connect(
                    "${BASE_URL}/b/${book.bookId}"
                ).get()
                docInside.select("div#main").select("a")
                    .forEach { downloadLink ->
                        if(downloadLink.attr("href").contains("/a/")) {
                            if (downloadLink.parent()?.attr("id").equals("main")) {
                                book.bookAuthor = downloadLink.html()
                            }
                        } else {
                            val regex = "([^\\/]+\$)".toRegex()
                            val extensionsLink = regex.find(downloadLink.attr("href"))?.value
                            if (extensionsLink != null) {
                                when (extensionsLink) {
                                    "fb2" -> book.fbLink = downloadLink.attr("href")
                                    "txt" -> book.txtLink = downloadLink.attr("href")
                                    "pdf" -> book.pdfLink = downloadLink.attr("href")
                                    "epub" -> book.epubLink = downloadLink.attr("href")
                                    "mobi" -> book.mobiLink = downloadLink.attr("href")
                                    "rtf" -> book.rtfLink = downloadLink.attr("href")
                                }
                            }
                        }
                    }

                docInside.select("div#main").select("img")
                    .forEach { image ->
                        if (image.attr("title").equals("Cover image")) {
                            book.bookImage = "$BASE_URL${image.attr("src")}"
                        }
                    }

                docInside.select("div#main").select("p")
                    .forEach { description ->
                        if (description.parent()?.attr("id").equals("main")) {
                            book.bookDescription = description.text()
                        }
                    }

                bookComplexity.postValue(book)
            }
        }
        return bookComplexity
    }
}