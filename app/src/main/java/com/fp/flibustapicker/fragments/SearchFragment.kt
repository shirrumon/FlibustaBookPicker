package com.fp.flibustapicker.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
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
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {
    private lateinit var listAdapter: SearchListAdapter
    private lateinit var binding: SearchListElementBinding
    private var taskListEntities: List<BookModel> = arrayListOf()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val searchViewModel = ViewModelProvider(requireActivity())[SearchViewModel::class.java]
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        val searchBar: SearchBar = view.findViewById(R.id.search_bar)
        val searchView: SearchView = view.findViewById(R.id.search_view)
        binding = SearchListElementBinding.inflate(inflater)
        listAdapter = SearchListAdapter(requireActivity())


        initAdapterView(view, searchViewModel)

        searchView
            .editText
            .setOnEditorActionListener { v, actionId, event ->
                loadBooksList(view, searchView.text.toString(), searchViewModel)
                searchBar.text = searchView.text
                searchView.hide()
                true
            }

        return view
    }

    private fun initAdapterView(view: View?, searchViewModel: SearchViewModel) = with(binding) {
        val recyclerView: RecyclerView = view?.findViewById(R.id.archive_recycler)!!
        recyclerView.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = listAdapter
        listAdapter.submitList(searchViewModel.responseFromBookSearchSaved)
    }

    private fun loadBooksList(view: View, searchString: String, searchViewModel: SearchViewModel) {
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