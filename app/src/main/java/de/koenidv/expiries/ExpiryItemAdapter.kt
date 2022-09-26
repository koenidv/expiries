package de.koenidv.expiries

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ExpiryItemAdapter(private val dataset: List<Article>) : RecyclerView.Adapter<ExpiryItemAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameText: TextView
        val expiryText: TextView

        init {
            nameText = view.findViewById(R.id.nameTextView)
            expiryText = view.findViewById(R.id.expiryTextView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.expiry_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.nameText.text = dataset[position].name
        holder.expiryText.text = dataset[position].expiry.toString()
    }

    override fun getItemCount() = dataset.size

}