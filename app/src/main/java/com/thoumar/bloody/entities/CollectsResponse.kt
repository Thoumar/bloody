package com.thoumar.bloody.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class CollectsResponse(
    val results: List<Place>,
    val num_results: Int
): Parcelable
