package com.fp.flibustapicker.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fp.flibustapicker.api.FlibustaApi
import com.fp.flibustapicker.models.BookModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchViewModel : ViewModel() {
    private val repository: FlibustaApi = FlibustaApi()
    var responseFromBookSearchSaved: List<BookModel> = listOf()
    var responseBookPageSaved: BookModel? = null

    fun searchBook(bookName: String): MutableLiveData<List<BookModel>> {
        val mutableResponse: MutableLiveData<List<BookModel>> = MutableLiveData()
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                val findBooks = repository.findBooksByName(bookName)
                responseFromBookSearchSaved = findBooks
                mutableResponse.postValue(findBooks)
            }
        }

        return mutableResponse
    }

    fun getBookPage(book: BookModel): MutableLiveData<BookModel> {
        val bookComplexity: MutableLiveData<BookModel> = MutableLiveData()
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                val bookPageCompletedModel = repository.getBookPage(book)
                responseBookPageSaved = bookPageCompletedModel
                bookComplexity.postValue(bookPageCompletedModel)
            }
        }

        return bookComplexity
    }
}