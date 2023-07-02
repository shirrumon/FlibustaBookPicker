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
    private lateinit var listAdapter: SearchListAdapter
    private lateinit var binding: SearchListElementBinding
    private var taskListEntities: List<BookModel> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        initFragment(view)

        return view
    }

    private fun initFragment(view: View) {
        val searchViewModel = ViewModelProvider(requireActivity())[SearchViewModel::class.java]

        val searchBar: SearchBar = view.findViewById(R.id.search_bar)
        val searchView: SearchView = view.findViewById(R.id.search_view)
        binding = SearchListElementBinding.inflate(this.layoutInflater)
        listAdapter = SearchListAdapter(requireActivity())


        initAdapterView(view, searchViewModel)

        searchView
            .editText
            .setOnEditorActionListener { _, _, _ ->
                searchBar.text = searchView.text
                searchView.hide()
                loadBooksList(view, searchView.text.toString(), searchViewModel)
                true
            }
    }

    private fun initAdapterView(view: View?, searchViewModel: SearchViewModel) = with(binding) {
        view?.findViewById<RecyclerView>(R.id.archive_recycler)?.apply {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            adapter = listAdapter
        }

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