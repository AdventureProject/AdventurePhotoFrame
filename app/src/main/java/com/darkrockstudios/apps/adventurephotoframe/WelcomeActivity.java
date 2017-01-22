package com.darkrockstudios.apps.adventurephotoframe;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class WelcomeActivity extends AppIntro
{
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

		showSkipButton( false );

		addSlide( AppIntroFragment.newInstance( getString( R.string.WELCOME_slide1_title ),
		                                        getString( R.string.WELCOME_slide1_description ),
		                                        R.drawable.welcome_01,
		                                        getColor( R.color.WELCOME_slide1_background ) ) );

		addSlide( AppIntroFragment.newInstance( getString( R.string.WELCOME_slide2_title ),
		                                        getString( R.string.WELCOME_slide2_description ),
		                                        R.drawable.welcome_02,
		                                        getColor( R.color.WELCOME_slide2_background ) ) );

		addSlide( AppIntroFragment.newInstance( getString( R.string.WELCOME_slide3_title ),
		                                        getString( R.string.WELCOME_slide3_description ),
		                                        R.drawable.welcome_03,
		                                        getColor( R.color.WELCOME_slide3_background ) ) );

		addSlide( AppIntroFragment.newInstance( getString( R.string.WELCOME_slide4_title ),
		                                        getString( R.string.WELCOME_slide4_description ),
		                                        R.drawable.welcome_04,
		                                        getColor( R.color.WELCOME_slide4_background ) ) );

		addSlide( AppIntroFragment.newInstance( getString( R.string.WELCOME_slide5_title ),
		                                        getString( R.string.WELCOME_slide5_description ),
		                                        R.drawable.welcome_05,
		                                        getColor( R.color.WELCOME_slide5_background ) ) );

		addSlide( AppIntroFragment.newInstance( getString( R.string.WELCOME_slide6_title ),
		                                        getString( R.string.WELCOME_slide6_description ),
		                                        R.drawable.welcome_06,
		                                        getColor( R.color.WELCOME_slide6_background ) ) );

		addSlide( AppIntroFragment.newInstance( getString( R.string.WELCOME_slide7_title ),
		                                        getString( R.string.WELCOME_slide7_description ),
		                                        R.drawable.welcome_07,
		                                        getColor( R.color.WELCOME_slide7_background ) ) );

		addSlide( AppIntroFragment.newInstance( getString( R.string.WELCOME_slide8_title ),
		                                        getString( R.string.WELCOME_slide8_description ),
		                                        R.drawable.welcome_08,
		                                        getColor( R.color.WELCOME_slide8_background ) ) );
	}

	@Override
	public void onDonePressed( Fragment currentFragment )
	{
		super.onDonePressed( currentFragment );

		startActivity( new Intent( this, SettingsActivity.class ) );
	}
}
