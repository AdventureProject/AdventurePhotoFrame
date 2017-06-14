package com.darkrockstudios.iot.adventurephotoframe

import android.app.Activity
import android.os.Bundle
import android.os.PersistableBundle
import android.support.annotation.LayoutRes

/**
 * Created by adamw on 12/22/2016.
 */

abstract class BaseActivity : Activity()
{
	override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?)
	{
		super.onCreate(savedInstanceState, persistentState)

		initialize()
	}

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)

		initialize()
	}

	private fun initialize()
	{
		setContentView(contentLayout)
	}

	@get:LayoutRes
	abstract val contentLayout: Int
}
