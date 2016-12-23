package com.darkrockstudios.apps.adventurephotoframe;

import android.content.Context;
import android.content.Intent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import butterknife.OnClick;

public class DebugActivity extends BaseActivity
{
	@Override
	public int getContentLayout()
	{
		return R.layout.activity_debug;
	}

	@OnClick(R.id.DEBUG_close)
	public void onClose()
	{
		finish();
	}

	@OnClick(R.id.DEBUG_enable_keyboards)
	public void onKeyboardSettings()
	{
		startActivityForResult( new Intent( android.provider.Settings.ACTION_INPUT_METHOD_SETTINGS ), 0 );
	}

	@OnClick(R.id.DEBUG_select_keyboards)
	public void onKeyboardSelect()
	{
		InputMethodManager imeManager =
				(InputMethodManager) getApplicationContext().getSystemService( Context.INPUT_METHOD_SERVICE );
		if( imeManager != null )
		{
			imeManager.showInputMethodPicker();
		}
		else
		{
			Toast.makeText( getApplicationContext(), "IME ERROR",
			                Toast.LENGTH_LONG ).show();
		}
	}
}
