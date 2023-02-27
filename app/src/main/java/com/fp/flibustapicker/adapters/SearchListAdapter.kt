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
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fp.flibustapicker.R
import com.fp.flibustapicker.api.FlibustaApi
import com.fp.flibustapicker.databinding.SearchListElementBinding
import com.fp.flibustapicker.fragments.BookPageFragment
import com.fp.flibustapicker.models.BookModel
import com.fp.flibustapicker.viewModels.NotificationsViewModel

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
        val item = getItem(position)
        holder.bind(item)
        holder.itemView.setOnClickListener {
            val bookPageFragment = BookPageFragment(item)

            activity.supportFragmentManager
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.fragment_container, bookPageFragment)
                .addToBackStack(null)
                .commit()
        }
    }
}