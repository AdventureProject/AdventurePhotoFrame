package com.darkrockstudios.iot.adventurephotoframe.api

import com.darkrockstudios.iot.adventurephotoframe.data.Photo
import com.darkrockstudios.iot.adventurephotoframe.data.RegistrationResponse
import retrofit2.Call
import retrofit2.http.*

/**
 * Created by adamw on 12/21/2016.
 */

interface PhotoFrameApi
{
	@GET("photoframe/{deviceId}")
	fun getPhoto(@Path("deviceId") deviceId: String): Call<Photo>

	@GET("photoframe/{deviceId}/{photoId}")
	fun getPhotoById(@Path("deviceId") deviceId: String, @Path("photoId") photoId: Long): Call<Photo>

	@FormUrlEncoded
	@POST("health/{photoFrameId}")
	fun healthCheckIn(@Path("photoFrameId") photoFrameId: Long,
					  @Query("deviceId") deviceId: String,
					  @Query("version") version: Int,
					  @Field("update_channel") updateChannel: String): Call<Void>

	@FormUrlEncoded
	@POST("photowallnfc/{deviceId}")
	fun photoWallNfc(@Path("deviceId") deviceId: String, @Field("registration_id") registrationId: String): Call<RegistrationResponse>
}
