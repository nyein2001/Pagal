package com.service.mediataggingapp.view

import com.service.mediataggingapp.model.ItemInfo

interface ItemView {
    fun showLoading()
    fun hideLoading()
    fun setProject(items: List<ItemInfo>)
    fun onErrorLoading(message: String?)
}