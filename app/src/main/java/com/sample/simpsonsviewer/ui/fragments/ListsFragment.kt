package com.sample.simpsonsviewer.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.sample.simpsonsviewer.adaptors.RelatedTopicAdapter
import com.sample.simpsonsviewer.data.model.RelatedTopic
import com.sample.simpsonsviewer.databinding.FragmentListBinding
import com.sample.simpsonsviewer.ui.viewModels.SimpsonsViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class ListsFragment : Fragment(), RelatedTopicAdapter.OnItemClickListener {

    private var _binding: FragmentListBinding? = null
    private val binding: FragmentListBinding
        get() = _binding!!

    private val viewModel: SimpsonsViewModel by viewModels()

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

