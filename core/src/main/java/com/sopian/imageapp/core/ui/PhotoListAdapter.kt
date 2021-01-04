package com.sopian.imageapp.core.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.sopian.imageapp.core.R
import com.sopian.imageapp.core.databinding.PhotoItemBinding
import com.sopian.imageapp.core.domain.model.Photo
import com.sopian.imageapp.core.utils.AppExecutors
import com.sopian.imageapp.core.utils.DataBoundListAdapter

class PhotoListAdapter(
    appExecutors: AppExecutors,
    private val photoClickCallback: ((Photo) -> Unit)?
) : DataBoundListAdapter<Photo, PhotoItemBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<Photo>() {
        override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean {
            return oldItem.id == newItem.id
                    && oldItem.urls.regular == newItem.urls.regular
        }

        override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean {
            return oldItem.urls.regular == newItem.urls.regular
                    && oldItem.description == newItem.description
        }
    }
) {

    override fun createBinding(parent: ViewGroup): PhotoItemBinding {
        val binding = DataBindingUtil.inflate<PhotoItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.photo_item,
            parent,
            false
        )
        binding.root.setOnClickListener {
            binding.photo?.let {
                photoClickCallback?.invoke(it)
            }
        }
        return binding
    }

    override fun bind(binding: PhotoItemBinding, item: Photo) {
        binding.photo = item
    }
}