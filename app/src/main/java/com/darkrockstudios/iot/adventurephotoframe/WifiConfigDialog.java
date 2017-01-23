package com.darkrockstudios.iot.adventurephotoframe;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
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

	@Nullable
	@InjectExtra(ARG_WIFI_NETWORK)
	ScanResult m_wifiNetwork;

	@BindView(R.id.WIFIDIALOG_ssid)
	EditText m_ssidView;

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

	public static WifiConfigDialog newInstance()
	{
		Bundle args = new Bundle();

		WifiConfigDialog fragment = new WifiConfigDialog();
		fragment.setArguments( args );
		return fragment;
	}

	@Override
	public void onViewCreated( View view, Bundle savedInstanceState )
	{
		getDialog().getWindow().requestFeature( Window.FEATURE_NO_TITLE );
		super.onViewCreated( view, savedInstanceState );

		if( m_wifiNetwork != null )
		{
			m_ssidView.setText( m_wifiNetwork.SSID );
			m_ssidView.setInputType( InputType.TYPE_NULL );
			m_ssidView.setFocusable( false );
		}
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
		String ssid = m_ssidView.getText().toString();
		if( !TextUtils.isEmpty( password ) )
		{
			connectToWifi( ssid, password );
		}
	}

	private void connectToWifi( String ssid, String password )
	{
		WifiConfiguration conf = new WifiConfiguration();

		conf.SSID = "\"" + ssid + "\"";
		conf.hiddenSSID = (m_wifiNetwork == null);

		conf.preSharedKey = "\"" + password + "\"";
		//conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

		conf.status = WifiConfiguration.Status.ENABLED;

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