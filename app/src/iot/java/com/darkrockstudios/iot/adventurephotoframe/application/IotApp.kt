package com.darkrockstudios.iot.adventurephotoframe.application

import com.darkrockstudios.iot.adventurephotoframe.BuildConfig
import com.google.android.things.update.UpdateManager
import com.google.android.things.update.UpdatePolicy
import java.util.concurrent.TimeUnit

/**
 * Created by adamw on 12/11/2017.
 */
class IotApp : App()
{
	override fun onCreate()
	{
		super.onCreate()

		if (!BuildConfig.DEBUG)
		{
			setupUpdateMonitoring()
		}
	}

	private fun setupUpdateMonitoring()
	{
		val manager = UpdateManager.getInstance()

		val updatePolicy = UpdatePolicy.Builder().setPolicy(UpdateManager.POLICY_APPLY_AND_REBOOT)
				.setApplyDeadline(1, TimeUnit.HOURS)
				.build()
		manager.setPolicy(updatePolicy)
	}
}