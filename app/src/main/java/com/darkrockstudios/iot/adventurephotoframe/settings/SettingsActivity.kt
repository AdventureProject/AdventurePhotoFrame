package com.darkrockstudios.iot.adventurephotoframe.settings

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.wifi.SupplicantState
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.text.TextUtils
import android.text.format.Formatter
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.SeekBar
import android.widget.Toast
import com.darkrockstudios.iot.adventurephotoframe.DebugActivity
import com.darkrockstudios.iot.adventurephotoframe.R
import com.darkrockstudios.iot.adventurephotoframe.base.BaseActivity
import com.google.android.things.device.DeviceManager
import com.google.android.things.update.StatusListener
import com.google.android.things.update.UpdateManager
import com.google.android.things.update.UpdateManagerStatus
import kotlinx.android.synthetic.main.activity_settings.*
import me.eugeniomarletti.extras.SimpleActivityCompanion


class SettingsActivity : BaseActivity(), SwipeRefreshLayout.OnRefreshListener
{
	companion object : SimpleActivityCompanion(SettingsActivity::class)
	{
		private val TAG = SettingsActivity::class.java.simpleName
		private val WIFI_CONFIG_DIALOG_TAG = "WIFI_CONFIG_DIALOG"
	}

	private lateinit var m_avalibleNetworksAdapter: WifiListAdapter

	private lateinit var m_connectionReceiver: WifiReceiver
	private lateinit var m_scanReceiver: NetworkScanReceiver
	private lateinit var m_wifiManager: WifiManager

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

	private val m_updateManager = UpdateManager()
	private val m_deviceManager = DeviceManager()

