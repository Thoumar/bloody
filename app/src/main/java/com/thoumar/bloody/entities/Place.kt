package com.thoumar.bloody.entities

import android.content.ContentUris
import android.content.Intent
import android.net.Uri
import android.os.Parcelable
import android.provider.CalendarContract
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName
import com.google.maps.android.clustering.ClusterItem
import kotlinx.android.parcel.Parcelize


@Parcelize
class Markup(@SerializedName(value = "#markup") val markup: String?): Parcelable {

}

@Parcelize
data class Place(

    @SerializedName(value = "lp_ad3")
    val cityLong: String?,

    @SerializedName(value = "lp_ad2")
    val address: String?,

    @SerializedName(value = "lp_ad1")
    val name: String?,

    @SerializedName(value = "lp_com")
    val communication: Markup?,

    @SerializedName(value = "lat")
    val latitude: Double,

    @SerializedName(value = "lon")
    val longitude: Double,

    @SerializedName(value = "sang")
    val blood: Int?,

    @SerializedName(value = "plasma")
    val plasma: Int?,

    @SerializedName(value = "plaquettes")
    val platelets: Int?,

    @SerializedName(value = "num_tel")
    val phoneNumber: String?,

    @SerializedName(value = "adr_mail")
    val mailAddress: String?,

    @SerializedName(value = "metro")
    val subway: String?,

    @SerializedName(value = "tram")
    val tramway: String?,

    @SerializedName(value = "bus")
    val buses: String?,

    @SerializedName(value = "parking")
    val parking: String?,

    @SerializedName(value = "region")
    val region: String?,

    @SerializedName(value = "gr_code")
    val grCode: String?,

    @SerializedName(value = "lp_code")
    val lpCode: String?,

    @SerializedName(value = "region_name")
    val regionName: String?,

    @SerializedName(value = "icon")
    val icon: String?,

    @SerializedName(value = "text")
    val text: Markup?,

    @SerializedName(value = "ville")
    val cityShort: String?,

    @SerializedName(value = "sahre")
    val share: Markup?,

    @SerializedName(value = "type_don_value")
    val givingTypes: List<String>?,

//
//    @SerializedName(value = "quand")
//    val time: String?,

    val marker: String?,

    var phoneIntent: Intent?,
    var navigationIntent: Intent?,
    var calendarIntent: Intent?,
    var shareIntent: Intent?,
    var range: Double?

): ClusterItem, Parcelable {
    init {

        // Navigation Intent
        val navigationUri = Uri.parse("google.navigation:q=${this.latitude},${this.longitude}")
        navigationIntent = Intent(Intent.ACTION_VIEW, navigationUri)

        // Phone Intent
        if (this.phoneNumber !== null) {
            val phoneUri = Uri.parse("tel:" + this.phoneNumber)
            phoneIntent = Intent(Intent.ACTION_CALL, phoneUri)
        }

        // Calendar Intent
        val startMillis = System.currentTimeMillis()
        val builder = CalendarContract.CONTENT_URI.buildUpon()
        builder.appendPath("time")
        ContentUris.appendId(builder, startMillis)
        calendarIntent = Intent(Intent.ACTION_VIEW)
            .setData(builder.build())
            .putExtra(CalendarContract.Events.TITLE, "Don du sang")
            .putExtra(CalendarContract.Events.DESCRIPTION, this.communication.toString())

        val shareBody = "Hey ! Je donne mon sang à " + this.cityShort + ", rejoins moi !"
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_SUBJECT, "Je donne mon sang !")
        intent.putExtra(Intent.EXTRA_TEXT, shareBody)
        shareIntent = intent
    }

    override fun getSnippet(): String? {
        return this.name
    }

    override fun getTitle(): String? {
        return this.name
    }

    override fun getPosition(): LatLng {
        return LatLng(latitude, longitude)
    }
}