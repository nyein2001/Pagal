package com.service.mediataggingapp.utils

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlin.math.roundToInt


object Tools {

    fun displayImageOriginal(ctx: Context?, img: ImageView, url: String) {
        try {
            Glide.with(ctx!!).load(url)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(img)
        } catch (ignored: Exception) {
        }
    }

    fun getImageHeight(activity: Activity?): Int {
        val wRatio = 2f
        val hRatio = 1f
        val screenWidth: Int = Resources.getSystem().displayMetrics.widthPixels - 10
        val resHeight = screenWidth * hRatio / wRatio
        return resHeight.roundToInt()
    }


}