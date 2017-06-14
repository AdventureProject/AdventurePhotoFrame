package com.darkrockstudios.iot.adventurephotoframe

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import com.darkrockstudios.iot.adventurephotoframe.data.Photo
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.tutorial.*
import me.eugeniomarletti.extras.SimpleActivityCompanion
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : BaseActivity()
{
	companion object : SimpleActivityCompanion(MainActivity::class)
	{
		private val TAG = MainActivity::class.java.simpleName

		private val FAILURE_BACKOFF = 1L * 60L * 1000L
	}

	private var m_currentPhoto: Photo? = null

	private val m_handler: Handler = Handler()
	private val m_updateTask: UpdatePhotoTask = UpdatePhotoTask()

	private var m_updateFrequency: Long = 0

	private val m_connectionListener: ConnectionStateReceiver? = ConnectionStateReceiver()

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)

		m_updateFrequency = Settings.getUpdateFrequency(this)

		TUTORIAL_tutorial.setOnClickListener(this::onTutorial)
		TUTORIAL_next_photo.setOnClickListener(this::onNext)
		TUTORIAL_photo_info.setOnClickListener(this::onPhotoInfo)
		TUTORIAL_settings.setOnClickListener(this::onSettings)
		TUTORIAL_about.setOnClickListener(this::onAbout)

		requestNewPhoto()

		handleFirstRun()
	}

	private fun handleFirstRun()
	{
		if (Settings.getFirstRun(this))
		{
			WelcomeActivity.start(this)
		}
	}

	override fun onStart()
	{
		super.onStart()

		val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
		registerReceiver(m_connectionListener, intentFilter)
	}

	override fun onResume()
	{
		super.onResume()
		m_updateFrequency = Settings.getUpdateFrequency(this)

		scheduleUpdateTask(m_updateFrequency)

		updateConnectionStatus()
	}

	override fun onPause()
	{
		super.onPause()
		m_handler.removeCallbacks(m_updateTask)
	}

	override fun onStop()
	{
		super.onStop()

		unregisterReceiver(m_connectionListener)
	}

	override val contentLayout: Int
		get() = R.layout.activity_main

	private fun scheduleUpdateTask(delay: Long)
	{
		m_handler.removeCallbacks(m_updateTask)
		m_handler.postDelayed(m_updateTask, delay)
	}

	private fun requestNewPhoto()
	{
		m_handler.removeCallbacks(m_updateTask)

		hideTutorial()
		PHOTOFRAME_loading.visibility = View.VISIBLE

		val call = App.inst.networking.m_photoFrameService.getPhoto(App.inst.photoFrameId)
		Log.d(TAG, "Requesting photo info...")
		call.enqueue(PhotoCallback())
	}

	private fun updatePhoto(photo: Photo)
	{
		m_currentPhoto = photo

		val photoView = PHOTOFRAME_photo
		if (photoView != null)
		{
			Log.i(TAG, "Loading image: " + photo.image)

			Picasso.with(this)
					.load(photo.image)
					.placeholder(R.drawable.loading)
					.error(R.drawable.no_image)
					.resize(photoView.measuredWidth, photoView.measuredHeight)
					.centerCrop()
					.into(photoView, ImageCallback())
		}
		else
		{
			Log.i(TAG, "Can't load image, View is not ready.")
		}
	}

	private fun updateConnectionStatus()
	{
		val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

		val activeNetwork = cm.activeNetworkInfo
		val isConnected = activeNetwork != null && activeNetwork.isConnected

		PHOTOFRAME_wifi_down.visibility = if (isConnected) View.GONE else View.VISIBLE
	}

	private fun showTutorial()
	{
		TUTORIAL_container.alpha = 1.0f
		/*
		ObjectAnimator anim = ObjectAnimator.ofFloat( TUTORIAL_container, "alpha", 0f, 1f );
		anim.setDuration( 50 );
		anim.start();
		*/
	}

	private fun hideTutorial()
	{
		TUTORIAL_container.alpha = 0.0f
		/*
		ObjectAnimator anim = ObjectAnimator.ofFloat( TUTORIAL_container, "alpha", 1f, 0f );
		anim.setDuration( 50 );
		anim.start();
		*/
	}

	private val isTutorialShowing: Boolean
		get() = TUTORIAL_container.alpha == 1.0f

	private inner class ImageCallback : com.squareup.picasso.Callback
	{
		override fun onSuccess()
		{
			Log.i(TAG, "Image load successful!")
			PHOTOFRAME_loading.visibility = View.GONE

			scheduleUpdateTask(m_updateFrequency)
		}

		override fun onError()
		{
			Log.w(TAG, "Image load failed.")
			PHOTOFRAME_loading.visibility = View.GONE

			scheduleUpdateTask(FAILURE_BACKOFF)
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
		}

		override fun onFailure(call: Call<Photo>, t: Throwable)
		{
			Log.w(TAG, "Failed to get photo info... " + t)
			scheduleUpdateTask(FAILURE_BACKOFF)
		}
	}

	fun onTutorial(view: View)
	{
		if (isTutorialShowing)
		{
			hideTutorial()
		}
		else
		{
			showTutorial()
		}
	}

	fun onNext(view: View)
	{
		hideTutorial()
		requestNewPhoto()
	}

	fun onPhotoInfo(view: View)
	{
		hideTutorial()

		if (m_currentPhoto != null)
		{
			PhotoInfoActivity.start(this) {
				it.photoInfo = m_currentPhoto
			}
		}
		else
		{
			requestNewPhoto()
		}
	}

	fun onSettings(view: View)
	{
		hideTutorial()
		SettingsActivity.start(this)
	}

	fun onAbout(view: View)
	{
		hideTutorial()
		AboutActivity.start(this)
	}

	private inner class UpdatePhotoTask : Runnable
	{
		override fun run()
		{
			requestNewPhoto()
		}
	}

	private inner class ConnectionStateReceiver : BroadcastReceiver()
	{
		override fun onReceive(context: Context, intent: Intent)
		{
			updateConnectionStatus()
		}
	}
}
