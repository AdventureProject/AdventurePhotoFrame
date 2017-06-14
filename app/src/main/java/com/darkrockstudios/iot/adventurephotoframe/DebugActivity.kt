package com.darkrockstudios.iot.adventurephotoframe

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import kotlinx.android.synthetic.main.activity_debug.*

class DebugActivity : BaseActivity()
{
	override val contentLayout: Int
		get() = R.layout.activity_debug

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)

		DEBUG_enable_keyboards.setOnClickListener(this::onKeyboardSettings)
		DEBUG_select_keyboards.setOnClickListener(this::onKeyboardSelect)
	}

	fun onKeyboardSettings(view: View)
	{
		startActivityForResult(Intent(android.provider.Settings.ACTION_INPUT_METHOD_SETTINGS), 0)
	}

	fun onKeyboardSelect(view: View)
	{
		val imeManager = applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
		imeManager.showInputMethodPicker()
	}
}
