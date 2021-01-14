package com.sopian.imageapp.ui.home

import android.content.Context
import android.os.Bundle
import android.os.IBinder
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.sopian.imageapp.MyApplication
import com.sopian.imageapp.R
import com.sopian.imageapp.core.data.Resource
import com.sopian.imageapp.core.ui.PhotoListAdapter
import com.sopian.imageapp.core.utils.AppExecutors
import com.sopian.imageapp.core.utils.EventObserver
import com.sopian.imageapp.databinding.FragmentHomeBinding
import com.sopian.imageapp.ui.ViewModelFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var appExecutors: AppExecutors

    @Inject
    lateinit var factory: ViewModelFactory

    private val viewModel: HomeViewModel by viewModels {
        factory
    }

    private var searchJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as MyApplication).appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val photoListAdapter = PhotoListAdapter(appExecutors, viewModel::onPhotoClicked)

        with(binding.recyclerView) {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = photoListAdapter
        }

        viewModel.navigateToDetail.observe(viewLifecycleOwner, EventObserver{
            val bundle = bundleOf("id" to it.id)
            findNavController().navigate(
                R.id.action_homeFragment_to_detailFragment, bundle
            )
        })


        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            viewModel.photosMediatorData.observe(viewLifecycleOwner) { photo ->
                Timber.d(photo.data.toString())
                if (photo != null) {
                    when (photo) {
                        is Resource.Loading -> {
                            binding.recyclerView.visibility = View.GONE
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        is Resource.Success -> {
                            binding.recyclerView.visibility = View.VISIBLE
                            binding.progressBar.visibility = View.GONE
                            photoListAdapter.submitList(photo.data)
                        }
                        is Resource.Error -> {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(activity, "Error", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.menu_home, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                if (query != null) {
                    binding.recyclerView.scrollToPosition(0)
                    doSearch(searchView, query)
                    searchView.clearFocus()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_favorite -> {
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToFavoriteNavigation()
                )
                true
            }
            else -> true
        }
    }

    private fun doSearch(v: SearchView, query: String) {
        dismissKeyboard(v.windowToken)
        viewModel.onSearchQuery(query)
    }

    private fun dismissKeyboard(windowToken: IBinder) {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.progressBar.stopShimmer()
        binding.progressBar.removeAllViewsInLayout()
        binding.recyclerView.removeAllViews()
        _binding = null
    }
}