package de.koenidv.expiries

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import de.koenidv.expiries.databinding.FragmentExpiriesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class ExpiriesFragment : Fragment() {

    private var _binding: FragmentExpiriesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExpiriesBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = Database.get(requireContext())

        val adapter = ExpiryItemAdapter(requireActivity())
        binding.recycler.adapter = adapter

        enableSwipeActions(binding.recycler, db)

        CoroutineScope(Dispatchers.Main).launch {

            val articlesObservable = db.articleDao().getAllSorted()
            articlesObservable.collect {
                adapter.differ.submitList(ArticleListDividers().addListDividers(it))
                //adapter.notifyDataSetChanged()
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun enableSwipeActions(recycler: RecyclerView, db: Database) {
        val swipeCallback: SwipeCallback = object : SwipeCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {
                val position = viewHolder.adapterPosition
                val adapter = recycler.adapter as ExpiryItemAdapter
                val article = adapter.differ.currentList[position] as Article

                CoroutineScope(Dispatchers.Main).launch { db.articleDao().delete(article) }

                Snackbar.make(
                    recycler,
                    R.string.warning_item_deleted,
                    Snackbar.LENGTH_LONG
                ).setAction(R.string.action_undo) {
                    CoroutineScope(Dispatchers.Main).launch { db.articleDao().insert(article) }
                }.show()
            }
        }
        ItemTouchHelper(swipeCallback).attachToRecyclerView(recycler)
    }

}