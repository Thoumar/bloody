package com.thoumar.bloody.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Base64.NO_WRAP
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.crowdfire.cfalertdialog.CFAlertDialog
import com.thoumar.bloody.R
import com.thoumar.bloody.entities.Place
import kotlinx.android.synthetic.main.activity_place.*
import org.jsoup.Jsoup
import java.io.InputStream
import java.util.*


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
            insertText(placeName, place.name, null)
            insertText(placeCityLong, place.cityLong, null)
            insertText(placeAddress, place.address, null)

            // Blood / Plasma / Platelets
            if(place.blood === 1) bloodBlock.visibility = View.VISIBLE
            if(place.plasma === 1) plasmaBlock.visibility = View.VISIBLE
            if(place.platelets === 1) plateletsBlock.visibility = View.VISIBLE


            // Communication
            // Enable Javascript
            placeCommunication.loadDataWithBaseURL("", place.communication?.markup, "text/html", "UTF-8", "")

            // Share
            val shareHtmlHtmlDoc = Jsoup.parse(place.share?.markup)
            val linksHtmlElements = shareHtmlHtmlDoc.select("li > a")
            linksHtmlElements.forEach { el ->

                when (el.text().toLowerCase(Locale.ROOT)) {
                    "mail" -> {
                        placeMailContainer.visibility = View.VISIBLE
                        placeMailContainer.setOnClickListener {
                            val i = Intent(Intent.ACTION_VIEW)
                            i.data = Uri.parse("https://dondesang.efs.sante.fr" + el.attr("href"))
                            startActivity(i)
                        }
                    }
                    "facebook" -> {
                        placeFacebookContainer.visibility = View.VISIBLE
                        placeFacebookContainer.setOnClickListener {
                            val i = Intent(Intent.ACTION_VIEW)
                            i.data = Uri.parse(el.attr("href"))
                            startActivity(i)
                        }
                    }
                    "twitter" -> {
                        placeTwitterContainer.visibility = View.VISIBLE
                        placeTwitterContainer.setOnClickListener {
                            val i = Intent(Intent.ACTION_VIEW)
                            i.data = Uri.parse(el.attr("href"))
                            startActivity(i)
                        }
                    }
                }
            }

            // Transports
            insertText(placeSubway, place.subway, placeSubwayContainer)
            insertText(placeBuses, place.buses, placeBusesContainer)
            insertText(placeTramway, place.tramway, placeTramwayContainer)
            insertText(placeParking, place.parking, placeParkingContainer)
            if(
                place.subway.isNullOrBlank() &&
                place.buses.isNullOrBlank() &&
                place.tramway.isNullOrBlank() &&
                place.parking.isNullOrBlank()
                    ) {
                transportTitle.visibility = View.GONE
                transportContainer.visibility = View.GONE
            }


            placeSubwayContainer.setOnClickListener {
                setDialogMessage(R.drawable.ic_subway, "Métros", place.subway.toString())
            }

            placeBusesContainer.setOnClickListener {
                setDialogMessage(R.drawable.ic_subway, "Bus", place.buses.toString())
            }

            placeTramwayContainer.setOnClickListener {
                setDialogMessage(R.drawable.ic_tramway, "Tramways", place.tramway.toString())
            }

            placeParkingContainer.setOnClickListener {
                setDialogMessage(R.drawable.ic_parking, "Parking", place.parking.toString())
            }

            // Phone intent
            if (place.phoneIntent == null) {
                phoneBlock.visibility = View.GONE
            } else {
                startCallBtn.setOnClickListener {
                    startActivity(place.phoneIntent)
                }
            }

            navigateToBtn.setOnClickListener { startActivity(place.navigationIntent) }
            addToCalendarBtn.setOnClickListener { startActivity(place.calendarIntent) }
            shareBtn.setOnClickListener { startActivity(place.shareIntent) }

            givingTypesBtn.setOnClickListener {
                setDialogMessage(R.drawable.ic_info, "Types de dons possibles :", "Fiole rouge -> Don de sang\nFiole orange -> Don de plasma\nFiole jaune -> Don de plaquettes\n")
            }
        }
    }

    private fun setDialogMessage(icon: Int, title: String, message: String) {
        val builder: CFAlertDialog.Builder = CFAlertDialog.Builder(this)
            .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
            .setIcon(icon)
            .setTitle(title)
            .setMessage(message)
            .addButton("Fermer", -1, -1, CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.CENTER) { dialog, _ ->
                dialog.dismiss()
            }
        builder.show()
    }

    private fun insertText(tv: TextView, str: String?, parentView: View?) {
        if(!str.isNullOrBlank()) {
            tv.text = str
        } else {
            if(parentView == null) {
                tv.visibility = View.GONE
            } else {
                parentView.visibility = View.GONE
            }
        }
    }
}
