package com.darkrockstudios.iot.adventurephotoframe.pushmessaging

import com.darkrockstudios.iot.adventurephotoframe.PersonalMessageActivity
import com.darkrockstudios.iot.adventurephotoframe.PhotoInfoActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class AdventureFirebaseMessagingService : FirebaseMessagingService()
{
	companion object
	{
		const val KEY_MESSAGE_TYPE = "type"
		const val MESSAGE_TYPE_PHOTOWALL = "photowall"
		const val KEY_PHOTO_DATA = "photo_id"

		const val MESSAGE_TYPE_PERSONAL_MESSAGE = "personal_message"
		const val KEY_PERSONAL_MESSAGE_AUTHOR = "personal_message_author"
		const val KEY_PERSONAL_MESSAGE_RECIPIENT = "personal_message_recipient"
		const val KEY_PERSONAL_MESSAGE_CONTENT = "personal_message_content"
	}

	override fun onMessageReceived(remoteMessage: RemoteMessage?)
	{
		remoteMessage?.let {
			if (remoteMessage.data.isNotEmpty() && remoteMessage.data.containsKey(KEY_MESSAGE_TYPE))
			{
				when (remoteMessage.data[KEY_MESSAGE_TYPE])
				{
					MESSAGE_TYPE_PHOTOWALL -> onPhotoWall(remoteMessage.data[KEY_PHOTO_DATA])
					MESSAGE_TYPE_PERSONAL_MESSAGE -> onPersonalMessage(remoteMessage.data[KEY_PERSONAL_MESSAGE_AUTHOR],
					                                                   remoteMessage.data[KEY_PERSONAL_MESSAGE_CONTENT],
					                                                   remoteMessage.data[KEY_PERSONAL_MESSAGE_RECIPIENT])
				}
			}
		}
	}

	private fun onPhotoWall(photoIdStr: String?)
	{
		if (photoIdStr != null)
		{
			val photoId = photoIdStr.toLongOrNull()
			if (photoId != null)
			{
				PhotoInfoActivity.start(this) {
					it.photoId = photoId
				}
			}
		}
	}

	private fun onPersonalMessage(author: String?,
	                              content: String?,
	                              recipient: String?)
	{
		if (author != null && content != null && recipient != null)
		{
			PersonalMessageActivity.start(this) {
				it.messageAuthor = author
				it.messageContent = content
				it.messageRecipient = recipient
			}
		}
	}
}