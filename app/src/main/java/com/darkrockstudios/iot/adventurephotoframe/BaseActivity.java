package com.darkrockstudios.iot.adventurephotoframe;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.LayoutRes;

import com.f2prateek.dart.Dart;

import butterknife.ButterKnife;

/**
 * Created by adamw on 12/22/2016.
 */

public abstract class BaseActivity extends Activity
{
	@Override
	public void onCreate( Bundle savedInstanceState, PersistableBundle persistentState )
	{
		super.onCreate( savedInstanceState, persistentState );

		initialize();
	}

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

		initialize();
	}

	private void initialize()
	{
		Dart.inject( this );

		setContentView( getContentLayout() );
		ButterKnife.bind( this );
	}

	@LayoutRes
	abstract public int getContentLayout();
}
