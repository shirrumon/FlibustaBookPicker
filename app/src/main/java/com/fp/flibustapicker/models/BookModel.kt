package com.fp.flibustapicker.models

data class BookModel(
    val bookName: String,
    var fbLink: String? = null,
    var mobiLink: String? = null,
    var pdfLink: String? = null,
    var epubLink: String? = null,
    var txtLink: String? = null,
    var rtfLink: String? = null
)
