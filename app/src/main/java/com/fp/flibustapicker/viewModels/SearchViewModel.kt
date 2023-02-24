package com.fp.flibustapicker.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fp.flibustapicker.api.FlibustaApi
import com.fp.flibustapicker.models.BookModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchViewModel: ViewModel() {
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
}