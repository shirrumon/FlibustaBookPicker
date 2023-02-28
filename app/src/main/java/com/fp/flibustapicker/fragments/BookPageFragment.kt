package com.fp.flibustapicker.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import coil.load
import coil.size.Scale
import com.fp.flibustapicker.R
import com.fp.flibustapicker.api.FlibustaApi
import com.fp.flibustapicker.models.BookModel
import com.fp.flibustapicker.viewModels.SearchViewModel
import com.google.gson.Gson

class BookPageFragment : Fragment() {
    private val flibustaApi: FlibustaApi = FlibustaApi()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_book_page, container, false)
        val searchViewModel = ViewModelProvider(requireActivity())[SearchViewModel::class.java]

        val bundle = this.arguments
        val bookModel = bundle?.getString("bookModel")

        if (bookModel == null)
            drawView(view, searchViewModel.responseBookPageSaved!!)
        else
            initBookPage(view, searchViewModel, Gson().fromJson(bookModel, BookModel::class.java))

        return view
    }

    private fun initBookPage(view: View, searchViewModel: SearchViewModel, bookModel: BookModel) {
        searchViewModel.getBookPage(bookModel).observe(viewLifecycleOwner) { book ->
            drawView(view, book)
        }
    }

    private fun drawView(view: View, book: BookModel) {
        view.findViewById<TextView>(R.id.bookName).text = book.bookName
        view.findViewById<TextView>(R.id.bookAuthor).text = book.bookAuthor
        view.findViewById<TextView>(R.id.bookDescription).text = book.bookDescription

        view.findViewById<ImageView>(R.id.bookImage).load(book.bookImage) {
            crossfade(750)
            scale(Scale.FILL)
        }

        book.fbLink?.let { fbLink ->
            val fbButton = view.findViewById<Button>(R.id.downloadFb2)
            fbButton.visibility = View.VISIBLE
            fbButton.setOnClickListener {
                flibustaApi.downloadBook(
                    fbLink
                )
            }
        }

        book.mobiLink?.let { mobiLink ->
            val mobiButton = view.findViewById<Button>(R.id.downloadMobi)
            mobiButton.visibility = View.VISIBLE
            mobiButton.setOnClickListener {
                flibustaApi.downloadBook(
                    mobiLink
                )
            }
        }

        book.pdfLink?.let { pdfLink ->
            val pdfButton = view.findViewById<Button>(R.id.downloadPdf)
            pdfButton.visibility = View.VISIBLE
            pdfButton.setOnClickListener {
                flibustaApi.downloadBook(
                    pdfLink
                )
            }
        }

        book.epubLink?.let { epubLink ->
            val epubButton = view.findViewById<Button>(R.id.downloadEpub)
            epubButton.visibility = View.VISIBLE
            epubButton.setOnClickListener {
                flibustaApi.downloadBook(
                    epubLink
                )
            }
        }

        book.txtLink?.let { txtLink ->
            val txtButton = view.findViewById<Button>(R.id.downloadTxt)
            txtButton.visibility = View.VISIBLE
            txtButton.setOnClickListener {
                flibustaApi.downloadBook(
                    txtLink
                )
            }
        }

        book.rtfLink?.let { rtfLink ->
            val rtfButton = view.findViewById<Button>(R.id.downloadRtf)
            rtfButton.visibility = View.VISIBLE
            rtfButton.setOnClickListener {
                flibustaApi.downloadBook(
                    rtfLink
                )
            }
        }
    }
}