package com.darkrockstudios.iot.adventurephotoframe.settings

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.net.wifi.ScanResult
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.CompoundButton
import android.widget.Toast
import com.darkrockstudios.iot.adventurephotoframe.R
import com.darkrockstudios.iot.adventurephotoframe.base.BaseFragment
import com.github.vmironov.jetpack.arguments.bindOptionalArgument
import kotlinx.android.synthetic.main.wifi_config_dialog.*
import java.util.regex.Pattern


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

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)

		isCancelable = false
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?)
	{
		dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
		super.onViewCreated(view, savedInstanceState)

		WIFIDIALOG_close.setOnClickListener { dismissAllowingStateLoss() }
		WIFIDIALOG_confirm.setOnClickListener(this::onConnectClick)

		if (m_wifiNetwork != null)
		{
			WIFIDIALOG_info.setOnClickListener(this::onInfoClick)
			WIFIDIALOG_info.visibility = View.VISIBLE
		}
		else
		{
			WIFIDIALOG_info.visibility = View.GONE
		}

		WIFIDIALOG_show_password.setOnCheckedChangeListener(this::onShowPasswordChanged)

		m_wifiNetwork?.let { wifiNetwork ->
			WIFIDIALOG_ssid.setText(wifiNetwork.SSID)
			WIFIDIALOG_ssid.inputType = InputType.TYPE_NULL
			WIFIDIALOG_ssid.isFocusable = false
		}
	}

	override val resourceLayout: Int
		get() = R.layout.wifi_config_dialog

	private fun onInfoClick(view: View)
	{
		AlertDialog.Builder(context)
				.setIcon(R.drawable.ic_wifi_info)
				.setTitle(m_wifiNetwork?.SSID)
				.setMessage(m_wifiNetwork?.capabilities)
				.create().show()
	}

	private fun onShowPasswordChanged(buttonView: CompoundButton, isChecked: Boolean)
	{
		if (isChecked)
		{
			WIFIDIALOG_password.inputType = (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)
		}
		else
		{
			WIFIDIALOG_password.inputType = (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)
		}
	}

	private fun onConnectClick(view: View)
	{
		val password = WIFIDIALOG_password.text.toString()
		val ssid = WIFIDIALOG_ssid.text.toString()

		if (!ssid.trim().isEmpty())
		{
			connectToWifi(ssid, password)
		}
	}

	private fun createCommonNetworkConfig(ssid: String, isHidden: Boolean): WifiConfiguration
	{
		val config = WifiConfiguration()
		config.allowedAuthAlgorithms.clear()
		config.allowedGroupCiphers.clear()
		config.allowedKeyManagement.clear()
		config.allowedPairwiseCiphers.clear()
		config.allowedProtocols.clear()
		// Android API insists that an ascii SSID must be quoted to be correctly handled.
		config.SSID = quoteNonHex(ssid)
		config.hiddenSSID = isHidden
		return config
	}

	private fun createWpa2Config(ssid: String, password: String, isHidden: Boolean): WifiConfiguration
	{
		val config = createCommonNetworkConfig(ssid, isHidden)
		// Hex passwords that are 64 bits long are not to be quoted.
		config.preSharedKey = quoteNonHex(password, 64)
		config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN)
		config.allowedProtocols.set(WifiConfiguration.Protocol.WPA) // For WPA
		config.allowedProtocols.set(WifiConfiguration.Protocol.RSN) // For WPA2
		config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
		config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP)
		config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
		config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)
		config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
		config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)

		return config
	}

	private fun quoteNonHex(value: String, vararg allowedLengths: Int): String?
	{
		return if (isHexOfLength(value, *allowedLengths)) value else convertToQuotedString(value)
	}

	/**
	 * Encloses the incoming string inside double quotes, if it isn't already quoted.
	 * @param s the input string
	 * @return a quoted string, of the form "input".  If the input string is null, it returns null
	 * as well.
	 */
	private fun convertToQuotedString(s: String?): String?
	{
		if (s == null || s.isEmpty())
		{
			return null
		}
		// If already quoted, return as-is
		return if (s[0] == '"' && s[s.length - 1] == '"')
		{
			s
		}
		else '\"' + s + '\"'
	}

	private val HEX_DIGITS = Pattern.compile("[0-9A-Fa-f]+")

	/**
	 * @param value input to check
	 * @param allowedLengths allowed lengths, if any
	 * @return true if value is a non-null, non-empty string of hex digits, and if allowed lengths are given, has
	 * an allowed length
	 */
	private fun isHexOfLength(value: CharSequence?, vararg allowedLengths: Int): Boolean
	{
		if (value == null || !HEX_DIGITS.matcher(value).matches())
		{
			return false
		}
		if (allowedLengths.isEmpty())
		{
			return true
		}
		for (length in allowedLengths)
		{
			if (value.length == length)
			{
				return true
			}
		}
		return false
	}

	private fun findNetworkInExistingConfig(wifiManager: WifiManager, ssid: String): Int?
	{
		val existingConfigs = wifiManager.configuredNetworks
		if (existingConfigs != null)
		{
			for (existingConfig in existingConfigs)
			{
				val existingSSID = existingConfig.SSID
				if (existingSSID != null && existingSSID == ssid)
				{
					return existingConfig.networkId
				}
			}
		}
		return null
	}

	private fun connectToWifi(ssid: String, password: String)
	{
		val config = createWpa2Config(ssid, password, (m_wifiNetwork == null))

		// Add the new Network to Android
		val wifiManager = activity.getSystemService(Context.WIFI_SERVICE) as WifiManager

		val foundNetworkID = findNetworkInExistingConfig(wifiManager, config.SSID)
		if (foundNetworkID != null)
		{
			Log.i(TAG, "Removing old configuration for network " + config.SSID)
			wifiManager.removeNetwork(foundNetworkID)
		}

		val networkId = wifiManager.addNetwork(config)
		if (networkId >= 0)
		{
			// Try to disable the current network and start a new one.
			if (wifiManager.enableNetwork(networkId, true))
			{
				Log.i(TAG, "Associating to network " + config.SSID)
			}
			else
			{
				Log.w(TAG, "Failed to enable network " + config.SSID)
			}
		}
		else
		{
			Log.w(TAG, "Unable to add network " + config.SSID)
		}

		Toast.makeText(activity,
					   getString(R.string.TOAST_wifi_set, ssid),
					   Toast.LENGTH_SHORT)
				.show()

		dismissAllowingStateLoss()
	}
}
