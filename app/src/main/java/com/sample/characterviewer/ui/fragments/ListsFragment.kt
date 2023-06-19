package com.sample.characterviewer.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.sample.characterviewer.R
import com.sample.characterviewer.adaptors.RelatedTopicAdapter
import com.sample.characterviewer.data.model.RelatedTopic
import com.sample.characterviewer.databinding.FragmentListBinding
import com.sample.characterviewer.ui.viewModels.CharactersViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class ListsFragment : Fragment(), RelatedTopicAdapter.OnItemClickListener {

    private var _binding: FragmentListBinding? = null
    private val binding: FragmentListBinding
        get() = _binding!!

    private val viewModel: CharactersViewModel by viewModels { CharactersViewModel.Factory }
    private var itemDetailFragmentContainer: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        itemDetailFragmentContainer = binding.root.findViewById(R.id.fragmentContainerViewTablet)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val adapter = setRecyclerViewAdapter(itemDetailFragmentContainer)

        lifecycleScope.launch {
            viewModel.simpsonsCharacters.collectLatest { pagingData ->
                adapter.submitData(pagingData = pagingData)
            }
        }

        binding.loadButton.setOnClickListener {
            adapter.retry()
        }

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentList_to_fragmentSearchDialog)
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
                // TODO search view is deactivated for tablets
                fab.isVisible = currentState is LoadState.NotLoading
                        && itemDetailFragmentContainer == null
            }

        }
    }

    private fun setRecyclerViewAdapter(itemDettailFragmentContainer: View?): RelatedTopicAdapter {
        val adapter = RelatedTopicAdapter(this, itemDettailFragmentContainer)
        binding.itemList.setHasFixedSize(true)
        binding.itemList.adapter = adapter
        return adapter
    }

    override fun onItemClicked(relatedTopic: RelatedTopic, itemDetailFragmentContainer: View?) {
        val bundle = Bundle()
        bundle.putParcelable("relatedTopic", relatedTopic)
        if (itemDetailFragmentContainer != null) {
            binding.fragmentContainerViewTablet?.visibility = View.VISIBLE
            itemDetailFragmentContainer.findNavController().navigate(
                R.id
                    .nav_graph_tablet, bundle
            )
        } else {
//            val action = ListsFragmentDirections.actionFragmentListToFragmentDetails(relatedTopic)
            findNavController().navigate(R.id.action_fragmentList_to_fragmentDetails, bundle)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}

