package com.darkrockstudios.iot.adventurephotoframe

import android.content.Context
import android.net.wifi.ScanResult
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.view.View
import android.view.Window
import android.widget.Toast
import com.github.vmironov.jetpack.arguments.bindOptionalArgument
import kotlinx.android.synthetic.main.wifi_config_dialog.*

/**
 * Created by adamw on 12/28/2016.
 */

class WifiConfigDialog : BaseFragment()
{
	companion object
	{
		fun newInstance(wifiNetwork: ScanResult?): WifiConfigDialog = WifiConfigDialog().apply {
			m_wifiNetwork = wifiNetwork
		}

		fun newInstance(): WifiConfigDialog = WifiConfigDialog().apply {
			m_wifiNetwork = null
		}
	}

	private var m_wifiNetwork: ScanResult? by bindOptionalArgument<ScanResult>()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?)
	{
		dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
		super.onViewCreated(view, savedInstanceState)

		WIFIDIALOG_confirm.setOnClickListener(this::onConnectClick)

		m_wifiNetwork?.let { wifiNetwork ->
			WIFIDIALOG_ssid.setText(wifiNetwork.SSID)
			WIFIDIALOG_ssid.inputType = InputType.TYPE_NULL
			WIFIDIALOG_ssid.isFocusable = false
		}
	}

	override val resourceLayout: Int
		get() = R.layout.wifi_config_dialog

	fun onConnectClick(view: View)
	{
		val password = WIFIDIALOG_password.text.toString()
		val ssid = WIFIDIALOG_ssid.text.toString()

		connectToWifi(ssid, password)
	}

	private fun connectToWifi(ssid: String, password: String)
	{
		val conf = WifiConfiguration()

		conf.SSID = "\"" + ssid + "\""
		conf.hiddenSSID = m_wifiNetwork == null

		if (TextUtils.isEmpty(password))
		{
			conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
		}
		else
		{
			conf.preSharedKey = "\"" + password + "\""
		}

		conf.status = WifiConfiguration.Status.ENABLED

		// Add the new Network to Android
		val wifiManager = activity.getSystemService(Context.WIFI_SERVICE) as WifiManager
		wifiManager.addNetwork(conf)

		// Enable the newly added connection
		val list = wifiManager.configuredNetworks
		for (i in list)
		{
			if (i.SSID != null && i.SSID == "\"" + ssid + "\"")
			{
				wifiManager.disconnect()
				wifiManager.enableNetwork(i.networkId, true)
				wifiManager.reconnect()

				break
			}
		}

		Toast.makeText(activity,
		               getString(R.string.TOAST_wifi_set, ssid),
		               Toast.LENGTH_SHORT)
				.show()

		dismissAllowingStateLoss()
	}
}
