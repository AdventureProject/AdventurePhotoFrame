package com.darkrockstudios.iot.adventurephotoframe.application

import android.content.Context
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.module.AppGlideModule


/**
 * Created by adamw on 11/30/2017.
 */

@GlideModule
class AdventureAppGlideModule : AppGlideModule()
{
	override fun applyOptions(context: Context, builder: GlideBuilder)
	{
		val diskCacheSizeBytes = 1024 * 1024 * 1024 * 1L // 1 GB
		builder.setDiskCache(InternalCacheDiskCacheFactory(context, diskCacheSizeBytes))
	}
}