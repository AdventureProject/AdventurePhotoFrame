package com.darkrockstudios.iot.adventurephotoframe

import android.content.Context
import android.net.wifi.WifiManager
import android.os.Bundle
import android.view.View
import com.darkrockstudios.iot.adventurephotoframe.settings.SettingsBaseActivity
import com.google.android.things.device.DeviceManager
import com.google.android.things.update.StatusListener
import com.google.android.things.update.UpdateManager
import com.google.android.things.update.UpdateManagerStatus
import com.google.android.things.update.UpdatePolicy
import kotlinx.android.synthetic.main.activity_settings.*
import me.eugeniomarletti.extras.SimpleActivityCompanion


/**
 * Created by adamw on 12/11/2017.
 */
class SettingsActivity : SettingsBaseActivity()
{
	companion object : SimpleActivityCompanion(SettingsActivity::class)

	private val m_updateListener = StatusListener { status ->
		runOnUiThread {
			when (status.currentState)
			{
				UpdateManagerStatus.STATE_IDLE                 ->
				{
					SETTINGS_update_button.isEnabled = false
					SETTINGS_update_status.text = getString(R.string.SETTINGS_UPDATE_status_idle)
					SETTINGS_update_progress.visibility = View.GONE
				}
				UpdateManagerStatus.STATE_REPORTING_ERROR      ->
				{
					SETTINGS_update_button.isEnabled = false
					SETTINGS_update_status.text = getString(R.string.SETTINGS_UPDATE_status_error)
					SETTINGS_update_progress.visibility = View.GONE
				}
				UpdateManagerStatus.STATE_CHECKING_FOR_UPDATES ->
				{
					SETTINGS_update_button.isEnabled = false
					SETTINGS_update_status.text = getString(R.string.SETTINGS_UPDATE_status_checking)
					SETTINGS_update_progress.visibility = View.VISIBLE
				}
				UpdateManagerStatus.STATE_UPDATE_AVAILABLE     ->
				{
					SETTINGS_update_button.isEnabled = true
					SETTINGS_update_status.text = getString(R.string.SETTINGS_UPDATE_status_available)
					SETTINGS_update_progress.visibility = View.GONE
				}
				UpdateManagerStatus.STATE_DOWNLOADING_UPDATE   ->
				{
					SETTINGS_update_button.isEnabled = false
					SETTINGS_update_status.text = getString(R.string.SETTINGS_UPDATE_status_downloading)
					SETTINGS_update_progress.visibility = View.VISIBLE
				}
				UpdateManagerStatus.STATE_FINALIZING_UPDATE    ->
				{
					SETTINGS_update_button.isEnabled = false
					SETTINGS_update_status.text = getString(R.string.SETTINGS_UPDATE_status_finalizing)
					SETTINGS_update_progress.visibility = View.VISIBLE
				}
				UpdateManagerStatus.STATE_UPDATED_NEEDS_REBOOT ->
				{
					SETTINGS_update_button.isEnabled = false
					SETTINGS_update_status.text = getString(R.string.SETTINGS_UPDATE_status_reboot)
					SETTINGS_update_progress.visibility = View.GONE
				}
			}
		}
	}

	private val m_updateManager = UpdateManager.getInstance()
	private val m_deviceManager = DeviceManager.getInstance()

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)

		ensureWifiEnabled()

		SETTINGS_reboot_button.setOnClickListener { m_deviceManager.reboot() }
		SETTINGS_update_button.setOnClickListener { m_updateManager.performUpdateNow(UpdatePolicy.POLICY_APPLY_AND_REBOOT); }

		m_updateManager.addStatusListener(m_updateListener)
	}

	private fun ensureWifiEnabled()
	{
		val wifiManager = getSystemService(Context.WIFI_SERVICE) as WifiManager
		if (!wifiManager.isWifiEnabled)
		{
			wifiManager.isWifiEnabled = true
		}
	}

	override fun onResume()
	{
		super.onResume()

		checkForUpdate()
	}

	override fun onDestroy()
	{
		super.onDestroy()

		m_updateManager.removeStatusListener(m_updateListener)
	}

	private fun checkForUpdate()
	{
		SETTINGS_update_button.isEnabled = false
		SETTINGS_update_status.text = ""

		// Trigger an update check immediately
		m_updateManager.performUpdateNow(UpdatePolicy.POLICY_CHECKS_ONLY)
	}
}