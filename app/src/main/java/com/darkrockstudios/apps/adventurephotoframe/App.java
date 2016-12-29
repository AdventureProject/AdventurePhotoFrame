package com.darkrockstudios.apps.adventurephotoframe;

import android.app.Application;

import com.darkrockstudios.apps.adventurephotoframe.api.Networking;

/**
 * Created by adamw on 12/21/2016.
 */

public class App extends Application
{
	private static App m_app;
	private static int m_photoFrameId;

	public static App get()
	{
		return m_app;
	}

	private Networking m_networking;

	public Networking getNetworking()
	{
		return m_networking;
	}

	public int getPhotoFrameId()
	{
		return m_photoFrameId;
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
		m_app = this;

		m_networking = new Networking();

		m_photoFrameId = getResources().getInteger( R.integer.PHOTOFRAME_ID );
	}
}
