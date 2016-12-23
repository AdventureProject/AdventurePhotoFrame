package com.darkrockstudios.apps.adventurephotoframe;

import android.net.wifi.ScanResult;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by adamw on 12/22/2016.
 */

public class WifiListItemViewHolder
{
	@BindView(R.id.WIFI_ITEM_ssid)
	TextView m_ssidView;

	@BindView(R.id.WIFI_ITEM_security)
	TextView m_securityView;

	public WifiListItemViewHolder( View view )
	{
		ButterKnife.bind( this, view );
	}

	public void bind( ScanResult wifiNetwork )
	{
		m_ssidView.setText( wifiNetwork.SSID );
		m_securityView.setText( wifiNetwork.capabilities );
	}
}
