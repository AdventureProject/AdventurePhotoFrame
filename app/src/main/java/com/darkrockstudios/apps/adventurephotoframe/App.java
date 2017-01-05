package com.darkrockstudios.apps.adventurephotoframe;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;

import com.darkrockstudios.apps.adventurephotoframe.api.Networking;

import java.util.UUID;

/**
 * Created by adamw on 12/21/2016.
 */

public class App extends Application
{
	private static App m_app;

	public static App get()
	{
		return m_app;
	}

	private Networking m_networking;

	public Networking getNetworking()
	{
		return m_networking;
	}

	@SuppressLint("DefaultLocale")
	public long getPhotoFrameId()
	{
		long photoFrameId = Settings.getPhotoFrameId( this );
		if( photoFrameId == 0 )
		{
			// Get 4 unique digits
			UUID uuid = UUID.randomUUID();
			photoFrameId =
					Long.parseLong( String.format( "%d", Math.abs( uuid.getLeastSignificantBits() ) ).substring( 0, 4 ) );

			Settings.setPhotoFrameId( this, photoFrameId );

			healthMonitorInitialCheckin();
		}

		return photoFrameId;
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
		m_app = this;

		m_networking = new Networking();

		setupHealthMonitor();
	}

	private void setupHealthMonitor()
	{
		ComponentName serviceComponent = new ComponentName( this, HealthMonitorService.class );

		JobInfo.Builder builder = new JobInfo.Builder( HealthMonitorService.JOB_ID_PERIODIC, serviceComponent );
		builder.setRequiredNetworkType( JobInfo.NETWORK_TYPE_ANY );
		builder.setPeriodic( HealthMonitorService.JOB_PERIOD );
		builder.setBackoffCriteria( 1000, JobInfo.BACKOFF_POLICY_EXPONENTIAL );

		JobScheduler jobScheduler = (JobScheduler) getSystemService( Context.JOB_SCHEDULER_SERVICE );
		jobScheduler.schedule( builder.build() );

	}

	private void healthMonitorInitialCheckin()
	{
		ComponentName serviceComponent = new ComponentName( this, HealthMonitorService.class );

		JobInfo.Builder builder = new JobInfo.Builder( HealthMonitorService.JOB_ID_INITIAL, serviceComponent );
		builder.setRequiredNetworkType( JobInfo.NETWORK_TYPE_ANY );
		builder.setMinimumLatency( 1000 );
		builder.setOverrideDeadline( 1000 );
		builder.setPersisted( true );
		builder.setBackoffCriteria( 1000, JobInfo.BACKOFF_POLICY_EXPONENTIAL );

		JobScheduler jobScheduler = (JobScheduler) getSystemService( Context.JOB_SCHEDULER_SERVICE );
		jobScheduler.schedule( builder.build() );

	}
}
