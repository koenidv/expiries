package de.koenidv.expiries

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import de.koenidv.expiries.databinding.FragmentLocationDetailsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch

private const val LOCATION_ID = "locationId"

// fixme DRY this, just a copy of ExpiriesFragment with a different query

class LocationDetailsFragment : Fragment() {
    private var locationId: String? = null

    private var _binding: FragmentLocationDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            locationId = it.getString(LOCATION_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocationDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = Database.get(requireContext())
        binding.recycler.adapter = ExpiryItemAdapter(requireActivity())
        enableSwipeActions(db)
    }

    override fun onResume() {
        startArticlesObserver()
        super.onResume()
    }

    private fun startArticlesObserver() {
        val db = Database.get(requireContext())
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("Location", "Observing articles for location $locationId")
            val articlesObservable =
                if (locationId !== null) db.articleDao().observeByLocation(locationId!!)
                else db.articleDao().observeByLocationNull()
            articlesObservable.takeWhile { isResumed }.collect {
                requireActivity().runOnUiThread {
                    Log.d("Location", "Received ${it.size} articles")
                    (binding.recycler.adapter as ExpiryItemAdapter)
                        .submitList(ArticleListDividers().addListDividers(it))
                }
            }
        }
    }

    private fun enableSwipeActions(db: Database) {
        val swipeCallback: SwipeCallback = object : SwipeCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {
                val position = viewHolder.adapterPosition
                val adapter = binding.recycler.adapter as ExpiryItemAdapter
                val article = adapter.differ.currentList[position] as Article

                CoroutineScope(Dispatchers.Main).launch { db.articleDao().delete(article) }
                showUndoSnackbar(article, db)
            }
        }
        ItemTouchHelper(swipeCallback).attachToRecyclerView(binding.recycler)
    }

    private fun showUndoSnackbar(article: Article, db: Database) {
        Snackbar.make(
            requireActivity().findViewById(R.id.navbar),
            R.string.warning_item_deleted,
            Snackbar.LENGTH_LONG
        ).apply {
            anchorView = requireActivity().findViewById(R.id.navbar)
        }.setAction(R.string.action_undo) {
            CoroutineScope(Dispatchers.Main).launch { db.articleDao().insert(article) }
        }.show()
    }

    fun filterRecycler(query: String?) {
        (binding.recycler.adapter as ExpiryItemAdapter).filter(query)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         */
        @JvmStatic
        fun newInstance(locationId: String?) =
            LocationDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(LOCATION_ID, locationId)
                }
            }
    }
}