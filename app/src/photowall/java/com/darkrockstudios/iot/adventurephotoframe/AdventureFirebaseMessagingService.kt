package com.darkrockstudios.iot.adventurephotoframe

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class AdventureFirebaseMessagingService : FirebaseMessagingService()
{
	companion object
	{
		const val KEY_PHOTO_DATA = "photo_id"
	}

	override fun onMessageReceived(remoteMessage: RemoteMessage?)
	{
		remoteMessage?.let {
			if (remoteMessage.data.isNotEmpty() && remoteMessage.data.containsKey(KEY_PHOTO_DATA))
			{
				Log.d("NFC", "Message data payload: " + remoteMessage.data)
				//remoteMessage.notification.body

				val photoIdStr = remoteMessage.data.get(KEY_PHOTO_DATA)
				if (photoIdStr != null)
				{
					val photoId = photoIdStr.toLongOrNull()
					photoId.let {
						PhotoInfoActivity.start(this) {
							it.photoId = photoId
						}
					}
				}
			}
		}
	}
}