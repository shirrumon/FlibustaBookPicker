package com.fp.flibustapicker.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.fp.flibustapicker.R
import com.fp.flibustapicker.api.FlibustaApi
import com.fp.flibustapicker.models.BookModel
import com.fp.flibustapicker.viewModels.SearchViewModel
import com.squareup.picasso.Picasso

class BookPageFragment(private val bookModel: BookModel) : Fragment() {
    private val searchViewModel: SearchViewModel = SearchViewModel()
    private val flibustaApi: FlibustaApi = FlibustaApi()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_book_page, container, false)

        initBookPage(view)
        return view
    }

    private fun initBookPage(view: View) {
        searchViewModel.getBookPage(bookModel).observe(viewLifecycleOwner) { book ->
            view.findViewById<TextView>(R.id.bookName).text = book.bookName
            view.findViewById<TextView>(R.id.bookAuthor).text = book.bookAuthor
            view.findViewById<TextView>(R.id.bookDescription).text = book.bookDescription

            Picasso.get().load(book.bookImage).into(view.findViewById<ImageView>(R.id.bookImage))

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
}