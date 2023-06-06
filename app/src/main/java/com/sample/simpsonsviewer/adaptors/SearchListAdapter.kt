package com.sample.simpsonsviewer.adaptors

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sample.simpsonsviewer.data.model.RelatedTopic
import com.sample.simpsonsviewer.databinding.ItemNameBinding

class SearchListAdapter(private val onItemClickListener: (RelatedTopic) -> Unit) :
    ListAdapter<RelatedTopic, SearchListAdapter.ViewHolder>(DiffCallback) {

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<RelatedTopic>() {
            override fun areItemsTheSame(oldItem: RelatedTopic, newItem: RelatedTopic): Boolean {
                return oldItem.Text == newItem.Text
            }

            override fun areContentsTheSame(oldItem: RelatedTopic, newItem: RelatedTopic): Boolean {
                return oldItem == newItem
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNameBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        item?.let { thisItem ->
            holder.bing(thisItem)
        }

    }

    inner class ViewHolder(private val binding: ItemNameBinding) : RecyclerView.ViewHolder(
        binding
            .root
    ) {

        init {
            binding.itemNameText.setOnClickListener {
             val position = bindingAdapterPosition
             if(position!=RecyclerView.NO_POSITION) {
                 val item = getItem(position)
                 item?.let {
                     onItemClickListener(it)
                 }
             }
            }
        }
        fun bing(thisItem: RelatedTopic) {
            val index = thisItem.Text.indexOf("-")
            binding.itemNameText.text = if (index > 0) thisItem.Text.substring(0, index) else "NA"
        }

    }
}