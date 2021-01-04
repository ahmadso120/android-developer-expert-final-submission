package com.sopian.imageapp.ui.detail

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
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
import com.google.android.material.snackbar.Snackbar
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import com.sopian.imageapp.MyApplication
import com.sopian.imageapp.R
import com.sopian.imageapp.core.data.Resource
import com.sopian.imageapp.core.domain.model.Download
import com.sopian.imageapp.core.domain.model.Info
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

    private lateinit var mapboxMap: MapboxMap
    private lateinit var symbolManager: SymbolManager

    private var listener: MapboxMap.OnMoveListener? = null

    companion object {
        private const val ICON_ID = "ICON_ID"
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
            observeInfoData(savedInstanceState)
        }
    }

    private fun observeInfoData(savedInstanceState: Bundle?){
        viewModel.infoData.observe(viewLifecycleOwner){ infoData ->
            if (infoData != null) {
                val latitude = infoData.data?.location?.position?.latitude
                val longitude = infoData.data?.location?.position?.longitude

                when(infoData){
                    is Resource.Success -> {
                        binding.info = infoData.data
                        if (latitude != null && longitude != null) {
                            binding.mapView.onCreate(savedInstanceState)
                            binding.mapView.getMapAsync { mapboxMap ->
                                this.mapboxMap = mapboxMap
                                showMarker(infoData.data!!)
                                onMapMoveListener()
                            }
                        } else {
                            binding.mapView.visibility = View.GONE
                        }

                    }
                    is Resource.Error -> {
                        Toast.makeText(activity, infoData.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun observePhotoData(view: View){
        viewModel.photoData
            .observe(viewLifecycleOwner) { photoData ->
                Timber.d(photoData.toString())
                binding.photo = photoData

                if(photoData != null){
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


    private fun onMapMoveListener(){
        listener = object : MapboxMap.OnMoveListener {
            override fun onMoveBegin(detector: MoveGestureDetector) {
                binding.nestedScroll.requestDisallowInterceptTouchEvent(true)
            }

            override fun onMove(detector: MoveGestureDetector) {
                binding.nestedScroll.requestDisallowInterceptTouchEvent(true)
            }

            override fun onMoveEnd(detector: MoveGestureDetector) {
                binding.nestedScroll.requestDisallowInterceptTouchEvent(true)
            }
        }
        mapboxMap.addOnMoveListener(listener as MapboxMap.OnMoveListener)
    }

    private fun downloadImage(){
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

    private fun showMarker(info: Info) {
        mapboxMap.setStyle(Style.MAPBOX_STREETS) { style ->
            symbolManager = SymbolManager(binding.mapView, mapboxMap, style)
            symbolManager.iconAllowOverlap = true

            style.addImage(
                ICON_ID,
                BitmapFactory.decodeResource(resources, R.drawable.mapbox_marker_icon_default),
                false
            )

            showLocation(info)
        }
    }

    private fun showLocation(info: Info) {
        val latitude = info.location?.position?.latitude
        val longitude = info.location?.position?.longitude
        val title = info.location?.title

        latitude?.let { lat ->
            longitude?.let { lng ->
                val location = LatLng(lat, lng)
                symbolManager.create(
                    SymbolOptions()
                        .withLatLng(LatLng(location.latitude, location.longitude))
                        .withIconImage(ICON_ID)
                        .withIconOffset(arrayOf(0f, -1.5f))
                        .withTextField(title)
                        .withIconSize(1.5f)
                        .withTextHaloColor("rgba(255, 255, 255, 100)")
                        .withTextHaloWidth(5.0f)
                        .withTextAnchor("top")
                        .withTextOffset(arrayOf(0f, 1.5f))
                )

                mapboxMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10.0))
            }
        }
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

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.download.setOnClickListener(null)
        if (listener != null){
            mapboxMap.removeOnMoveListener(listener!!)
        }
        listener = null
        binding.nestedScroll.removeAllViewsInLayout()
        binding.mapView.removeAllViewsInLayout()
        binding.mapView.onDestroy()
        _binding = null
    }

}