package com.darkrockstudios.iot.adventurephotoframe

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.view.View
import com.darkrockstudios.iot.adventurephotoframe.application.App
import com.darkrockstudios.iot.adventurephotoframe.base.BaseActivity
import kotlinx.android.synthetic.main.activity_about.*
import me.eugeniomarletti.extras.SimpleActivityCompanion

class AboutActivity : BaseActivity()
{
	companion object : SimpleActivityCompanion(AboutActivity::class)

	override fun onCreate(savedInstanceState: Bundle?)
	{
		actionBar.setDisplayHomeAsUpEnabled(true)

		super.onCreate(savedInstanceState)

		ABOUT_version.text = getString(R.string.SETTINGS_version, BuildConfig.VERSION_CODE)
		ABOUT_id.text = getString(R.string.SETTINGS_id, App.inst.photoFrameId)

		ABOUT_welcome_rerun.setOnClickListener(this::onWeclomeClick)
		ABOUT_reboot.setOnClickListener(this::onRebootClick)
	}

	@get:LayoutRes
	override val contentLayout: Int
		get() = R.layout.activity_about

	fun onWeclomeClick(view: View)
	{
		WelcomeActivity.start(this)
		finish()
	}

	fun onRebootClick(view: View)
	{

	}
}
