package com.thoumar.bloody.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thoumar.bloody.adapters.viewholders.PlaceMapViewHolder
import com.thoumar.bloody.entities.Place

class PlaceAdapter(private val list: List<Place>, private val click: ((place: Place) -> Unit)):  RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return PlaceMapViewHolder(inflater, parent)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val place: Place = list[position]
        holder as PlaceMapViewHolder
        holder.bind(place, click)
    }
}