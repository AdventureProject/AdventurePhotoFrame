package com.darkrockstudios.apps.adventurephotoframe;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

/**
 * Created by adamw on 12/22/2016.
 */

public class WifiListAdapter extends ArrayAdapter<ScanResult>
{
	public WifiListAdapter( Context context )
	{
		super( context, R.layout.wifi_list_item );
	}

	@Override
	@NonNull
	public View getView( int position, @Nullable View convertView, @NonNull ViewGroup parent)
	{
		ScanResult wifiNetwork = getItem( position );

		final WifiListItemViewHolder viewHolder;
		if( convertView == null )
		{
			convertView = LayoutInflater.from( getContext() ).inflate( R.layout.wifi_list_item, parent, false );
			viewHolder = new WifiListItemViewHolder( convertView );
			convertView.setTag( viewHolder );
		}
		else
		{
			viewHolder = (WifiListItemViewHolder) convertView.getTag();
		}

		viewHolder.bind( wifiNetwork );

		return convertView;
	}
}
