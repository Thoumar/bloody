package com.thoumar.bloody.activities

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.thoumar.bloody.R
import com.thoumar.bloody.entities.Place
import kotlinx.android.synthetic.main.activity_place.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document


class PlaceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place)

        val place = intent.getParcelableExtra<Place>("PLACE")

        backBtn.setOnClickListener {
            finish()
        }

        if (place != null) {

            // Main informations
            insertText(placeName, place.name)
            insertText(placeCityLong, place.cityLong)
            insertText(placeAddress, place.address)

            // Blood / Plasma / Platelets
            if(place.blood === 1) bloodBlock.visibility = View.VISIBLE
            if(place.plasma === 1) plasmaBlock.visibility = View.VISIBLE
            if(place.platelets === 1) plateletsBlock.visibility = View.VISIBLE


            // Communication
            val communicationHtmlString = place.communication?.markup
            placeCommunication.setBackgroundColor(Color.TRANSPARENT)
            placeCommunication.loadDataWithBaseURL("", communicationHtmlString, "text/html", "UTF-8", "")

            // Share
            val shareHtmlString = place.share?.markup
            placeShare.setBackgroundColor(Color.TRANSPARENT)
            placeShare.loadDataWithBaseURL("", shareHtmlString, "text/html", "UTF-8", "")




            placePhoneNumber.text = place.phoneNumber
            placeMailAddress.text = place.mailAddress
            placeSubway.text = "Metros:" + place.subway
            placeTramway.text = "Tramways:" + place.tramway
            placeBuses.text = "Buses:" + place.buses
            placeParking.text = "Parking:" + place.parking

            // Place text
            placeView.text = place.view

            startCallBtn.setOnClickListener { startActivity(place.phoneIntent) }
            navigateToBtn.setOnClickListener { startActivity(place.navigationIntent) }
            addToCalendarBtn.setOnClickListener { startActivity(place.calendarIntent) }
        }
    }

    private fun insertText(tv: TextView, str: String?) {
        if(!str.isNullOrBlank()) {
            tv.text = str
        } else {
            tv.visibility = View.GONE
        }
    }
}
