package com.sample.simpsonsviewer.ui.fragments

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.sample.simpsonsviewer.BuildConfig
import com.sample.simpsonsviewer.adaptors.RelatedTopicAdapter
import com.sample.simpsonsviewer.data.model.RelatedTopic
import com.sample.simpsonsviewer.databinding.FragmentListBinding
import com.sample.simpsonsviewer.ui.viewModels.SimpsonsViewModel
import com.sample.simpsonsviewer.util.Resource
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class ListsFragment : Fragment(), RelatedTopicAdapter.OnItemClickListener {

    private var _binding: FragmentListBinding? = null
    private val binding: FragmentListBinding
        get() = _binding!!

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
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = setRecyclerViewAdapter()

        lifecycleScope.launch {
            viewModel.simpsonsCharacters.collectLatest { pagingData ->
                adapter.submitData(pagingData = pagingData)
            }
        }

        registerNetworkStateMonitor()

        if (viewModel.networkState.value!!) {
            viewModel.getSimpsonsCharacters("Barney")
            viewModel.searchedCharacters.observe(viewLifecycleOwner) {
                if (it is Resource.Success)
                    it.data?.forEach { relatedTopic ->

                        if (BuildConfig.DEBUG) {
                            Log.d("RelatedTopic", "onViewCreated: ${relatedTopic.Text}")
                        }
                    }

            }
        }

        binding.loadButton.setOnClickListener {
            adapter.retry()
        }

        binding.fab.setOnClickListener {
            val action = ListsFragmentDirections.actionFragmentListToFragmentSearchDialog()
            findNavController().navigate(action)
        }

        updateComponentVisibilityState(adapter)
    }

    private fun updateComponentVisibilityState(adapter: RelatedTopicAdapter) {
        adapter.addLoadStateListener { combinedLoadStates ->
            binding.apply {
                val currentState = combinedLoadStates.source.refresh
                progressBarContainer.isVisible = currentState is LoadState.Loading
                itemList.isVisible = currentState is LoadState.NotLoading
                listViewError.isVisible = currentState is LoadState.Error
                fab.isVisible = currentState is LoadState.NotLoading
            }

        }
    }

    private fun registerNetworkStateMonitor() {
        val conManager =
            activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        conManager.registerDefaultNetworkCallback(networkCallback)
    }

    private fun setRecyclerViewAdapter(): RelatedTopicAdapter {
        val adapter = RelatedTopicAdapter(this)
        binding.itemList.setHasFixedSize(true)
        binding.itemList.adapter = adapter
        return adapter
    }

    override fun onItemClicked(relatedTopic: RelatedTopic) {
        val action = ListsFragmentDirections.actionFragmentListToFragmentDetails(relatedTopic)
        findNavController().navigate(action)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}

