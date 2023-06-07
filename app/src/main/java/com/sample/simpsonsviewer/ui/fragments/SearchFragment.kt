package com.sample.simpsonsviewer.ui.fragments

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.sample.simpsonsviewer.R
import com.sample.simpsonsviewer.adaptors.SearchListAdapter
import com.sample.simpsonsviewer.data.model.RelatedTopic
import com.sample.simpsonsviewer.databinding.FragmentSearchBinding
import com.sample.simpsonsviewer.ui.viewModels.SimpsonsViewModel
import com.sample.simpsonsviewer.util.Resource


class SearchFragment : Fragment() {

    private lateinit var binding :FragmentSearchBinding

    private val viewModel: SimpsonsViewModel by viewModels()

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            viewModel.networkState.postValue(true)
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            viewModel.networkState.postValue(false)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentSearchBinding.inflate(inflater, container, false)
        registerNetworkStateMonitor()

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val adapter = setSearchListAdapterToRecyclerView {
            val action = SearchFragmentDirections.actionFragmentSearchDialogToFragmentDetails(it)
            findNavController().navigate(directions = action)

        }

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {

                val searchView = setupMenu(menuInflater, menu)

                searchView.setOnQueryTextListener(object : OnQueryTextListener {

                    override fun onQueryTextSubmit(query: String?): Boolean {

                        showProgressBar()
                        handelQuery(query, adapter)

                        hideSoftKeyboard(searchView)
                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        viewModel.searchedCharacters = MutableLiveData()
                        adapter.submitList(emptyList())
                        showSearchStart()
                        return true
                    }
                })

            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == R.id.action_search) {

                    return true
                }
                return false
            }
        }, viewLifecycleOwner)
    }

    private fun setupMenu(
        menuInflater: MenuInflater,
        menu: Menu
    ): SearchView {
        menuInflater.inflate(R.menu.menu, menu)
        val menuItem = menu.findItem(R.id.action_search)
        val searchView = (menuItem.actionView as SearchView)
        searchView.queryHint = "Search character"
        return searchView
    }


    private fun handelQuery(
        query: String?,
        adapter: SearchListAdapter
    ) {
        if (!query.isNullOrBlank()) {

            showRecyclerView()

            if (viewModel.networkState.value!!) {
                viewModel.getSimpsonsCharacters(query = query)
                viewModel.searchedCharacters.observe(viewLifecycleOwner) {
                    if (it is Resource.Success) {
                        it.data?.let { relatedTopics ->
                            if (relatedTopics.isEmpty()) {
                                adapter.submitList(emptyList())
                                showEmptyList()
                            } else
                                adapter.submitList(relatedTopics)

                        } ?: {
                            adapter.submitList(emptyList())
                            showEmptyList()
                        }
                    }
                    if (it is Resource.Error) {
                        adapter.submitList(emptyList())
                        showEmptyList()
                    }
                }
            } else {
                adapter.submitList(emptyList())
                showEmptyList()
            }
        }
    }

    private fun setSearchListAdapterToRecyclerView(onItemClickListener: (RelatedTopic) -> Unit):
            SearchListAdapter {
        val adapter = SearchListAdapter(onItemClickListener = onItemClickListener)
        binding.searchRecyclerView.setHasFixedSize(true)
        binding.searchRecyclerView.adapter = adapter
        return adapter
    }

    private fun showRecyclerView() {
        binding.searchStart.visibility = View.GONE
        binding.searchProgress.visibility = View.GONE
        binding.searchRecyclerView.visibility = View.VISIBLE
        binding.emptyListImage.visibility = View.GONE
    }

    private fun showProgressBar() {
        binding.searchStart.visibility = View.GONE
        binding.searchRecyclerView.visibility = View.GONE
        binding.searchProgress.visibility = View.VISIBLE
        binding.emptyListImage.visibility = View.GONE
    }

    private fun showSearchStart() {
        binding.searchRecyclerView.visibility = View.GONE
        binding.searchProgress.visibility = View.GONE
        binding.searchStart.visibility = View.VISIBLE
        binding.emptyListImage.visibility = View.GONE
    }

    private fun showEmptyList() {
        binding.emptyListImage.visibility = View.VISIBLE
        binding.searchRecyclerView.visibility = View.GONE
        binding.searchProgress.visibility = View.GONE
        binding.searchStart.visibility = View.GONE
    }


    private fun registerNetworkStateMonitor() {
        val conManager =
            activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        conManager.registerDefaultNetworkCallback(networkCallback)
    }


    private fun hideSoftKeyboard(searchView: SearchView) {
        val inputMethodManager = requireActivity().getSystemService(
            Context.INPUT_METHOD_SERVICE
        ) as InputMethodManager

        inputMethodManager.hideSoftInputFromWindow(
            searchView.windowToken, 0
        )
    }


}