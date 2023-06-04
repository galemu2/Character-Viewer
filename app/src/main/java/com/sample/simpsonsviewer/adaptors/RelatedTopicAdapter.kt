package com.sample.simpsonsviewer.adaptors

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sample.simpsonsviewer.data.model.RelatedTopic
import com.sample.simpsonsviewer.databinding.ItemNameBinding
import com.sample.simpsonsviewer.util.Const.TAG

class RelatedTopicAdapter(
    private val listener: OnItemClickListener,
    diffCallback: DiffUtil.ItemCallback<RelatedTopic> = object : DiffUtil
    .ItemCallback<RelatedTopic>() {
        override fun areItemsTheSame(oldItem: RelatedTopic, newItem: RelatedTopic): Boolean {
            return oldItem.Text == newItem.Text
        }

        override fun areContentsTheSame(oldItem: RelatedTopic, newItem: RelatedTopic): Boolean {
            return oldItem == newItem
        }
    }
) : PagingDataAdapter<RelatedTopic, RelatedTopicAdapter.ViewHolder>(diffCallback = diffCallback) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNameBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        item?.let { thisItem ->
            Log.d(TAG, "onBindViewHolder: ${thisItem.Text}")
            holder.bind(thisItem)
        }
        Log.d(TAG, "onBindViewHolder: $item")
    }


    interface OnItemClickListener {
        fun onItemClicked(text: RelatedTopic)
        // TODO - pass data to detail view
    }

    inner class ViewHolder(private val binding: ItemNameBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition

                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    item?.let {
                        listener.onItemClicked(it)
                    }
                }
            }
        }

        fun bind(thisItem: RelatedTopic) {
            val index = thisItem.Text.indexOf("-")
            binding.itemNameText.text = if (index > 0) thisItem.Text.substring(0, index) else "NA"
        }

    }

}