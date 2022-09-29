package de.koenidv.expiries

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle

class ExpiryItemAdapter(val activity: FragmentActivity) :
    RecyclerView.Adapter<ExpiryItemAdapter.ViewHolder>() {

    var dataset: List<Article> = listOf()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameText: TextView
        val expiryText: TextView
        val card: CardView

        init {
            nameText = view.findViewById(R.id.nameTextView)
            expiryText = view.findViewById(R.id.expiryTextView)
            card = view.findViewById(R.id.card)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.expiry_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.nameText.text = dataset[position].name
        holder.expiryText.text = dataset[position].expiry
            ?.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT))
        holder.card.setOnClickListener {
            EditorSheet(dataset[position]) {
                CoroutineScope(Dispatchers.IO).launch {
                    Database.get(activity).articleDao().update(it)
                }
            }.show(activity.supportFragmentManager, "editor")
        }
    }

    override fun getItemCount() = dataset.size

}