package com.fp.flibustapicker.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fp.flibustapicker.databinding.SearchListElementBinding
import com.fp.flibustapicker.models.BookModel

class SearchListAdapter:
    ListAdapter<BookModel, SearchListAdapter.MainViewHolder>(ItemComparator()) {

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
//            val bundle = Bundle()
//            item.id?.let { id -> bundle.putInt("listId", id) }
//            bundle.putFloat("tasksSummary", item.taskSummary)
//            bundle.putString("parentListName", item.listName)
        }
    }
}