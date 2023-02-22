package de.koenidv.expiries

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class LocationsAdapter(callback: (Int) -> Unit) :
    RecyclerView.Adapter<LocationsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameText: TextView

        init {
            nameText = view.findViewById(R.id.locationName)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.location_item, parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val location = differ.currentList[position] as Location
        holder.nameText.text = location.name
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
