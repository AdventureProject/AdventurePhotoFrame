package com.darkrockstudios.iot.adventurephotoframe.application

import android.annotation.SuppressLint
import android.app.Application
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import com.darkrockstudios.iot.adventurephotoframe.api.Networking
import com.darkrockstudios.iot.adventurephotoframe.healthmonitor.HealthMonitorService
import com.darkrockstudios.iot.adventurephotoframe.settings.Settings
import com.google.firebase.analytics.FirebaseAnalytics
import java.util.*


/**
 * Created by adamw on 12/21/2016.
 */

open class App : Application()
{
	lateinit var analytics: FirebaseAnalytics
		private set

	lateinit var networking: Networking
		private set

	val deviceId: String
		@SuppressLint("HardwareIds")
		get() = android.provider.Settings.Secure.getString(contentResolver, android.provider.Settings.Secure.ANDROID_ID)

	// Get 4 unique digits
	val photoFrameId: Long
		@SuppressLint("DefaultLocale")
		get()
		{
			var photoFrameId = Settings.getPhotoFrameId(this)
			if (photoFrameId == 0L)
			{
				val uuid = UUID.randomUUID()
				photoFrameId = java.lang.Long.parseLong(
						String.format("%d", Math.abs(uuid.leastSignificantBits)).substring(0, 4))

				Settings.setPhotoFrameId(this, photoFrameId)

				healthMonitorInitialCheckin(this)
			}

			return photoFrameId
		}

	override fun onCreate()
	{
		super.onCreate()
		inst = this

		analytics = FirebaseAnalytics.getInstance(this)

		networking = Networking()

		setupHealthMonitor()

		// Ensure the photo frame is in manual brightness mode
		/*
		android.provider.Settings.System.putInt( getContentResolver(),
		                                         android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE,
		                                         android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL );

		android.provider.Settings.System.putInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS, 1 );
		*/
	}

	private fun setupHealthMonitor()
	{
		val serviceComponent = ComponentName(this, HealthMonitorService::class.java)

		val builder = JobInfo.Builder(HealthMonitorService.JOB_ID_PERIODIC, serviceComponent)
		builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
		builder.setPeriodic(HealthMonitorService.JOB_PERIOD)
		builder.setBackoffCriteria(1000, JobInfo.BACKOFF_POLICY_EXPONENTIAL)

		val jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
		jobScheduler.schedule(builder.build())
	}

	companion object
	{
		private val TAG = App::class.java.simpleName

		lateinit var inst: App
			private set

		private fun healthMonitorInitialCheckin(context: Context)
		{
			val serviceComponent = ComponentName(context, HealthMonitorService::class.java)

			val builder = JobInfo.Builder(HealthMonitorService.JOB_ID_INITIAL, serviceComponent)
			builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
			builder.setMinimumLatency(1000)
			builder.setOverrideDeadline(1000)
			builder.setPersisted(true)
			builder.setBackoffCriteria(1000, JobInfo.BACKOFF_POLICY_EXPONENTIAL)

			val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
			jobScheduler.schedule(builder.build())
		}
	}
}
