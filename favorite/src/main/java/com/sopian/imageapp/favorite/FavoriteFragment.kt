package com.sopian.imageapp.favorite

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sopian.imageapp.MyApplication
import com.sopian.imageapp.core.utils.AppExecutors
import com.sopian.imageapp.favorite.databinding.FragmentFavoriteBinding
import com.sopian.imageapp.favorite.di.DaggerDynamicFeatureComponent
import com.sopian.imageapp.core.ui.PhotoListAdapter
import com.sopian.imageapp.core.utils.EventObserver
import timber.log.Timber
import javax.inject.Inject

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var factory: ViewModelFactory

    private val viewModel: FavoriteViewModel by viewModels {
        factory
    }

    @Inject
    lateinit var appExecutors: AppExecutors

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val coreComponent = (requireActivity().application as MyApplication).coreComponent
        DaggerDynamicFeatureComponent.factory().create(coreComponent).inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val photoListAdapter = PhotoListAdapter(appExecutors, viewModel::onPhotoClicked)

        with(binding.recyclerView) {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = photoListAdapter
        }

        viewModel.navigateToDetail.observe(viewLifecycleOwner, EventObserver {
            val bundle = bundleOf("id" to it.id)
            findNavController().navigate(
                R.id.action_favoriteFragment_to_detailFragment, bundle
            )
        })

        viewModel.favorites.observe(viewLifecycleOwner, { photo ->
            Timber.d(photo.toString())
            photoListAdapter.submitList(photo)
            binding.viewEmpty.txtNoData.visibility =
                if (photo.isNotEmpty()) View.GONE else View.VISIBLE
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.layout.removeAllViews()
        _binding = null
    }
}