package com.darkrockstudios.iot.adventurephotoframe.api

import com.darkrockstudios.iot.adventurephotoframe.data.Photo
import retrofit2.Call
import retrofit2.http.*

/**
 * Created by adamw on 12/21/2016.
 */

interface PhotoFrameApi
{
	@GET("photoframe/{photoFrameId}")
	fun getPhoto(@Path("photoFrameId") photoFrameId: Long): Call<Photo>

	@FormUrlEncoded
	@POST("health/{photoFrameId}")
	fun healthCheckIn(@Path("photoFrameId") photoFrameId: Long, @Query("version") version: Int,
	                  @Field("errors") errorLog: String): Call<Void>
}
