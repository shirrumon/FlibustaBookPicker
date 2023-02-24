package com.fp.flibustapicker.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fp.flibustapicker.R
import com.fp.flibustapicker.adapters.SearchListAdapter
import com.fp.flibustapicker.databinding.SearchListElementBinding
import com.fp.flibustapicker.models.BookModel
import com.fp.flibustapicker.viewModels.SearchViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.search.SearchBar
import com.google.android.material.search.SearchView

class SearchFragment : Fragment() {
    private val listAdapter: SearchListAdapter = SearchListAdapter()
    private lateinit var binding: SearchListElementBinding
    private var taskListEntities: List<BookModel> = arrayListOf()
    private lateinit var searchViewModel: SearchViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        val searchBar: SearchBar = view.findViewById(R.id.search_bar)
        val searchView: SearchView = view.findViewById(R.id.search_view)
        binding = SearchListElementBinding.inflate(inflater)
        searchViewModel = ViewModelProvider(requireActivity())[SearchViewModel::class.java]

        initAdapterView(view)

        searchView
            .editText
            .setOnEditorActionListener { v, actionId, event ->
                loadBooksList(view, searchView.text.toString())
                searchBar.text = searchView.text
                searchView.hide()
                true
            }

        return view
    }

    private fun initAdapterView(view: View?) = with(binding) {
        val recyclerView: RecyclerView = view?.findViewById(R.id.archive_recycler)!!
        recyclerView.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = listAdapter
    }

    private fun loadBooksList(view: View, searchString: String) {
        val emptyCommunicate: Chip = view.findViewById(R.id.empty_list_communicate)
        searchViewModel.searchBook(searchString).observe(viewLifecycleOwner) { findData ->
            if (findData.isEmpty()) {
                emptyCommunicate.visibility = View.VISIBLE
            } else {
                emptyCommunicate.visibility = View.GONE
                listAdapter.submitList(findData)
                taskListEntities = findData
            }
        }
    }
}