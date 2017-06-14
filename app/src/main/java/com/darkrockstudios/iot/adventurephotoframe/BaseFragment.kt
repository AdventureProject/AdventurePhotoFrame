package com.darkrockstudios.iot.adventurephotoframe

import android.app.DialogFragment
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by adamw on 12/28/2016.
 */

abstract class BaseFragment : DialogFragment()
{
	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
	{
		val view = inflater.inflate(resourceLayout, container, false)

		return view
	}

	@get:LayoutRes
	protected abstract val resourceLayout: Int
}
