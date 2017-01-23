package com.darkrockstudios.iot.adventurephotoframe.data;

import org.parceler.Parcel;

/**
 * Created by adamw on 12/21/2016.
 */

@Parcel
public class Photo
{
	public String title;
	public String description;
	public String date;
	public String image;
	public String url;
	public String location;

	@Override
	public String toString()
	{
		return title;
	}
}
