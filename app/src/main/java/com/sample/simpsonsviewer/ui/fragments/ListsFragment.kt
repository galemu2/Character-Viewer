package com.sample.simpsonsviewer.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.sample.simpsonsviewer.R
import com.sample.simpsonsviewer.adaptors.RelatedTopicAdapter
import com.sample.simpsonsviewer.data.model.RelatedTopic
import com.sample.simpsonsviewer.databinding.FragmentListBinding
import com.sample.simpsonsviewer.ui.viewModels.SimpsonsViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class ListsFragment : Fragment(R.layout.fragment_list), RelatedTopicAdapter.OnItemClickListener {

    private var _binding: FragmentListBinding? = null
    private val binding: FragmentListBinding
        get() = _binding!!

    private val viewModel: SimpsonsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentListBinding.bind(view)

        val adapter = RelatedTopicAdapter(this)
        binding.itemList.setHasFixedSize(true)
        binding.itemList.adapter = adapter


        lifecycleScope.launch {
            viewModel.simpsonsCharacters.collectLatest { pagingData ->
                adapter.submitData(pagingData = pagingData)
            }
        }

        binding.loadButton.setOnClickListener {
            adapter.retry()
        }

        adapter.addLoadStateListener { combinedLoadStates ->
            binding.apply {
                val currentState = combinedLoadStates.source.refresh
                progressBarContainer.isVisible = currentState is LoadState.Loading
                itemList.isVisible = currentState is LoadState.NotLoading
                listViewError.isVisible = currentState is LoadState.Error
            }

        }
    }

    override fun onItemClicked(relatedTopic: RelatedTopic) {
        Toast.makeText(requireContext(), "${relatedTopic.Icon.URL}", Toast.LENGTH_SHORT).show()
        val action = ListsFragmentDirections.actionFragmentListToFragmentDetails()
        findNavController().navigate(action)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}