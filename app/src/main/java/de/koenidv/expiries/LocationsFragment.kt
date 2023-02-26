package de.koenidv.expiries

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import de.koenidv.expiries.databinding.FragmentLocationsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_LOCATIONID = "locationId"

/**
 * A simple [Fragment] subclass.
 * Use the [LocationsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LocationsFragment : Fragment() {

    private var _binding: FragmentLocationsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = LocationsAdapter { id, longclick -> handleLocationSelected(id, longclick) }
        binding.locationsRecycler.adapter = adapter
    }

    override fun onResume() {
        startLocationsObserver(binding.locationsRecycler.adapter as LocationsAdapter)
        super.onResume()
    }

    private fun handleLocationSelected(id: Int, longclick: Boolean) {
        if (id >= 0) {
            if (longclick) {
                CoroutineScope(Dispatchers.IO).launch {
                    LocationEditSheet(
                        Database.get(requireContext()).locationDao().get(id)
                    ).show(parentFragmentManager, "loc-edit")
                }
            } else {
                findNavController().navigate(LocationsFragmentDirections.actionLocationsToDetails(id.toString()))
            }
        } else if (id == -1) {
            findNavController().navigate(LocationsFragmentDirections.actionLocationsToDetails(null))
        } else if (id == -2) { // Create Location
            LocationEditSheet(null).show(parentFragmentManager, "loc-edit")
        }
    }

    private fun startLocationsObserver(adapter: LocationsAdapter) {
        CoroutineScope(Dispatchers.IO).launch {
            val locationsObservable = Database.get(requireContext()).locationDao().observeAll()
            locationsObservable.takeWhile { isResumed }.collect {
                val copy = it.toMutableList().apply {
                    add(
                        Location(
                            id = -1,
                            name = getString(R.string.location_undefined),
                            icon_name = "ic_location",
                            comment = null
                        )
                    )
                    add(
                        Location(
                            id = -2,
                            name = getString(R.string.location_add),
                            icon_name = "ic_add",
                            comment = null
                        )
                    )
                }
                requireActivity().runOnUiThread {
                    adapter.differ.submitList(copy)
                }
            }
        }
    }
}