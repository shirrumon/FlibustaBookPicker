package com.fp.flibustapicker.adapters

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fp.flibustapicker.R
import com.fp.flibustapicker.api.FlibustaApi
import com.fp.flibustapicker.databinding.SearchListElementBinding
import com.fp.flibustapicker.models.BookModel
import com.fp.flibustapicker.viewModels.NotificationsViewModel

class SearchListAdapter(
    private val activity: Activity
) :
    ListAdapter<BookModel, SearchListAdapter.MainViewHolder>(ItemComparator()) {

    private val flibustaApi: FlibustaApi = FlibustaApi()

    class MainViewHolder(private val binding: SearchListElementBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(taskList: BookModel) = with(binding) {
            bookName.text = taskList.bookName
        }

        companion object {
            fun create(parent: ViewGroup): MainViewHolder {
                return MainViewHolder(
                    SearchListElementBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    class ItemComparator : DiffUtil.ItemCallback<BookModel>() {
        override fun areItemsTheSame(oldItem: BookModel, newItem: BookModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: BookModel, newItem: BookModel): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        holder.itemView.setOnClickListener { view ->

            item.fbLink?.let { fbLink ->
                Log.d("e", "open")
                val fbButton = view.findViewById<Button>(R.id.downloadFb2)
                fbButton.visibility = View.VISIBLE
                fbButton.setOnClickListener {
                    flibustaApi.downloadBook(
                        fbLink,
                        activity
                    )
                }
            }

            item.mobiLink?.let { mobiLink ->
                Log.d("e", "open")
                val mobiButton = view.findViewById<Button>(R.id.downloadMobi)
                mobiButton.visibility = View.VISIBLE
                mobiButton.setOnClickListener {
                    flibustaApi.downloadBook(
                        mobiLink,
                        activity
                    )
                }
            }

            item.pdfLink?.let { pdfLink ->
                Log.d("e", "open")
                val pdfButton = view.findViewById<Button>(R.id.downloadPdf)
                pdfButton.visibility = View.VISIBLE
                pdfButton.setOnClickListener {
                    flibustaApi.downloadBook(
                        pdfLink,
                        activity
                    )
                }
            }

            item.epubLink?.let { epubLink ->
                Log.d("e", "open")
                val epubButton = view.findViewById<Button>(R.id.downloadEpub)
                epubButton.visibility = View.VISIBLE
                epubButton.setOnClickListener {
                    flibustaApi.downloadBook(
                        epubLink,
                        activity
                    )
                }
            }

            item.txtLink?.let { txtLink ->
                Log.d("e", "open")
                val txtButton = view.findViewById<Button>(R.id.downloadTxt)
                txtButton.visibility = View.VISIBLE
                txtButton.setOnClickListener {
                    flibustaApi.downloadBook(
                        txtLink,
                        activity
                    )
                }
            }

            item.rtfLink?.let { rtfLink ->
                Log.d("e", "open")
                val rtfButton = view.findViewById<Button>(R.id.downloadRtf)
                rtfButton.visibility = View.VISIBLE
                rtfButton.setOnClickListener {
                    flibustaApi.downloadBook(
                        rtfLink,
                        activity
                    )
                }
            }

            val downloadButtonsLayout =
                view.findViewById<LinearLayout>(R.id.downloadButtonLayout)

            downloadButtonsLayout.visibility = when (downloadButtonsLayout.visibility) {
                View.GONE -> View.VISIBLE
                View.VISIBLE -> View.GONE
                View.INVISIBLE -> View.VISIBLE
                else -> View.GONE
            }
        }
    }
}