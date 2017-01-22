package com.darkrockstudios.apps.adventurephotoframe;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;

public class AboutActivity extends BaseActivity
{
	@BindView(R.id.ABOUT_version)
	TextView m_versionView;

	@BindView(R.id.ABOUT_id)
	TextView m_idView;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		getActionBar().setDisplayHomeAsUpEnabled( true );

		super.onCreate( savedInstanceState );

		m_versionView.setText( getString( R.string.SETTINGS_version, BuildConfig.VERSION_CODE ) );
		m_idView.setText( getString( R.string.SETTINGS_id, App.get().getPhotoFrameId() ) );
	}

	@Override
	public int getContentLayout()
	{
		return R.layout.activity_about;
	}

	@OnClick(R.id.ABOUT_welcome_rerun)
	public void onWeclomeClick()
	{
		startActivity( new Intent( this, WelcomeActivity.class ) );
		finish();
	}
}
