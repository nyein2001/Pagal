package com.service.mediataggingapp.view

import com.service.mediataggingapp.model.Hashtag

interface TagView {
    fun showLoading()
    fun hideLoading()
    fun setTags(hashtags: List<Hashtag>)
    fun onErrorLoading(message: String?)
}