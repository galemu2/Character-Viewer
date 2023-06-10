package com.sample.simpsonsviewer.ui.fragments

import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.sample.simpsonsviewer.R
import com.sample.simpsonsviewer.api.ApiUtil.BASE_URL
import com.sample.simpsonsviewer.data.model.RelatedTopic
import com.sample.simpsonsviewer.databinding.FragmentDetailsBinding

class FragmentDetails : Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding: FragmentDetailsBinding
        get() = _binding!!


    //    private val args: FragmentDetailsArgs by navArgs()
    private var relatedTopic: RelatedTopic? = null

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (it.containsKey("relatedTopic")) {
                relatedTopic = it.getParcelable("relatedTopic", RelatedTopic::class.java)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (relatedTopic == null) return


        val index = relatedTopic!!.Text.indexOf("-")
        binding.itemTitle.text = relatedTopic!!.Text.substring(0, index)
        binding.itemDescription.text = relatedTopic!!.Text.substring(index + 1)

        Glide.with(this)
            .load(getUrl(relatedTopic!!.Icon.URL))
            .centerInside()
            .placeholder(R.drawable.ic_simpson_place_holder)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.iconLoadedState.visibility = View.GONE
                    binding.imageIcon.visibility = View.VISIBLE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.iconLoadedState.visibility = View.GONE
                    binding.imageIcon.visibility = View.VISIBLE
                    return false
                }

            })
            .into(binding.imageIcon)
    }

    private fun getUrl(iconUrl: String): String {
        return BASE_URL + iconUrl
    }


}
