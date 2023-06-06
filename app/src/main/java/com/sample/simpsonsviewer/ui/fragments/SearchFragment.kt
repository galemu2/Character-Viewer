package com.sample.simpsonsviewer.ui.fragments

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import com.sample.simpsonsviewer.R
import com.sample.simpsonsviewer.adaptors.SearchListAdapter
import com.sample.simpsonsviewer.databinding.FragmentSearchBinding
import com.sample.simpsonsviewer.ui.viewModels.SimpsonsViewModel
import com.sample.simpsonsviewer.util.Resource


class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

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

        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        registerNetworkStateMonitor()

        val adapter = SearchListAdapter({
            Toast.makeText(requireContext(), "${it.Text}", Toast.LENGTH_SHORT).show()
        })
        binding.searchRecyclerView.setHasFixedSize(true)
        binding.searchRecyclerView.adapter = adapter

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu, menu)
                val menuItem = menu.findItem(R.id.action_search)
                val searchView = (menuItem.actionView as SearchView)
                searchView.queryHint = "Search character"
                searchView.setOnQueryTextListener(object : OnQueryTextListener {

                    override fun onQueryTextSubmit(query: String?): Boolean {
                        if (!query.isNullOrBlank()) {

                            if (viewModel.networkState.value!!) {
                                viewModel.getSimpsonsCharacters(query = query)
                                viewModel.searchedCharacters.observe(viewLifecycleOwner) {
                                    if (it is Resource.Success) {
                                        adapter.submitList(it.data)
                                    }
                                }
                            }
                            hideSoftKeyboard(searchView)
                            return true
                        }
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        viewModel.searchedCharacters = MutableLiveData()
                        adapter.submitList(emptyList())
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

        return binding.root
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}