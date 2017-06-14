package com.darkrockstudios.iot.adventurephotoframe.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by adamw on 12/21/2016.
 */

class Networking
{
	val m_httpClient: OkHttpClient
	val m_retrofit: Retrofit
	val m_photoFrameService: PhotoFrameApi

	init
	{
		m_httpClient = OkHttpClient().newBuilder().retryOnConnectionFailure(true).build()

		m_retrofit = Retrofit.Builder()
				.baseUrl("http://wethinkadventure.rocks/")
				.addConverterFactory(GsonConverterFactory.create())
				.client(m_httpClient)
				.build()

		m_photoFrameService = m_retrofit.create(PhotoFrameApi::class.java)
	}
}
