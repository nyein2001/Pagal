package com.service.mediataggingapp.utils

import android.content.Context
import android.net.ConnectivityManager


object NetworkCheck {
    fun isConnected(context: Context): Boolean {
        return try {
            val conn = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = conn.activeNetworkInfo
            if (networkInfo != null) {
                networkInfo.isConnected || networkInfo.isConnectedOrConnecting
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
}