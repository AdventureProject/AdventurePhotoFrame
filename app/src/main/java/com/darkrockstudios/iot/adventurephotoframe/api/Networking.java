package com.darkrockstudios.iot.adventurephotoframe.api;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by adamw on 12/21/2016.
 */

public class Networking
{
	public final OkHttpClient  m_httpClient;
	public final Retrofit      m_retrofit;
	public final PhotoFrameApi m_photoFrameService;

	public Networking()
	{
		m_httpClient = new OkHttpClient();

		m_retrofit = new Retrofit.Builder()
				             .baseUrl( "http://wethinkadventure.rocks/" )
				             .addConverterFactory( GsonConverterFactory.create() )
				             .client( m_httpClient )
				             .build();

		m_photoFrameService = m_retrofit.create( PhotoFrameApi.class );
	}
}
