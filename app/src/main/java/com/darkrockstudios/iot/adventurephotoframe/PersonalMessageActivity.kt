package com.darkrockstudios.iot.adventurephotoframe

import android.content.Intent
import android.os.Bundle
import com.darkrockstudios.iot.adventurephotoframe.base.BaseActivity
import kotlinx.android.synthetic.main.activity_personal_message.*
import me.eugeniomarletti.extras.ActivityCompanion
import me.eugeniomarletti.extras.intent.IntentExtra
import me.eugeniomarletti.extras.intent.base.String

class PersonalMessageActivity : BaseActivity()
{
	companion object : ActivityCompanion<PersonalMessageActivity.IntentOptions>(PersonalMessageActivity.IntentOptions, PersonalMessageActivity::class)

	object IntentOptions
	{
		var Intent.messageAuthor by IntentExtra.String()
		var Intent.messageContent by IntentExtra.String()
		var Intent.messageRecipient by IntentExtra.String()
	}

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_personal_message)

		val nbs = "\u00A0"

		with(PersonalMessageActivity.IntentOptions) {
			PERSONALMESSAGE_recipient.text = intent.messageRecipient
			PERSONALMESSAGE_content.text = intent.messageContent
			PERSONALMESSAGE_author.text = nbs + intent.messageAuthor + nbs // since this view is italic we need these spaces to prevent cutoff characters
		}
	}

	override val contentLayout: Int
		get() = R.layout.activity_personal_message
}
