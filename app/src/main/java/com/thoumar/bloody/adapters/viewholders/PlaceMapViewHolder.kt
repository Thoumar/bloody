package com.thoumar.bloody.adapters.viewholders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thoumar.bloody.R
import com.thoumar.bloody.entities.Place
import kotlinx.android.synthetic.main.activity_place.*
import kotlinx.android.synthetic.main.place_map_item.view.*
import kotlinx.android.synthetic.main.place_map_item.view.bloodBlock
import kotlinx.android.synthetic.main.place_map_item.view.plasmaBlock
import kotlinx.android.synthetic.main.place_map_item.view.plateletsBlock


class PlaceMapViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.place_map_item, parent, false)) {

    fun bind(place: Place, click: ((place: Place) -> Unit)) {
        itemView.placeName.text = place.name
        itemView.placeCityLong.text = place.cityLong
        if (place.range !== null) {
            itemView.placeRange.text = place.range.toString() + " km"
        }

        // Blood / Plasma / Platelets
        if(place.blood === 1) itemView.bloodBlock.visibility = View.VISIBLE
        if(place.plasma === 1) itemView.plasmaBlock.visibility = View.VISIBLE
        if(place.platelets === 1) itemView.plateletsBlock.visibility = View.VISIBLE

        itemView.placeCard.setOnClickListener {
            click(place)
        }
    }
}