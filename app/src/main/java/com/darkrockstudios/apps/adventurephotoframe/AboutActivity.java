package com.darkrockstudios.apps.adventurephotoframe;

import android.app.Activity;
import android.os.Bundle;

public class AboutActivity extends Activity
{
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

		getActionBar().setDisplayHomeAsUpEnabled( true );

		setContentView( R.layout.activity_about );
	}
}
