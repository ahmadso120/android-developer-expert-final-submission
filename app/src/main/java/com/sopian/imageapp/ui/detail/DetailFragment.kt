package com.sopian.imageapp.ui.detail

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.mapbox.mapboxsdk.Mapbox
import com.sopian.imageapp.MyApplication
import com.sopian.imageapp.R
import com.sopian.imageapp.core.data.Resource
import com.sopian.imageapp.core.domain.model.Download
import com.sopian.imageapp.core.utils.EventObserver
import com.sopian.imageapp.databinding.FragmentDetailBinding
import com.sopian.imageapp.ui.ViewModelFactory
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var factory: ViewModelFactory
    private val viewModel: DetailViewModel by viewModels {
        factory
    }
    private var url: String? = null
    private var id: String? = null

    companion object {
        private const val STORAGE_PERMISSION_CODE: Int = 1000
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(requireContext(), getString(R.string.mapbox_access_token))
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as MyApplication).appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        id = arguments?.getString("id")
        id?.let { viewModel.setIdDetail(it) }

        binding.download.setOnClickListener {
            id?.let { id -> viewModel.onDownloadClicked(id) }
        }

        observeData(view, savedInstanceState)
    }

    private fun observeData(view: View, savedInstanceState: Bundle?) {
        lifecycleScope.launch {
            observePhotoData(view)
            downloadImage()
            observeInfoData()
            navigateToMaps()
        }
    }

    private fun observeInfoData() {
        viewModel.infoData.observe(viewLifecycleOwner) { infoData ->
            when (infoData) {
                is Resource.Success -> {
                    if (infoData.data != null) {
                        val info = infoData.data
                        binding.info = info

                        val latitude = info?.location?.position?.latitude
                        val longitude = info?.location?.position?.longitude

                        if (latitude != null && longitude != null) {
                            binding.maps.visibility = View.VISIBLE
                            binding.maps.setOnClickListener {
                                viewModel.onMapsClicked(info.id)
                            }
                        }
                    }
                }
                is Resource.Error -> {
                    Toast.makeText(activity, infoData.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun observePhotoData(view: View) {
        viewModel.photoData
            .observe(viewLifecycleOwner) { photoData ->
                Timber.d(photoData.toString())
                binding.photo = photoData

                if (photoData != null) {
                    var statusFavorite = photoData.isFavorite
                    statusFavorite = !statusFavorite

                    binding.fab.setOnClickListener {
                        viewModel.setFavoritePhoto(photoData, statusFavorite)
                        if (statusFavorite) {
                            Snackbar.make(view, "Favorited", Snackbar.LENGTH_LONG).apply {
                                setAction("SEE") {
                                    it.findNavController().navigate(
                                        DetailFragmentDirections.actionDetailFragmentToFavoriteNavigation()
                                    )
                                }
                            }.show()
                        } else {
                            Snackbar.make(view, "UnFavorited", Snackbar.LENGTH_LONG).show()
                        }
                    }
                }
            }
    }

    private fun navigateToMaps() {
        viewModel.navigateToMaps.observe(viewLifecycleOwner, EventObserver {
            val action =
                DetailFragmentDirections.actionDetailFragmentToMapsBottomSheetFragment(it)
            findNavController().navigate(action)
        })
    }

    private fun downloadImage() {
        viewModel.download.observe(viewLifecycleOwner, { data ->
            Timber.tag("download data").d(data.data.toString())
            if (data != null) {
                when (data) {
                    is Resource.Success -> {
                        checkPermissionStorage(data.data)
                    }
                    is Resource.Error -> {
                        Toast.makeText(activity, "Error", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }

    private fun startDownloading(url: String, id: String) {
        Timber.tag("download url").d(url)
        val fileName = id
        val request = DownloadManager.Request(Uri.parse(url))

        request.setAllowedNetworkTypes(
            DownloadManager.Request.NETWORK_WIFI or
                    DownloadManager.Request.NETWORK_MOBILE
        )
        request.setTitle(fileName)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS,
            "${System.currentTimeMillis()}"
        )

        val manager = activity?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        manager.enqueue(request)
    }

    private fun checkPermissionStorage(data: Download?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) ==
                PackageManager.PERMISSION_DENIED
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    STORAGE_PERMISSION_CODE
                )
            } else {
                data?.url?.let { url ->
                    id?.let { id ->
                        startDownloading(url, id)
                    }
                }
            }
        } else {
            data?.url?.let { url ->
                id?.let { id ->
                    startDownloading(url, id)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            STORAGE_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0]
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    url?.let { id?.let { id -> startDownloading(it, id) } }
                } else {
                    Toast.makeText(requireContext(), "Permission denied!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.nestedScroll.removeAllViewsInLayout()
        binding.download.setOnClickListener(null)
        _binding = null
    }


}