	public override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)

		m_connectionReceiver = WifiReceiver()
		m_scanReceiver = NetworkScanReceiver()

		m_wifiManager = getSystemService(Context.WIFI_SERVICE) as WifiManager

		m_avalibleNetworksAdapter = WifiListAdapter(this)
		SETTINGS_available_networks.adapter = m_avalibleNetworksAdapter
		SETTINGS_available_networks.onItemClickListener = WifiItemClickListener()

		val currentFrequencyMs = Settings.getUpdateFrequency(this)
		val currentFrequencyMinutes = (currentFrequencyMs / 1000L / 60L).toInt()

		SETTINGS_frequency_seekbar.progress = currentFrequencyMinutes - 1
		SETTINGS_frequency_display.text = getString(R.string.SETTINGS_frequency, currentFrequencyMinutes)

		SETTINGS_frequency_seekbar.setOnSeekBarChangeListener(FrequencyChangeListener())

		updateWifiInfo()

		SETTINGS_available_networks_refresh.setOnRefreshListener(this)

		SETTINGS_wifi_hidden_network.setOnClickListener(this::onHiddenNetwork)
		//DEBUG.setOnClickListener(this::onDebug)

		SETTINGS_reboot_button.setOnClickListener { m_deviceManager.reboot() }
		SETTINGS_update_button.setOnClickListener { m_updateManager.performUpdateNow(UpdateManager.POLICY_APPLY_AND_REBOOT); }

		m_updateManager.addStatusListener(m_updateListener)
	}

	private fun checkForUpdate()
	{
		SETTINGS_update_button.isEnabled = false
		SETTINGS_update_status.text = ""

		// Trigger an update check immediately
		m_updateManager.performUpdateNow(UpdateManager.POLICY_CHECKS_ONLY)
	}

	override fun onResume()
	{
		super.onResume()

		registerReceiver(m_scanReceiver, IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
		registerReceiver(m_connectionReceiver, IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION))

		m_wifiManager.startScan()

		checkForUpdate()

		/*
		if (BuildConfig.DEBUG)
		{
			DEBUG.visibility = View.VISIBLE
		}
		else
		{
			DEBUG.visibility = View.GONE
		}
		*/
	}

	override fun onDestroy()
	{
		super.onDestroy()

		m_updateManager.removeStatusListener(m_updateListener)
	}

	override fun onPause()
	{
		super.onPause()

		unregisterReceiver(m_scanReceiver)
		unregisterReceiver(m_connectionReceiver)
	}

	private fun updateWifiInfo()
	{
		var ssid: String? = null

		val wifiInfo: WifiInfo

		wifiInfo = m_wifiManager.connectionInfo
		if (wifiInfo.supplicantState == SupplicantState.COMPLETED)
		{
			if (wifiInfo.hiddenSSID)
			{

			}
			else
			{
				ssid = wifiInfo.ssid
			}
		}

		SETTINGS_ssid.text = getString(R.string.SETTINGS_wifi_ssid, ssid ?: "none")

		val ipInt = m_wifiManager.connectionInfo.ipAddress
		if (ipInt != 0)
		{
			val ip = Formatter.formatIpAddress(ipInt)
			SETTINGS_status.text = getString(R.string.SETTINGS_wifi_status, ip)
		}
		else
		{
			SETTINGS_status.text = getString(R.string.SETTINGS_wifi_status, m_wifiManager.connectionInfo.supplicantState)
		}
	}

	override val contentLayout: Int
		get() = R.layout.activity_settings

	fun onDebug(view: View)
	{
		startActivity(Intent(this, DebugActivity::class.java))
	}

	fun onHiddenNetwork(view: View)
	{
		val wifiConfigDialog = WifiConfigDialog.newInstance()
		wifiConfigDialog.show(fragmentManager, WIFI_CONFIG_DIALOG_TAG)
	}

	private fun showWifi()
	{
		SETTINGS_wifi_loading.visibility = View.GONE
		SETTINGS_available_networks.visibility = View.VISIBLE
	}

	private fun hideWifi()
	{
		SETTINGS_wifi_loading.visibility = View.VISIBLE
		SETTINGS_available_networks.visibility = View.INVISIBLE
	}

	private fun updateBrightness()
	{
		try
		{
			/*
			int br = android.provider.Settings.System.getInt( getContentResolver(),
			                                                  android.provider.Settings.System.SCREEN_BRIGHTNESS );
*/
			val lp = window.attributes
			lp.screenBrightness = 0.01f
			window.attributes = lp
		}
		catch (ignored: Exception)
		{
		}

	}

	override fun onRefresh()
	{
		SETTINGS_available_networks_refresh.isRefreshing = false

		m_avalibleNetworksAdapter.clear()
		m_avalibleNetworksAdapter.notifyDataSetChanged()

		hideWifi()

		m_wifiManager.startScan()
	}

	private inner class FrequencyChangeListener : SeekBar.OnSeekBarChangeListener
	{
		override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean)
		{
			val minutes = progress + 1

			Settings.setUpdateFrequency(this@SettingsActivity, minutes.toLong())
			SETTINGS_frequency_display.text = getString(R.string.SETTINGS_frequency, minutes)
		}

		override fun onStartTrackingTouch(seekBar: SeekBar)
		{

		}

		override fun onStopTrackingTouch(seekBar: SeekBar)
		{
			val frequencyM = (Settings.getUpdateFrequency(this@SettingsActivity) / 1000L / 60L).toInt()

			Toast.makeText(this@SettingsActivity,
						   getString(R.string.TOAST_update_frequency_set, frequencyM),
						   Toast.LENGTH_SHORT)
					.show()
		}
	}

	private inner class NetworkScanReceiver : BroadcastReceiver()
	{
		override fun onReceive(context: Context, intent: Intent)
		{
			showWifi()

			if (intent.action == WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
			{
				val scanResults = m_wifiManager.scanResults
				m_avalibleNetworksAdapter.clear()
				for (result in scanResults)
				{
					if (!TextUtils.isEmpty(result.SSID))
					{
						m_avalibleNetworksAdapter.add(result)
					}
				}
				m_avalibleNetworksAdapter.notifyDataSetChanged()
			}
		}
	}

	private inner class WifiReceiver : BroadcastReceiver()
	{
		override fun onReceive(context: Context, intent: Intent)
		{
			val conMan = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
			val netInfo = conMan.activeNetworkInfo
			if (netInfo != null && netInfo.type == ConnectivityManager.TYPE_WIFI)
			{
				Log.i(TAG, " Wifi connected")
			}
			else
			{
				Log.w(TAG, "No Wifi connection")
			}

			updateWifiInfo()
		}
	}

	private inner class WifiItemClickListener : AdapterView.OnItemClickListener
	{
		override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long)
		{
			val wifiNetwork = m_avalibleNetworksAdapter.getItem(position)
			val wifiConfigDialog = WifiConfigDialog.newInstance(wifiNetwork)
			wifiConfigDialog.show(fragmentManager, WIFI_CONFIG_DIALOG_TAG)
		}
	}
}
