package com.darkrockstudios.iot.adventurephotoframe.data

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by adamw on 12/21/2016.
 */

data class Photo(val title: String? = null,
                 val description: String? = null,
                 val date: String? = null,
                 val image: String? = null,
                 val url: String? = null,
                 val location: String? = null) : Parcelable {
	override fun toString(): String = title ?: "null"

	companion object
	{
		@JvmField val CREATOR: Parcelable.Creator<Photo> = object : Parcelable.Creator<Photo>
		{
			override fun createFromParcel(source: Parcel): Photo = Photo(source)
			override fun newArray(size: Int): Array<Photo?> = arrayOfNulls(size)
		}
	}

	constructor(source: Parcel) : this(
	source.readString(),
	source.readString(),
	source.readString(),
	source.readString(),
	source.readString(),
	source.readString()
	)

	override fun describeContents() = 0

	override fun writeToParcel(dest: Parcel, flags: Int)
	{
		dest.writeString(title)
		dest.writeString(description)
		dest.writeString(date)
		dest.writeString(image)
		dest.writeString(url)
		dest.writeString(location)
	}
}
