package com.nemodroid.weatherapp.utils.binding

import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import coil.annotation.ExperimentalCoilApi
import coil.disk.DiskCache
import coil.imageLoader
import coil.memory.MemoryCache
import coil.request.ImageRequest
import coil.size.Precision
import coil.size.Scale
import coil.size.ViewSizeResolver

object ImageView {

    @OptIn(ExperimentalCoilApi::class)
    @JvmStatic
    @BindingAdapter(value = ["imageUrl"], requireAll = false)
    fun setImageUrl(image: AppCompatImageView, link: String?) {
        val imageLoader = image.context.imageLoader
        imageLoader.memoryCache?.apply {
            MemoryCache.Builder(image.context)
                .maxSizePercent(0.25)
                .build()
        }

        imageLoader.diskCache?.apply {
            DiskCache.Builder()
                .directory(image.context.cacheDir.resolve("image_cache"))
                .maxSizePercent(0.02)
                .build()
        }
        val request = ImageRequest.Builder(image.context)
            .data(link)
            .crossfade(true)
            .allowHardware(false)
            .size(ViewSizeResolver(image))
            .precision(Precision.EXACT)
            .target(image)
            .build()
        imageLoader.enqueue(request)
    }
}