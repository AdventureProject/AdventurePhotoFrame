package com.darkrockstudios.apps.adventurephotoframe;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.f2prateek.dart.InjectExtra;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by adamw on 12/28/2016.
 */

public class WifiConfigDialog extends BaseFragment
{
	public static final String ARG_WIFI_NETWORK = "ARG_WIFI_NETWORK";

	@InjectExtra(ARG_WIFI_NETWORK)
	ScanResult m_wifiNetwork;

	@BindView(R.id.WIFIDIALOG_title)
	TextView m_titleView;

	@BindView(R.id.WIFIDIALOG_password)
	EditText m_passwordView;

	public static WifiConfigDialog newInstance( ScanResult wifiNetwork )
	{
		Bundle args = new Bundle();
		args.putParcelable( ARG_WIFI_NETWORK, wifiNetwork );

		WifiConfigDialog fragment = new WifiConfigDialog();
		fragment.setArguments( args );
		return fragment;
	}

	@Override
	public void onViewCreated( View view, Bundle savedInstanceState )
	{
		super.onViewCreated( view, savedInstanceState );

		m_titleView.setText( getString( R.string.SETTINGS_WIFIDIALOG_title, m_wifiNetwork.SSID ) );
	}

	@Override
	protected int getResourceLayout()
	{
		return R.layout.wifi_config_dialog;
	}

	@OnClick(R.id.WIFIDIALOG_confirm)
	public void onConnectClick()
	{
		String password = m_passwordView.getText().toString();
		if( !TextUtils.isEmpty( password ) )
		{
			connectToWifi( m_wifiNetwork.SSID, password );
		}
	}

	private void connectToWifi( String ssid, String password )
	{
		WifiConfiguration conf = new WifiConfiguration();
		conf.SSID = "\"" + ssid + "\"";
		conf.preSharedKey = "\"" + password + "\"";
		//conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

		// Add the new Network to Android
		WifiManager wifiManager = (WifiManager) getActivity().getSystemService( Context.WIFI_SERVICE );
		wifiManager.addNetwork( conf );

		// Enable the newly added connection
		List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
		for( WifiConfiguration i : list )
		{
			if( i.SSID != null && i.SSID.equals( "\"" + ssid + "\"" ) )
			{
				wifiManager.disconnect();
				wifiManager.enableNetwork( i.networkId, true );
				wifiManager.reconnect();

				break;
			}
		}

		Toast.makeText( getActivity(),
		                getString( R.string.TOAST_wifi_set, ssid ),
		                Toast.LENGTH_SHORT )
		     .show();

		dismissAllowingStateLoss();
	}
}
