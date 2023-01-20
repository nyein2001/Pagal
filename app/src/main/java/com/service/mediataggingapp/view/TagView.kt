package com.service.mediataggingapp.view

import com.service.mediataggingapp.model.Hashtag

interface TagView {
    fun showLoading()
    fun hideLoading()
    fun setProject(hashtags: List<Hashtag>)
    fun onErrorLoading(message: String?)
}