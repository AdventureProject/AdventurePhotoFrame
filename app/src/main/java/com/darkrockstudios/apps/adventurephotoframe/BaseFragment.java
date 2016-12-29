package com.darkrockstudios.apps.adventurephotoframe;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.f2prateek.dart.Dart;

import butterknife.ButterKnife;

/**
 * Created by adamw on 12/28/2016.
 */

public abstract class BaseFragment extends DialogFragment
{
	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		Dart.inject( this );
	}

	@Nullable
	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
	{
		View view = inflater.inflate( getResourceLayout(), container, false );
		ButterKnife.bind( this, view );

		return view;
	}

	@LayoutRes
	protected abstract int getResourceLayout();
}
