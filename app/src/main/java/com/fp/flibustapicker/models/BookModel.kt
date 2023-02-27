package com.fp.flibustapicker.models

data class BookModel(
    val bookName: String,
    val bookId: Int,
    var bookAuthor: String? = null,
    var bookDescription: String? = null,
    var bookImage: String? = null,
    var fbLink: String? = null,
    var mobiLink: String? = null,
    var pdfLink: String? = null,
    var epubLink: String? = null,
    var txtLink: String? = null,
    var rtfLink: String? = null
)
