package com.darkrockstudios.iot.adventurephotoframe.healthmonitor

import android.app.job.JobParameters
import android.app.job.JobService
import android.util.Log
import com.darkrockstudios.iot.adventurephotoframe.BuildConfig
import com.darkrockstudios.iot.adventurephotoframe.application.App
import retrofit2.Call
import retrofit2.Response

class HealthMonitorService : JobService()
{
	override fun onStartJob(params: JobParameters): Boolean
	{
		if (!BuildConfig.DEBUG)
		{
			Log.i(TAG, "Checking in with Health Monitor...")

			val call = App.inst.networking.m_photoFrameService.healthCheckIn(App.inst.photoFrameId,
																			 BuildConfig.VERSION_CODE,
																			 "")
			call.enqueue(Callback(params))
		}

		return true
	}

	override fun onStopJob(params: JobParameters): Boolean
	{
		return true
	}

	private inner class Callback internal constructor(private val m_params: JobParameters) : retrofit2.Callback<Void>
	{

		override fun onResponse(call: Call<Void>, response: Response<Void>)
		{
			Log.i(TAG, "Health Check in complete")
			jobFinished(m_params, false)
		}

		override fun onFailure(call: Call<Void>, t: Throwable)
		{
			Log.w(TAG, "Health Check in failed.")
			jobFinished(m_params, true)
		}
	}

	companion object
	{
		private val TAG = HealthMonitorService::class.java.simpleName

		val JOB_ID_PERIODIC = 1
		val JOB_ID_INITIAL = 2
		val JOB_PERIOD = 24L * 60L * 60L * 1000L
	}
}
