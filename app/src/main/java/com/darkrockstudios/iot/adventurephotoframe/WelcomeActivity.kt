package com.darkrockstudios.iot.adventurephotoframe

import android.os.Bundle
import android.support.v4.app.Fragment
import com.darkrockstudios.iot.adventurephotoframe.settings.Settings
import com.github.paolorotolo.appintro.AppIntro
import com.github.paolorotolo.appintro.AppIntroFragment
import com.github.paolorotolo.appintro.model.SliderPage
import me.eugeniomarletti.extras.SimpleActivityCompanion

class WelcomeActivity : AppIntro()
{
	companion object : SimpleActivityCompanion(WelcomeActivity::class)

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)

		showSkipButton(false)

		var page = SliderPage()
		page.title = getString(R.string.WELCOME_slide1_title)
		page.description = getString(R.string.WELCOME_slide1_description)
		page.imageDrawable = R.drawable.welcome_01
		page.bgColor = getColor(R.color.WELCOME_slide1_background)
		addSlide(AppIntroFragment.newInstance(page))

		page = SliderPage()
		page.title = getString(R.string.WELCOME_slide2_title)
		page.description = getString(R.string.WELCOME_slide2_description)
		page.imageDrawable = R.drawable.welcome_02
		page.bgColor = getColor(R.color.WELCOME_slide2_background)
		addSlide(AppIntroFragment.newInstance(page))

		page = SliderPage()
		page.title = getString(R.string.WELCOME_slide3_title)
		page.description = getString(R.string.WELCOME_slide3_description)
		page.imageDrawable = R.drawable.welcome_03
		page.bgColor = getColor(R.color.WELCOME_slide3_background)
		addSlide(AppIntroFragment.newInstance(page))

		page = SliderPage()
		page.title = getString(R.string.WELCOME_slide4_title)
		page.description = getString(R.string.WELCOME_slide4_description)
		page.imageDrawable = R.drawable.welcome_04
		page.bgColor = getColor(R.color.WELCOME_slide4_background)
		addSlide(AppIntroFragment.newInstance(page))

		page = SliderPage()
		page.title = getString(R.string.WELCOME_slide5_title)
		page.description = getString(R.string.WELCOME_slide5_description)
		page.imageDrawable = R.drawable.welcome_05
		page.bgColor = getColor(R.color.WELCOME_slide5_background)
		addSlide(AppIntroFragment.newInstance(page))

		page = SliderPage()
		page.title = getString(R.string.WELCOME_slide6_title)
		page.description = getString(R.string.WELCOME_slide6_description)
		page.imageDrawable = R.drawable.welcome_06
		page.bgColor = getColor(R.color.WELCOME_slide6_background)
		addSlide(AppIntroFragment.newInstance(page))

		page = SliderPage()
		page.title = getString(R.string.WELCOME_slide7_title)
		page.description = getString(R.string.WELCOME_slide7_description)
		page.imageDrawable = R.drawable.welcome_07
		page.bgColor = getColor(R.color.WELCOME_slide7_background)
		addSlide(AppIntroFragment.newInstance(page))

		page = SliderPage()
		page.title = getString(R.string.WELCOME_slide8_title)
		page.description = getString(R.string.WELCOME_slide8_description)
		page.imageDrawable = R.drawable.welcome_08
		page.bgColor = getColor(R.color.WELCOME_slide8_background)
		addSlide(AppIntroFragment.newInstance(page))
	}

	override fun onDonePressed(currentFragment: Fragment?)
	{
		super.onDonePressed(currentFragment)

		Settings.setFirstRunDone(this)

		SettingsActivity.start(this)
	}
}
