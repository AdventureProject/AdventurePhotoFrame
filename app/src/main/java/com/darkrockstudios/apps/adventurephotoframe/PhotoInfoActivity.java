package com.darkrockstudios.apps.adventurephotoframe;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.darkrockstudios.apps.adventurephotoframe.data.Photo;
import com.f2prateek.dart.InjectExtra;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.OnClick;

public class PhotoInfoActivity extends BaseActivity
{
	private static final String TAG = PhotoInfoActivity.class.getSimpleName();

	public static final String EXTRA_PHOTO_INFO = "PHOTO_INFO";

	@InjectExtra(EXTRA_PHOTO_INFO)
	Photo m_photoInfo;

	@BindView(R.id.PHOTOINFO_title)
	TextView m_titleView;

	@BindView(R.id.PHOTOINFO_date)
	TextView m_dateView;

	@BindView(R.id.PHOTOINFO_map_close)
	ImageView m_mapCloseView;

	@BindView(R.id.PHOTOINFO_map_far)
	ImageView m_mapFarView;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

		m_titleView.setText( m_photoInfo.title );
		m_dateView.setText( m_photoInfo.date );

		if( !TextUtils.isEmpty( m_photoInfo.location ) )
		{
			Picasso.with( this )
			       .load( getZoomedInMapUrl( m_photoInfo.location ) )
			       .into( m_mapCloseView );

			Picasso.with( this )
			       .load( getZoomedOutMapUrl( m_photoInfo.location ) )
			       .into( m_mapFarView );
		}
		else
		{
			Log.i( TAG, "No Location for Photo" );
		}
	}

	@Override
	public int getContentLayout()
	{
		return R.layout.activity_photo_info;
	}

	@OnClick(R.id.PHOTOINFO_container)
	public void onClose()
	{
		finish();
	}

	private String getZoomedInMapUrl( String location )
	{
		String baseUrl =
				"http://maps.googleapis.com/maps/api/staticmap?center=%2$s&zoom=15&scale=1&size=400x480&maptype=terrain&key=%1$s&format=png&visual_refresh=true&markers=size:mid%%7Ccolor:0xff0000%%7Clabel:%%7C%2$s";

		String API_KEY = getString( R.string.GOOGLE_MAPS_API_KEY );
		return String.format( baseUrl, API_KEY, location );
	}

	private String getZoomedOutMapUrl( String location )
	{
		String baseUrl =
				"http://maps.googleapis.com/maps/api/staticmap?center=%2$s&zoom=6&scale=1&size=400x480&maptype=terrain&key=%1$s&format=png&visual_refresh=true&markers=size:mid%%7Ccolor:0xff0000%%7Clabel:%%7C%2$s";
		String API_KEY = getString( R.string.GOOGLE_MAPS_API_KEY );
		return String.format( baseUrl, API_KEY, location );
	}
}
