package com.darkrockstudios.iot.adventurephotoframe

import android.content.Context
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.darkrockstudios.iot.adventurephotoframe.application.App
import com.darkrockstudios.iot.adventurephotoframe.base.BaseActivity
import com.google.android.things.update.UpdateManager
import kotlinx.android.synthetic.main.activity_about.*
import me.eugeniomarletti.extras.SimpleActivityCompanion


class AboutActivity : BaseActivity()
{
	companion object : SimpleActivityCompanion(AboutActivity::class)

	sealed class UpdateChannels(val channelName: String, channelNameResource: Int, context: Context)
	{
		val channelDisplayName = context.getString(channelNameResource)

		override fun toString(): String = channelDisplayName

		class StableChannel(context: Context) : UpdateChannels("stable-channel", R.string.ABOUT_update_channel_stable, context)
		class BetaChannel(context: Context) : UpdateChannels("beta-channel", R.string.ABOUT_update_channel_beta, context)
		class DevChannel(context: Context) : UpdateChannels("dev-channel", R.string.ABOUT_update_channel_dev, context)
		class CanaryChannel(context: Context) : UpdateChannels("canary-channel", R.string.ABOUT_update_channel_canary, context)
	}

	override fun onCreate(savedInstanceState: Bundle?)
	{
		actionBar.setDisplayHomeAsUpEnabled(true)

		super.onCreate(savedInstanceState)

		ABOUT_version.text = getString(R.string.SETTINGS_version, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE)
		ABOUT_id.text = getString(R.string.SETTINGS_id, App.inst.photoFrameId)

		ABOUT_welcome_rerun.setOnClickListener(this::onWeclomeClick)

		setupUpdateChannels()
	}

	private fun setupUpdateChannels()
	{
		val updateChannels = if (BuildConfig.DEBUG)
		{
			arrayOf(UpdateChannels.StableChannel(this),
					UpdateChannels.BetaChannel(this),
					UpdateChannels.DevChannel(this),
					UpdateChannels.CanaryChannel(this))
		}
		else
		{
			arrayOf(UpdateChannels.StableChannel(this),
					UpdateChannels.BetaChannel(this))
		}

		ABOUT_update_channel_spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, updateChannels)

		var initialIndex = 0
		// Get the index of the currently selected channel
		val updateManager = UpdateManager.getInstance()
		for ((index, channel) in updateChannels.withIndex())
		{
			if (channel.channelName == updateManager.channel)
			{
				initialIndex = index
			}
		}

		Log.d("about", "Current update channel: " + updateManager.channel)

		ABOUT_update_channel_spinner.setSelection(initialIndex, false)

		ABOUT_update_channel_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener
		{
			override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long)
			{
				val selectedItem = parent.getItemAtPosition(position) as UpdateChannels
				UpdateManager.getInstance().channel = selectedItem.channelName
			}

			override fun onNothingSelected(parent: AdapterView<*>)
			{

			}
		}
	}

	@get:LayoutRes
	override val contentLayout: Int
		get() = R.layout.activity_about

	fun onWeclomeClick(view: View)
	{
		WelcomeActivity.start(this)
		finish()
	}
}
