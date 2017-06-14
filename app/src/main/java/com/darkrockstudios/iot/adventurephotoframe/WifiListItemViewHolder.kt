package com.darkrockstudios.iot.adventurephotoframe

import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.text.Html
import android.text.TextUtils
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView

/**
 * Created by adamw on 12/22/2016.
 */

class WifiListItemViewHolder(view: View)
{
	val m_ssidView: TextView = view.findViewById(R.id.WIFI_ITEM_ssid)
	val m_signalStrengthView: ProgressBar = view.findViewById(R.id.WIFI_ITEM_signal_strength)

	fun bind(wifiNetwork: ScanResult)
	{
		if (!TextUtils.isEmpty(wifiNetwork.SSID))
		{
			m_ssidView.text = wifiNetwork.SSID
		}
		else
		{
			m_ssidView.text = Html.fromHtml(m_ssidView!!.context.getString(R.string.SETTINGS_WIFI_ITEM_no_ssid),
			                                  Html.FROM_HTML_MODE_LEGACY)
		}

		m_signalStrengthView.progress = WifiManager.calculateSignalLevel(wifiNetwork.level, 10) * 10
	}
}
