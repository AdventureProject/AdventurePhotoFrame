package com.darkrockstudios.iot.adventurephotoframe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.darkrockstudios.iot.adventurephotoframe.Settings.getUpdateFrequency;

public class SettingsActivity extends BaseActivity
{
	private static final String TAG                    = SettingsActivity.class.getSimpleName();
	private static final String WIFI_CONFIG_DIALOG_TAG = "WIFI_CONFIG_DIALOG";

	@BindView(R.id.SETTINGS_ssid)
	TextView m_ssidView;

	@BindView(R.id.SETTINGS_status)
	TextView m_statusView;

	@BindView(R.id.SETTINGS_available_networks)
	ListView m_avalibleNetworksView;
	private WifiListAdapter m_avalibleNetworksAdapter;

	@BindView(R.id.SETTINGS_frequency_seekbar)
	SeekBar m_frequencySeekBar;

	@BindView(R.id.SETTINGS_frequency_display)
	TextView m_frequencyDisplayView;

	@BindView(R.id.SETTINGS_wifi_loading)
	View m_loadingContainer;

	private WifiReceiver        m_connectionReceiver;
	private NetworkScanReceiver m_scanReceiver;
	private WifiManager         m_wifiManager;

	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

		m_connectionReceiver = new WifiReceiver();
		m_scanReceiver = new NetworkScanReceiver();

		m_wifiManager = (WifiManager) getSystemService( Context.WIFI_SERVICE );

		m_avalibleNetworksAdapter = new WifiListAdapter( this );
		m_avalibleNetworksView.setAdapter( m_avalibleNetworksAdapter );
		m_avalibleNetworksView.setOnItemClickListener( new WifiItemClickListener() );

		long currentFrequencyMs = getUpdateFrequency( this );
		int currentFrequencyMinutes = (int) ((currentFrequencyMs / 1000L) / 60L);

		m_frequencySeekBar.setProgress( currentFrequencyMinutes - 1 );
		m_frequencyDisplayView.setText( getString( R.string.SETTINGS_frequency, currentFrequencyMinutes ) );

		m_frequencySeekBar.setOnSeekBarChangeListener( new FrequencyChangeListener() );

		updateWifiInfo();
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		registerReceiver( m_scanReceiver, new IntentFilter( WifiManager.SCAN_RESULTS_AVAILABLE_ACTION ) );
		registerReceiver( m_connectionReceiver, new IntentFilter( "android.net.wifi.STATE_CHANGE" ) );

		m_wifiManager.startScan();
	}

	@Override
	protected void onPause()
	{
		super.onPause();

		unregisterReceiver( m_scanReceiver );
		unregisterReceiver( m_connectionReceiver );
	}

	private void updateWifiInfo()
	{
		String ssid = null;

		WifiInfo wifiInfo;

		wifiInfo = m_wifiManager.getConnectionInfo();
		if( wifiInfo.getSupplicantState() == SupplicantState.COMPLETED )
		{
			if( wifiInfo.getHiddenSSID() )
			{

			}
			else
			{
				ssid = wifiInfo.getSSID();
			}
		}

		m_ssidView.setText( getString( R.string.SETTINGS_wifi_ssid, (ssid == null ? "none" : ssid) ) );

		int ipInt = m_wifiManager.getConnectionInfo().getIpAddress();
		if( ipInt != 0 )
		{
			String ip = Formatter.formatIpAddress( ipInt );
			m_statusView.setText( getString( R.string.SETTINGS_wifi_status, ip ) );
		}
		else
		{
			m_statusView.setText(
					getString( R.string.SETTINGS_wifi_status, m_wifiManager.getConnectionInfo().getSupplicantState() ) );
		}
	}

	@Override
	public int getContentLayout()
	{
		return R.layout.activity_settings;
	}

	@OnClick(R.id.DEBUG)
	public void onDebug()
	{
		startActivity( new Intent( this, DebugActivity.class ) );
	}

	@OnClick(R.id.SETTINGS_wifi_hidden_network)
	public void onHiddenNetwork()
	{
		WifiConfigDialog wifiConfigDialog = WifiConfigDialog.newInstance();
		wifiConfigDialog.show( getFragmentManager(), WIFI_CONFIG_DIALOG_TAG );
	}

	private void showWifi()
	{
		m_loadingContainer.setVisibility( View.GONE );
		m_avalibleNetworksView.setVisibility( View.VISIBLE );
	}

	private void hideWifi()
	{
		m_loadingContainer.setVisibility( View.VISIBLE );
		m_avalibleNetworksView.setVisibility( View.INVISIBLE );
	}

	private void updateBrightness()
	{
		try
		{
/*
			int br = android.provider.Settings.System.getInt( getContentResolver(),
			                                                  android.provider.Settings.System.SCREEN_BRIGHTNESS );
*/
			WindowManager.LayoutParams lp = getWindow().getAttributes();
			lp.screenBrightness = 0.01f;
			getWindow().setAttributes( lp );
		}
		catch( Exception ignored )
		{
		}
	}

	private class FrequencyChangeListener implements SeekBar.OnSeekBarChangeListener
	{
		@Override
		public void onProgressChanged( SeekBar seekBar, int progress, boolean fromUser )
		{
			int minutes = progress + 1;

			Settings.setUpdateFrequency( SettingsActivity.this, minutes );
			m_frequencyDisplayView.setText( getString( R.string.SETTINGS_frequency, minutes ) );
		}

		@Override
		public void onStartTrackingTouch( SeekBar seekBar )
		{

		}

		@Override
		public void onStopTrackingTouch( SeekBar seekBar )
		{
			int frequencyM = (int) (Settings.getUpdateFrequency( SettingsActivity.this ) / 1000L / 60L);

			Toast.makeText( SettingsActivity.this,
			                getString( R.string.TOAST_update_frequency_set, frequencyM ),
			                Toast.LENGTH_SHORT )
			     .show();
		}
	}

	private class NetworkScanReceiver extends BroadcastReceiver
	{
		@Override
		public void onReceive( Context context, Intent intent )
		{
			showWifi();

			if( intent.getAction().equals( WifiManager.SCAN_RESULTS_AVAILABLE_ACTION ) )
			{
				List<ScanResult> scanResults = m_wifiManager.getScanResults();
				m_avalibleNetworksAdapter.clear();
				for( ScanResult result : scanResults )
				{
					if( !TextUtils.isEmpty( result.SSID ) )
					{
						m_avalibleNetworksAdapter.add( result );
					}
				}
				m_avalibleNetworksAdapter.notifyDataSetChanged();
			}
		}
	}

	private class WifiReceiver extends BroadcastReceiver
	{
		@Override
		public void onReceive( Context context, Intent intent )
		{
			ConnectivityManager conMan = (ConnectivityManager) context.getSystemService( Context.CONNECTIVITY_SERVICE );
			NetworkInfo netInfo = conMan.getActiveNetworkInfo();
			if( netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI )
			{
				Log.i( TAG, " Wifi connected" );
			}
			else
			{
				Log.w( TAG, "No Wifi connection" );
			}

			updateWifiInfo();
		}
	}

	private class WifiItemClickListener implements AdapterView.OnItemClickListener
	{
		@Override
		public void onItemClick( AdapterView<?> parent, View view, int position, long id )
		{
			ScanResult wifiNetwork = m_avalibleNetworksAdapter.getItem( position );
			WifiConfigDialog wifiConfigDialog = WifiConfigDialog.newInstance( wifiNetwork );
			wifiConfigDialog.show( getFragmentManager(), WIFI_CONFIG_DIALOG_TAG );
		}
	}
}
