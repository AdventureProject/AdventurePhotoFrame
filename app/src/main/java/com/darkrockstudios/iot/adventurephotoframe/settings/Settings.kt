package com.darkrockstudios.iot.adventurephotoframe.settings

import android.content.Context
import android.preference.PreferenceManager

/**
 * Created by adamw on 12/22/2016.
 */

object Settings
{
	private val KEY_UPDATE_FREQUENCY = "UPDATE_FREQUENCY"
	private val DEFAULT_UPDATE_FREQUENCY = 5L * 60L * 1000L

	private val KEY_PHOTOFRAME_ID = "PHOTOFRAME_ID"

	private val KEY_FIRST_RUN = "FIRST_RUN"

	fun getFirstRun(context: Context): Boolean
	{
		val settings = PreferenceManager.getDefaultSharedPreferences(context)
		return settings.getBoolean(KEY_FIRST_RUN, true)
	}

	fun setFirstRunDone(context: Context)
	{
		val settings = PreferenceManager.getDefaultSharedPreferences(context)
		settings.edit().putBoolean(KEY_FIRST_RUN, false).apply()
	}

	fun getPhotoFrameId(context: Context): Long
	{
		val settings = PreferenceManager.getDefaultSharedPreferences(context)
		return settings.getLong(KEY_PHOTOFRAME_ID, 0)
	}

	fun setPhotoFrameId(context: Context, photoFrameId: Long)
	{
		val settings = PreferenceManager.getDefaultSharedPreferences(context)
		settings.edit().putLong(KEY_PHOTOFRAME_ID, photoFrameId).commit()
	}

	fun getUpdateFrequency(context: Context): Long
	{
		val settings = PreferenceManager.getDefaultSharedPreferences(context)
		return settings.getLong(KEY_UPDATE_FREQUENCY, DEFAULT_UPDATE_FREQUENCY)
	}

	fun setUpdateFrequency(context: Context, minutes: Long)
	{
		val settings = PreferenceManager.getDefaultSharedPreferences(context)
		settings.edit().putLong(KEY_UPDATE_FREQUENCY, minutes * 60L * 1000L).apply()
	}
}
