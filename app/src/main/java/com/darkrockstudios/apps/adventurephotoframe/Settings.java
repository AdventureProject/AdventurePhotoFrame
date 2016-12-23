package com.darkrockstudios.apps.adventurephotoframe;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

/**
 * Created by adamw on 12/22/2016.
 */

public class Settings
{
	private static final String KEY_UPDATE_FREQUENCY     = "UPDATE_FREQUENCY";
	private static final long   DEFAULT_UPDATE_FREQUENCY = 5L * 60L * 1000L;

	public static long getUpdateFrequency( @NonNull final Activity context )
	{
		SharedPreferences settings = context.getPreferences( Context.MODE_PRIVATE );
		return settings.getLong( KEY_UPDATE_FREQUENCY, DEFAULT_UPDATE_FREQUENCY );
	}

	public static void setUpdateFrequency( @NonNull final Activity context, final long minutes )
	{
		SharedPreferences settings = context.getPreferences( Context.MODE_PRIVATE );
		settings.edit().putLong( KEY_UPDATE_FREQUENCY, minutes * 60L * 1000L ).apply();
	}
}
