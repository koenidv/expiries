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

class ExpiriesFragment : Fragment() {

    private var _binding: FragmentExpiriesBinding? = null
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
        binding.recycler.adapter = ExpiryItemAdapter(requireActivity())
        collectArticles(db)
        enableSwipeActions(db)
    }

    private fun collectArticles(db: Database) {
        CoroutineScope(Dispatchers.Main).launch {
            val articlesObservable = db.articleDao().getAllSorted()
            articlesObservable.collect {
                (binding.recycler.adapter as ExpiryItemAdapter)
                    .differ.submitList(ArticleListDividers().addListDividers(it))
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
            binding.recycler,
            R.string.warning_item_deleted,
            Snackbar.LENGTH_LONG
        ).setAction(R.string.action_undo) {
            CoroutineScope(Dispatchers.Main).launch { db.articleDao().insert(article) }
        }.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}