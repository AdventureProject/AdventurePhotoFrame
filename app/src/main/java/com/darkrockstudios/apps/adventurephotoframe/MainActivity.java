package com.darkrockstudios.apps.adventurephotoframe;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.darkrockstudios.apps.adventurephotoframe.data.Photo;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity
{
	private static final String TAG = MainActivity.class.getSimpleName();

	@BindView(R.id.PHOTOFRAME_photo)
	ImageView m_photoView;

	@BindView(R.id.PHOTOFRAME_loading)
	View m_progressView;

	@BindView(R.id.TUTORIAL_container)
	View m_tutorialView;

	private Photo m_currentPhoto;

	private Handler         m_handler;
	private UpdatePhotoTask m_updateTask;
	
	private long m_updateFrequency;

	private static final long FAILURE_BACKOFF  = 1L * 60L * 1000L;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

		m_updateFrequency = Settings.getUpdateFrequency( this );
		
		m_handler = new Handler();
		m_updateTask = new UpdatePhotoTask();
		requestNewPhoto();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		m_updateFrequency = Settings.getUpdateFrequency( this );

		scheduleUpdateTask( m_updateFrequency );
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		m_handler.removeCallbacks( m_updateTask );
	}

	@Override
	public int getContentLayout()
	{
		return R.layout.activity_main;
	}

	private void scheduleUpdateTask( final long delay )
	{
		m_handler.removeCallbacks( m_updateTask );
		m_handler.postDelayed( m_updateTask, delay );
	}

	private void requestNewPhoto()
	{
		m_handler.removeCallbacks( m_updateTask );

		hideTutorial();
		m_progressView.setVisibility( View.VISIBLE );

		Call<Photo> call = App.get().getNetworking().m_photoFrameService.getPhoto( 1 );
		Log.d( TAG, "Requesting photo info..." );
		call.enqueue( new PhotoCallback() );
	}

	private void updatePhoto( final Photo photo )
	{
		m_currentPhoto = photo;

		Log.i( TAG, "Loading image: " + photo.image );

		Picasso.with( this )
		       .load( m_currentPhoto.image )
		       .placeholder( R.drawable.loading )
		       .error( R.drawable.wifi_down )
		       .resize( m_photoView.getMeasuredWidth(), m_photoView.getMeasuredHeight() )
		       .centerCrop()
		       .into( m_photoView, new ImageCallback() );
	}

	private void showTutorial()
	{
		m_tutorialView.setAlpha( 1.0f );
		/*
		ObjectAnimator anim = ObjectAnimator.ofFloat( m_tutorialView, "alpha", 0f, 1f );
		anim.setDuration( 50 );
		anim.start();
		*/
	}

	private void hideTutorial()
	{
		m_tutorialView.setAlpha( 0.0f );
		/*
		ObjectAnimator anim = ObjectAnimator.ofFloat( m_tutorialView, "alpha", 1f, 0f );
		anim.setDuration( 50 );
		anim.start();
		*/
	}

	private boolean isTutorialShowing()
	{
		return m_tutorialView.getAlpha() == 1.0f;
	}

	private class ImageCallback implements com.squareup.picasso.Callback
	{
		@Override
		public void onSuccess()
		{
			Log.i( TAG, "Image load successful!" );
			m_progressView.setVisibility( View.GONE );

			scheduleUpdateTask( m_updateFrequency );
		}

		@Override
		public void onError()
		{
			Log.w( TAG, "Image load failed." );
			m_progressView.setVisibility( View.GONE );

			scheduleUpdateTask( FAILURE_BACKOFF );
		}
	}

	private class PhotoCallback implements Callback<Photo>
	{
		@Override
		public void onResponse( Call<Photo> call, Response<Photo> response )
		{
			Log.d( TAG, "Got photo info..." );
			Photo photo = response.body();
			if( photo != null )
			{
				Log.d( TAG, photo.toString() );
				updatePhoto( photo );
			}
		}

		@Override
		public void onFailure( Call<Photo> call, Throwable t )
		{
			Log.w( TAG, "Failed to get photo info... " + t );
			scheduleUpdateTask( FAILURE_BACKOFF );
		}
	}

	@OnClick(R.id.TUTORIAL_tutorial)
	public void onTutorial()
	{
		if( isTutorialShowing() )
		{
			hideTutorial();
		}
		else
		{
			showTutorial();
		}
	}

	@OnClick(R.id.TUTORIAL_next_photo)
	public void onNext()
	{
		hideTutorial();
		requestNewPhoto();
	}

	@OnClick(R.id.TUTORIAL_photo_info)
	public void onPhotoInfo()
	{
		hideTutorial();

		if( m_currentPhoto != null )
		{
			Intent intent = new Intent( this, PhotoInfoActivity.class );
			intent.putExtra( PhotoInfoActivity.EXTRA_PHOTO_INFO, Parcels.wrap( m_currentPhoto ) );
			startActivity( intent );
		}
		else
		{
			requestNewPhoto();
		}
	}

	@OnClick(R.id.TUTORIAL_settings)
	public void onSettings()
	{
		hideTutorial();
		startActivity( new Intent( this, SettingsActivity.class ) );
	}

	private class UpdatePhotoTask implements Runnable
	{
		@Override
		public void run()
		{
			requestNewPhoto();
		}
	}
}
