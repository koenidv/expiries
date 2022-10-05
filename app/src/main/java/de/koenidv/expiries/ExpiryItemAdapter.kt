package de.koenidv.expiries

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle

class ExpiryItemAdapter(private val activity: FragmentActivity) :
    RecyclerView.Adapter<ViewHolder>() {

    var dataset: List<ListItem> = listOf()

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
                val article = dataset[position].article_data!!
                holder as ArticleViewHolder

                holder.nameText.text = article.name
                holder.expiryText.text = article.expiry
                    ?.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT))
                holder.card.setOnClickListener {
                    EditorSheet(article) {
                        CoroutineScope(Dispatchers.IO).launch {
                            Database.get(activity).articleDao().update(it)
                        }
                    }.show(activity.supportFragmentManager, "editor")
                }
            }
            else -> {
                val date = dataset[position].divider_data!!
                holder as DividerViewHolder

                holder.titleText.text = activity.getString(
                    ArticleListDividers().resolveDividerDate(date, LocalDate.now())
                )
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return dataset[position].type
    }

    override fun getItemCount() = dataset.size

}