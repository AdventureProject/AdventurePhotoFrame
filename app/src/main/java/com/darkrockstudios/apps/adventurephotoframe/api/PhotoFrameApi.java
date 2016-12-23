package com.darkrockstudios.apps.adventurephotoframe.api;

import com.darkrockstudios.apps.adventurephotoframe.data.Photo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by adamw on 12/21/2016.
 */

public interface PhotoFrameApi
{
	@GET("photoframe/{photoFrameId}")
	Call<Photo> getPhoto( @Path("photoFrameId") int photoFrameId );
}
