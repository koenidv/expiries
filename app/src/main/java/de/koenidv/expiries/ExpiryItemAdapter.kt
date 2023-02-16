package de.koenidv.expiries

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.R.attr
import com.google.android.material.color.MaterialColors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

class ExpiryItemAdapter(private val activity: FragmentActivity) :
    RecyclerView.Adapter<ViewHolder>() {

    class ArticleViewHolder(view: View) : ViewHolder(view) {
        val nameText: TextView
        val expiryText: TextView
        val card: CardView

        init {
            nameText = view.findViewById(R.id.nameTextView)
            expiryText = view.findViewById(R.id.expiryTextView)
            card = view.findViewById(R.id.card)
        }
    }

    class DividerViewHolder(view: View) : ViewHolder(view) {
        val titleText: TextView

        init {
            titleText = view.findViewById(R.id.titleTextView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            ListItem.TYPE_ARTICLE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.expiry_item, parent, false)
                ArticleViewHolder(view)
            }

            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.divider_item, parent, false)
                DividerViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (getItemViewType(position)) {

            ListItem.TYPE_ARTICLE -> {
                val article = differ.currentList[position] as Article
                holder as ArticleViewHolder

                holder.nameText.text = article.name
                holder.expiryText.text = article.expiry
                    ?.format(
                        DateTimeFormatter.ofPattern(
                            "d. MMM" +
                                    if (article.expiry.year != LocalDate.now().year) " yy" else ""
                        )
                    )

                if (article.expiry?.isBefore(LocalDate.now()) == true) {
                    holder.expiryText.setTextColor(
                        MaterialColors.getColor(activity, attr.colorError, Color.RED)
                    )
                } else {
                    holder.expiryText.setTextColor(
                        MaterialColors.getColor(activity, attr.colorOnSurfaceVariant, Color.BLACK)
                    )
                }

                holder.card.setOnClickListener {
                    EditorSheet(article) {
                        CoroutineScope(Dispatchers.IO).launch {
                            Database.get(activity).articleDao().update(it)
                        }
                    }.show(activity.supportFragmentManager, "editor")
                }

            }

            else -> {
                val divider = differ.currentList[position] as ListDivider
                holder as DividerViewHolder

                holder.titleText.text = activity.getString(
                    divider.getDividerString()
                )

            }

        }
    }

    override fun getItemViewType(position: Int): Int {
        return differ.currentList[position].type
    }

    override fun getItemCount() = differ.currentList.size


    private val diffCallback = object : DiffUtil.ItemCallback<ListItem>() {
        override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)
    var unfiltered: List<ListItem> = listOf()

    fun submitList(list: List<ListItem>) {
        unfiltered = list
        differ.submitList(list)
    }

    fun filter(query: String?) {
        if (query.isNullOrBlank()) {
            differ.submitList(unfiltered)
            return
        }
        differ.submitList(
            unfiltered.filter {
                it is Article && (it.name?.contains(query, true) ?: false)
            }
        )
    }

}