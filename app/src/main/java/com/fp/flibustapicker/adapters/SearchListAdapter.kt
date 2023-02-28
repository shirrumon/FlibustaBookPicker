package com.fp.flibustapicker.adapters

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fp.flibustapicker.R
import com.fp.flibustapicker.api.FlibustaApi
import com.fp.flibustapicker.databinding.SearchListElementBinding
import com.fp.flibustapicker.fragments.BookPageFragment
import com.fp.flibustapicker.models.BookModel
import com.google.gson.Gson

class SearchListAdapter(
    private val activity: FragmentActivity
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
        val book = getItem(position)
        holder.bind(book)
        holder.itemView.setOnClickListener {
            val bookPageFragment = BookPageFragment()
            val bundle = Bundle()
            bundle.putString("bookModel", Gson().toJson(book))
            bookPageFragment.arguments = bundle

            activity.findNavController(R.id.nav_host_fragment).navigate(R.id.bookPageFragment, bundle)
        }
    }
}