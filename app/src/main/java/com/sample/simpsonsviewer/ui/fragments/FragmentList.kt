package com.sample.simpsonsviewer.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.sample.simpsonsviewer.R
import com.sample.simpsonsviewer.adaptors.RelatedTopicAdapter
import com.sample.simpsonsviewer.databinding.FragmentListBinding
import com.sample.simpsonsviewer.ui.viewModels.SimpsonsViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class FragmentList : Fragment(R.layout.fragment_list), RelatedTopicAdapter.OnItemClickListener {

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
    }

    override fun onItemClicked(text: String) {
        Toast.makeText(requireContext(), "$text", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}