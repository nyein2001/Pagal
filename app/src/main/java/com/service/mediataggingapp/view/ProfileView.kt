package com.service.mediataggingapp.view

import com.service.mediataggingapp.model.UserDetailsInfo

interface ProfileView {
    fun showLoading()
    fun hideLoading()
    fun setProfile(profileInfo: UserDetailsInfo)
    fun onErrorLoading(message: String?)
}