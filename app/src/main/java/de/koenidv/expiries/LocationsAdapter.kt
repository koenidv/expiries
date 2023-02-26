package de.koenidv.expiries

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.color.MaterialColors

class LocationsAdapter(val callback: (Int, Boolean, String) -> Unit) :
    RecyclerView.Adapter<LocationsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameText: TextView
        val card: CardView

        init {
            nameText = view.findViewById(R.id.locationName)
            card = view.findViewById(R.id.card)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.location_item, parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val location = differ.currentList[position] as Location

        holder.nameText.text = location.name

        holder.card.setOnClickListener { callback(location.id, false, location.name) }
        holder.card.setOnLongClickListener { callback(location.id, true, location.name); true }

        val background = if (location.id < 0)
            MaterialColors.getColor(
                holder.card,
                com.google.android.material.R.attr.colorSurface
            )
        else
            MaterialColors.getColor(
                holder.card,
                com.google.android.material.R.attr.colorSurfaceVariant
            )

        holder.card.setCardBackgroundColor(background)
    }

    override fun getItemCount() = differ.currentList.size

    private val diffCallback = object : DiffUtil.ItemCallback<Location>() {
        override fun areItemsTheSame(oldItem: Location, newItem: Location): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Location, newItem: Location): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)
}
