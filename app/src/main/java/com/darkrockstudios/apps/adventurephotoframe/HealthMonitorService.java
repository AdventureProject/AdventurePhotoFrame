package com.darkrockstudios.apps.adventurephotoframe;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

import retrofit2.Call;
import retrofit2.Response;

public class HealthMonitorService extends JobService
{
	private static final String TAG = HealthMonitorService.class.getSimpleName();

	public static final int  JOB_ID_PERIODIC = 1;
	public static final int  JOB_ID_INITIAL  = 2;
	public static final long JOB_PERIOD      = 24L * 60L * 60L * 1000L;

	@Override
	public boolean onStartJob( JobParameters params )
	{
		Log.i( TAG, "Checking in with Health Monitor..." );

		Call<Void> call = App.get().getNetworking().m_photoFrameService.healthCheckIn( App.get().getPhotoFrameId(),
		                                                                               BuildConfig.VERSION_CODE );
		call.enqueue( new Callback( params ) );

		return true;
	}

	@Override
	public boolean onStopJob( JobParameters params )
	{
		return true;
	}

	private class Callback implements retrofit2.Callback<Void>
	{
		private final JobParameters m_params;

		Callback( JobParameters params )
		{
			m_params = params;
		}

		@Override
		public void onResponse( Call<Void> call, Response<Void> response )
		{
			Log.i( TAG, "Health Check in complete" );
			jobFinished( m_params, false );
		}

		@Override
		public void onFailure( Call<Void> call, Throwable t )
		{
			Log.w( TAG, "Health Check in failed." );
			jobFinished( m_params, true );
		}
	}
}
