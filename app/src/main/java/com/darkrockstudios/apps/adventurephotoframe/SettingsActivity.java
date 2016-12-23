package com.darkrockstudios.apps.adventurephotoframe;

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
import android.util.Log;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class SettingsActivity extends BaseActivity
{
	private static final String TAG = SettingsActivity.class.getSimpleName();

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

		long currentFrequencyMs = Settings.getUpdateFrequency( this );
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
			ssid = wifiInfo.getSSID();
		}

		m_ssidView.setText( getString( R.string.SETTINGS_wifi_ssid, (ssid == null ? "none" : ssid) ) );
		m_statusView.setText( getString( R.string.SETTINGS_wifi_status, wifiInfo.getSupplicantState() ) );
	}

	@Override
	public int getContentLayout()
	{
		return R.layout.activity_settings;
	}

	@OnClick(R.id.SETTINGS_close)
	public void onClose()
	{
		finish();
	}

	@OnClick(R.id.DEBUG)
	public void onDebug()
	{
		startActivity( new Intent( this, DebugActivity.class ) );
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

		}
	}

	private class NetworkScanReceiver extends BroadcastReceiver
	{
		@Override
		public void onReceive( Context context, Intent intent )
		{
			if( intent.getAction().equals( WifiManager.SCAN_RESULTS_AVAILABLE_ACTION ) )
			{
				List<ScanResult> scanResults = m_wifiManager.getScanResults();
				m_avalibleNetworksAdapter.clear();
				m_avalibleNetworksAdapter.addAll( scanResults );
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
}
