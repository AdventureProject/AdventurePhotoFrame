package com.darkrockstudios.iot.adventurephotoframe

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.darkrockstudios.iot.adventurephotoframe.data.Photo
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_photo_info.*
import me.eugeniomarletti.extras.ActivityCompanion
import me.eugeniomarletti.extras.intent.IntentExtra
import me.eugeniomarletti.extras.intent.base.Parcelable

class PhotoInfoActivity : BaseActivity()
{
	companion object : ActivityCompanion<IntentOptions>(IntentOptions, PhotoInfoActivity::class)
	{
		private val TAG = PhotoInfoActivity::class.java.simpleName
	}

	object IntentOptions
	{
		var Intent.photoInfo by IntentExtra.Parcelable<Photo>()
	}

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)

		with(PhotoInfoActivity.IntentOptions) {
			PHOTOINFO_title.text = intent.photoInfo?.title
			PHOTOINFO_date.text = intent.photoInfo?.date

			val location = intent.photoInfo?.location
			if (location != null && !TextUtils.isEmpty(location))
			{
				Picasso.with(this@PhotoInfoActivity)
						.load(getZoomedInMapUrl(location))
						.into(PHOTOINFO_map_close)

				Picasso.with(this@PhotoInfoActivity)
						.load(getZoomedOutMapUrl(location))
						.into(PHOTOINFO_map_far)
			}
			else
			{
				Log.i(TAG, "No Location for Photo")
			}
		}

		PHOTOINFO_container.setOnClickListener(this::onClose)
	}

	override val contentLayout: Int
		get() = R.layout.activity_photo_info

	fun onClose(view: View)
	{
		finish()
	}

	private fun getZoomedInMapUrl(location: String): String
	{
		val baseUrl = "http://maps.googleapis.com/maps/api/staticmap?center=%2\$s&zoom=15&scale=1&size=400x480&maptype=terrain&key=%1\$s&format=png&visual_refresh=true&markers=size:mid%%7Ccolor:0xff0000%%7Clabel:%%7C%2\$s"

		val API_KEY = getString(R.string.GOOGLE_MAPS_API_KEY)
		return String.format(baseUrl, API_KEY, location)
	}

	private fun getZoomedOutMapUrl(location: String): String
	{
		val baseUrl = "http://maps.googleapis.com/maps/api/staticmap?center=%2\$s&zoom=6&scale=1&size=400x480&maptype=terrain&key=%1\$s&format=png&visual_refresh=true&markers=size:mid%%7Ccolor:0xff0000%%7Clabel:%%7C%2\$s"
		val API_KEY = getString(R.string.GOOGLE_MAPS_API_KEY)
		return String.format(baseUrl, API_KEY, location)
	}
}
