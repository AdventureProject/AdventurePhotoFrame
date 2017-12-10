package com.darkrockstudios.iot.adventurephotoframe

import android.content.ContentValues.TAG
import android.content.Context
import android.preference.PreferenceManager
import android.util.Log
import com.darkrockstudios.iot.adventurephotoframe.data.RegistrationResponse
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import retrofit2.Call
import retrofit2.Response


class AdventureFirebaseInstanceIDService : FirebaseInstanceIdService()
{
	companion object
	{
		private const val KEY_REGISTERED = "registered_with_server"
		fun isRegisteredWithServer(context: Context): Boolean
		{
			val settings = PreferenceManager.getDefaultSharedPreferences(context)
			return settings.getBoolean(KEY_REGISTERED, false)
		}

		fun markAsRegistered(context: Context)
		{
			val settings = PreferenceManager.getDefaultSharedPreferences(context)
			settings.edit().putBoolean(KEY_REGISTERED, true).apply()
		}

		fun clearRegistered(context: Context)
		{
			val settings = PreferenceManager.getDefaultSharedPreferences(context)
			settings.edit().remove(KEY_REGISTERED).apply()
		}
	}

	override fun onTokenRefresh()
	{
		// Get updated InstanceID token.
		val refreshedToken = FirebaseInstanceId.getInstance().token
		Log.d(TAG, "Refreshed token: " + refreshedToken!!)

		// If you want to send messages to this application instance or
		// manage this apps subscriptions on the server side, send the
		// Instance ID token to your app server.
		sendRegistrationToServer(refreshedToken)
	}

	private fun sendRegistrationToServer(refreshedToken: String)
	{
		clearRegistered(this)

		val call = App.inst.networking.m_photoFrameService.photoWallNfc(App.inst.photoFrameId,
		                                                                refreshedToken)
		call.enqueue(Callback())
	}

	private inner class Callback : retrofit2.Callback<RegistrationResponse>
	{
		override fun onFailure(call: Call<RegistrationResponse>?, t: Throwable?)
		{
			Log.d("NFC", "Registration Failed")
			Log.d("NFC", t?.message)
			t?.printStackTrace()
		}

		override fun onResponse(call: Call<RegistrationResponse>?, response: Response<RegistrationResponse>?)
		{
			if (response?.body()?.success == true)
			{
				Log.d("NFC", "Registration Success!")
				markAsRegistered(this@AdventureFirebaseInstanceIDService)
			}
			else
			{
				Log.d("NFC", "Registration rejected")
			}
		}
	}
}
