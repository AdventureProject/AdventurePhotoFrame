package com.darkrockstudios.iot.adventurephotoframe.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by adamw on 12/21/2016.
 */
@Parcelize
data class Photo(val title: String? = null,
				 val description: String? = null,
				 val date: String? = null,
				 val image: String? = null,
				 val url: String? = null,
				 val thumbnail: String? = null,
				 val orientation: String? = null,
				 val location: String? = null,
				 val id: String? = null) : Parcelable