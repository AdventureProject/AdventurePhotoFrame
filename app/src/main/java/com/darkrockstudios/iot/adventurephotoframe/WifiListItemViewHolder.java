package com.darkrockstudios.iot.adventurephotoframe;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
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

	@BindView(R.id.WIFI_ITEM_signal_strength)
	ProgressBar m_signalStrengthView;

	public WifiListItemViewHolder( View view )
	{
		ButterKnife.bind( this, view );
	}

	public void bind( ScanResult wifiNetwork )
	{
		if( !TextUtils.isEmpty( wifiNetwork.SSID ) )
		{
			m_ssidView.setText( wifiNetwork.SSID );
		}
		else
		{
			m_ssidView.setText( Html.fromHtml( m_ssidView.getContext().getString( R.string.SETTINGS_WIFI_ITEM_no_ssid ),
			                                   Html.FROM_HTML_MODE_LEGACY ) );
		}

		m_signalStrengthView.setProgress( WifiManager.calculateSignalLevel( wifiNetwork.level, 10 ) * 10 );
	}
}
