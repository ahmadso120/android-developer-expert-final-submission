package com.sopian.imageapp.ui.detail

import android.app.Dialog
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
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
import com.sopian.imageapp.core.domain.model.Info
import com.sopian.imageapp.databinding.FragmentMapsBottomSheetBinding
import com.sopian.imageapp.ui.ViewModelFactory
import javax.inject.Inject


class MapsBottomSheetFragment : BottomSheetDialogFragment(), MapboxMap.OnMoveListener {

    private var _binding: FragmentMapsBottomSheetBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var factory: ViewModelFactory
    private val viewModel: MapsBottomSheetViewModel by viewModels {
        factory
    }

    private var mapboxMap: MapboxMap? = null
    private lateinit var symbolManager: SymbolManager

    companion object {
        private const val ICON_ID = "ICON_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(requireContext(), getString(R.string.mapbox_access_token))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        dialog.setOnShowListener {
            val bottomSheet = (it as BottomSheetDialog)
                .findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
            val behavior = BottomSheetBehavior.from(bottomSheet!!)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED

            behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                        behavior.state = BottomSheetBehavior.STATE_EXPANDED
                    }
                    if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                        val materialShapeDrawable = createMaterialShapeDrawable(bottomSheet)
                        ViewCompat.setBackground(bottomSheet, materialShapeDrawable)
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            })
        }

        return dialog
    }

    private fun createMaterialShapeDrawable(bottomSheet: View): MaterialShapeDrawable? {
        val shapeAppearanceModel =
            ShapeAppearanceModel.builder(
                context,
                0,
                R.style.CustomShapeAppearanceBottomSheetDialog
            )
                .build()

        val currentMaterialShapeDrawable = bottomSheet.background as MaterialShapeDrawable
        val newMaterialShapeDrawable = MaterialShapeDrawable(shapeAppearanceModel)

        newMaterialShapeDrawable.initializeElevationOverlay(context)
        newMaterialShapeDrawable.fillColor = currentMaterialShapeDrawable.fillColor
        newMaterialShapeDrawable.tintList = currentMaterialShapeDrawable.tintList
        newMaterialShapeDrawable.elevation = currentMaterialShapeDrawable.elevation
        newMaterialShapeDrawable.strokeWidth = currentMaterialShapeDrawable.strokeWidth
        newMaterialShapeDrawable.strokeColor = currentMaterialShapeDrawable.strokeColor
        return newMaterialShapeDrawable
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as MyApplication).appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = arguments?.getString("id")
        id?.let { viewModel.setIdDetail(it) }

        binding.close.setOnClickListener {
            dismiss()
        }

        viewModel.infoData.observe(viewLifecycleOwner) { infoData ->
            when (infoData) {
                is Resource.Success -> {
                    if (infoData.data != null) {
                        infoData.data?.let {
                            binding.mapView.onCreate(savedInstanceState)
                            binding.mapView.getMapAsync { mapboxMap ->
                                this.mapboxMap = mapboxMap
                                showMarker(it)
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

    private fun showMarker(info: Info) {
        mapboxMap.let { map ->
            map?.setStyle(Style.MAPBOX_STREETS) { style ->
                symbolManager = SymbolManager(binding.mapView, map, style)
                symbolManager.iconAllowOverlap = true

                style.addImage(
                    ICON_ID,
                    BitmapFactory.decodeResource(resources, R.drawable.mapbox_marker_icon_default),
                    false
                )
                showLocation(info)
                mapboxMap?.addOnMoveListener(this)
            }
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

                mapboxMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10.0))
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
        mapboxMap?.removeOnMoveListener(this)
        mapboxMap = null
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
        mapboxMap?.removeOnMoveListener(this)
        mapboxMap = null
        binding.mapsLayout.removeAllViewsInLayout()
        binding.mapView.removeAllViewsInLayout()
        binding.close.setOnClickListener(null)
        binding.mapView.onDestroy()
        _binding = null
    }

    override fun onMoveBegin(detector: MoveGestureDetector) {
        binding.mapsLayout.requestDisallowInterceptTouchEvent(true)
    }

    override fun onMove(detector: MoveGestureDetector) {
        binding.mapsLayout.requestDisallowInterceptTouchEvent(true)
    }

    override fun onMoveEnd(detector: MoveGestureDetector) {
        binding.mapsLayout.requestDisallowInterceptTouchEvent(true)
    }
}