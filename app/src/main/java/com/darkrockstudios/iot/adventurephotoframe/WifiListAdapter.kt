package com.darkrockstudios.iot.adventurephotoframe

import android.content.Context
import android.net.wifi.ScanResult
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter

/**
 * Created by adamw on 12/22/2016.
 */

class WifiListAdapter(context: Context) : ArrayAdapter<ScanResult>(context, R.layout.wifi_list_item)
{
	override fun getView(position: Int, convertView: View?, parent: ViewGroup): View
	{
		var convertViewResolved = convertView
		val wifiNetwork = getItem(position)

		val viewHolder: WifiListItemViewHolder
		if (convertViewResolved == null)
		{
			convertViewResolved = LayoutInflater.from(context).inflate(R.layout.wifi_list_item, parent, false)
			viewHolder = WifiListItemViewHolder(convertViewResolved)
			convertViewResolved!!.tag = viewHolder
		}
		else
		{
			viewHolder = convertViewResolved.tag as WifiListItemViewHolder
		}

		viewHolder.bind(wifiNetwork)

		return convertViewResolved
	}
}
