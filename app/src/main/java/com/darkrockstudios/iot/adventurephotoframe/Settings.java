package com.darkrockstudios.iot.adventurephotoframe;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

/**
 * Created by adamw on 12/22/2016.
 */

public class Settings
{
	private static final String KEY_UPDATE_FREQUENCY     = "UPDATE_FREQUENCY";
	private static final long   DEFAULT_UPDATE_FREQUENCY = 5L * 60L * 1000L;

	private static final String KEY_PHOTOFRAME_ID = "PHOTOFRAME_ID";

	private static final String KEY_FIRST_RUN = "FIRST_RUN";

	public static boolean getFirstRun( @NonNull final Context context )
	{
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences( context );
		return settings.getBoolean( KEY_FIRST_RUN, true );
	}

	public static void setFirstRunDone( @NonNull final Context context )
	{
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences( context );
		settings.edit().putBoolean( KEY_FIRST_RUN, false ).apply();
	}

	public static long getPhotoFrameId( @NonNull final Context context )
	{
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences( context );
		return settings.getLong( KEY_PHOTOFRAME_ID, 0 );
	}

	public static void setPhotoFrameId( @NonNull final Context context, final long photoFrameId )
	{
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences( context );
		settings.edit().putLong( KEY_PHOTOFRAME_ID, photoFrameId ).commit();
	}

	public static long getUpdateFrequency( @NonNull final Context context )
	{
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences( context );
		return settings.getLong( KEY_UPDATE_FREQUENCY, DEFAULT_UPDATE_FREQUENCY );
	}

	public static void setUpdateFrequency( @NonNull final Context context, final long minutes )
	{
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences( context );
		settings.edit().putLong( KEY_UPDATE_FREQUENCY, minutes * 60L * 1000L ).apply();
	}
}
