package com.darkrockstudios.iot.adventurephotoframe

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.darkrockstudios.iot.adventurephotoframe.application.App
import com.darkrockstudios.iot.adventurephotoframe.base.BaseActivity
import com.darkrockstudios.iot.adventurephotoframe.data.Photo
import kotlinx.android.synthetic.main.activity_photo_info.*
import me.eugeniomarletti.extras.ActivityCompanion
import me.eugeniomarletti.extras.intent.IntentExtra
import me.eugeniomarletti.extras.intent.base.Long
import me.eugeniomarletti.extras.intent.base.Parcelable
import org.joda.time.format.DateTimeFormat
import org.unbescape.html.HtmlEscape
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class PhotoInfoActivity : BaseActivity()
{
	companion object : ActivityCompanion<IntentOptions>(IntentOptions, PhotoInfoActivity::class)
	{
		private val TAG = PhotoInfoActivity::class.java.simpleName
		private const val AUTO_HIDE = 5L * 1000L
		private const val AUTO_CLOSE = 5L * 60L * 1000L
	}

	object IntentOptions
	{
		var Intent.photoInfo by IntentExtra.Parcelable<Photo>()
		var Intent.photoId by IntentExtra.Long()
	}

	private lateinit var m_timer: Timer
	private var m_autoCloseDescriptionTask: AutoDescriptionCloseTask? = AutoDescriptionCloseTask()
	private var m_autoCloseTask: AutoCloseTask = AutoCloseTask()

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)

		m_timer = Timer()
		m_timer.schedule(m_autoCloseDescriptionTask, AUTO_HIDE)
		m_timer.schedule(m_autoCloseTask, AUTO_CLOSE)

		with(PhotoInfoActivity.IntentOptions) {
			if (intent.photoInfo != null)
			{
				Log.d(TAG, "Photo was supplied.")
				intent.photoInfo?.let { photo -> updatePhoto(photo) }
			}
			else
			{
				if (intent.photoId != null)
				{
					intent.photoId?.let { photoId ->
						val call = App.inst.networking.m_photoFrameService.getPhotoById(App.inst.photoFrameId, photoId)
						Log.d(TAG, "Requesting photo info... " + photoId)
						call.enqueue(PhotoCallback())
					}
				}
				else
				{
					Log.i(TAG, "Not enough data provided to show details")
					finish()
				}
			}
		}

		PHOTOINFO_container.setOnClickListener(this::onClose)

		PHOTOINFO_title.setOnClickListener(this::toggleDescription)
		PHOTOINFO_description.setOnClickListener(this::toggleDescription)
	}

	override fun onStop()
	{
		super.onStop()

		// Cancel any remaining timers
		m_timer.cancel()
	}

	private fun rescheduleAutoClose()
	{
		m_autoCloseTask.cancel()
		m_autoCloseTask = AutoCloseTask()
		m_timer.schedule(m_autoCloseTask, AUTO_CLOSE)
	}

	private inner class AutoDescriptionCloseTask : TimerTask()
	{
		override fun run()
		{
			Log.d(TAG, "Auto-hiding description")
			runOnUiThread { PHOTOINFO_description.visibility = View.GONE }
		}
	}

	private inner class AutoCloseTask : TimerTask()
	{
		override fun run()
		{
			Log.d(TAG, "Auto-closing")
			runOnUiThread { finish() }
		}
	}

	private inner class PhotoCallback : Callback<Photo>
	{
		override fun onResponse(call: Call<Photo>, response: Response<Photo>)
		{
			Log.d(TAG, "Got photo info...")
			val photo = response.body()
			if (photo != null)
			{
				Log.d(TAG, photo.toString())
				updatePhoto(photo)
			}
			else
			{
				PHOTOINFO_loading.visibility = View.GONE
				finish()
			}
		}

		override fun onFailure(call: Call<Photo>, t: Throwable)
		{
			Log.w(TAG, "Failed to get photo info... " + t)
			PHOTOINFO_loading.visibility = View.GONE
			finish()
		}
	}

	private fun updatePhoto(photo: Photo)
	{
		PHOTOINFO_loading.visibility = View.GONE

		PHOTOINFO_title.text = HtmlEscape.unescapeHtml(photo.title)
		PHOTOINFO_description.text = HtmlEscape.unescapeHtml(photo.description)

		val dateTime = DateTimeFormat.forPattern("yyy-MM-dd HH:mm:ss").parseDateTime(photo.date)
		PHOTOINFO_date.text = DateTimeFormat.forPattern("HH:mm - EEEE, MMMM ee, yyyy").print(dateTime)

		val location = photo.location
		if (location != null && !TextUtils.isEmpty(location))
		{
			GlideApp.with(this)
					.load(getZoomedInMapUrl(location))
					.placeholder(R.drawable.map_loading)
					.into(PHOTOINFO_map_close)

			GlideApp.with(this)
					.load(getZoomedOutMapUrl(location))
					.placeholder(R.drawable.map_loading)
					.into(PHOTOINFO_map_far)
		}
		else
		{
			Log.i(TAG, "No Location for Photo")
		}
	}

	private fun toggleDescription(view: View)
	{
		m_autoCloseDescriptionTask?.let {
			Log.d(TAG, "Canceling auto-dismiss description task.")
			it.cancel()
			m_autoCloseDescriptionTask = null
		}

		rescheduleAutoClose()

		if (PHOTOINFO_description.visibility == View.GONE)
		{
			PHOTOINFO_description.visibility = View.VISIBLE
		}
		else
		{
			PHOTOINFO_description.visibility = View.GONE
		}
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
