package com.thoumar.bloody.adapters.viewholders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thoumar.bloody.R
import com.thoumar.bloody.entities.Place
import kotlinx.android.synthetic.main.place_map_item.view.*


class PlaceMapViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.place_map_item, parent, false)) {

    fun bind(place: Place, click: ((place: Place) -> Unit)) {
        itemView.placeName.text = place.name
        itemView.placeCityLong.text = place.cityLong
        itemView.placeRange.text = place.range.toString() + "km"

        itemView.placeCard.setOnClickListener {
            click(place)
        }
    }
}